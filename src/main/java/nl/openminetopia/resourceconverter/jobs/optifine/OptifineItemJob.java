package nl.openminetopia.resourceconverter.jobs.optifine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;

public class OptifineItemJob extends ConversionJob {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public void run() {
        System.out.println("Running OptifineItemJob");

        File optifineDir = new File(Main.OPTIFINE_DIR, "cit/items");

        if (!optifineDir.exists() || !optifineDir.isDirectory()) {
            System.out.println("No items found in Optifine directory, skipping...");
            return;
        }

        Files.walk(optifineDir.toPath())
                .filter(path -> !path.toFile().isDirectory())
                .forEach(path -> {
                    File file = path.toFile();
                    String itemName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    if (itemName.contains(" ")) itemName = itemName.replace(" ", "_");

                    String parentName = file.getParentFile().getName();
                    if (parentName.contains(" ")) parentName = parentName.replace(" ", "_");

                    if (file.getName().endsWith(".png")) {
                        copyTexture(file);
                    } else if (file.getName().endsWith(".json")) {
                        copyModel(file);
                        createItemsFile(parentName, itemName);
                    }
                });

        // loop through every directory in every subdirectory in assets/minecraft/optifine/cit/item
    }

    @SneakyThrows
    private void copyTexture(File texture) {
        String textureName = texture.getName();
        if (textureName.contains(" ")) textureName = textureName.replace(" ", "_");

        String parentName = texture.getParentFile().getName();
        if (parentName.contains(" ")) parentName = parentName.replace(" ", "_");

        BufferedImage bimg = ImageIO.read(texture);
        int width = bimg.getWidth();
        int height = bimg.getHeight();

        if (width >= 256 || height >= 256) {
            System.out.println("Texture " + texture.getName() + " is too large, skipping...");
            textureName = textureName + "_large";
        }

        // copy the texture to the output directory
        FileUtils.copyFile(texture, new File(Main.OUTPUT_DIR, "assets/minecraft/textures/item/custom/" + parentName + "/" + textureName));
    }

    @SneakyThrows
    private void copyModel(File model) {
        String modelName = model.getName();
        if (modelName.contains(" ")) modelName = modelName.replace(" ", "_");

        String parentName = model.getParentFile().getName();
        if (parentName.contains(" ")) parentName = parentName.replace(" ", "_");

        // create the equipment file
        String content = FileUtils.readFileToString(model, StandardCharsets.UTF_8);
        if (content.charAt(0) == '\ufeff') {
            content = content.substring(1);
        }
        ObjectNode modelNode = (ObjectNode) objectMapper.readTree(content);

        // modify parent model path if necessary
        if (modelNode.has("parent")) {
            String parent = modelNode.get("parent").asText();

            String[] split = parent.split("/");

            if (split.length > 1) {
                parent = split[split.length - 1];
            }
            parent = parent.replace(" ", "_").replace(".json", "");
            modelNode.put("parent", "minecraft:item/custom/" + parentName + "/" + parent);
        }

        Iterator<String> fieldNames = modelNode.withObject("textures").fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            String value = modelNode.withObject("textures").get(fieldName).asText();

            if (value.contains(" ")) value = value.replace(" ", "_");

            String[] split = value.split("/");
            if (split.length > 1) {
                value = split[split.length - 1];
            }

            modelNode.withObject("textures").put(fieldName, "minecraft:item/custom/" + parentName + "/" + value);
        }

        File destination = new File(Main.OUTPUT_DIR, "assets/minecraft/models/item/custom/" + parentName + "/" + modelName);
        FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(modelNode), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public void createItemsFile(String parentName, String itemName) {
        System.out.println("Creating custom item file for: " + itemName);

        // Root JSON node
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode modelNode = rootNode.putObject("model");
        modelNode.put("type", "minecraft:model");
        modelNode.put("model", "minecraft:item/custom/" + parentName + "/" + itemName);

        // Ensure the file is written correctly
        File destination = new File(Main.OUTPUT_DIR, "assets/minecraft/items/custom/" + parentName + "/" + itemName + ".json");
        FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), StandardCharsets.UTF_8);
    }
}
