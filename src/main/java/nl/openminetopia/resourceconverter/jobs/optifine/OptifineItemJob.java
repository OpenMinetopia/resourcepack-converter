package nl.openminetopia.resourceconverter.jobs.optifine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

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

        try (Stream<Path> paths = Files.walk(optifineDir.toPath())) {
            paths.filter(Files::isRegularFile).forEach(path -> processFile(path.toFile(), optifineDir));
        }
    }

    @SneakyThrows
    private void processFile(File file, File baseDir) {
        String relativePath = baseDir.toPath().relativize(file.toPath()).toString().replace(" ", "_");

        if (file.getName().endsWith(".png")) {
            copyTexture(file, new File(Main.OUTPUT_DIR, "assets/minecraft/textures/item/custom/" + relativePath).getPath());
        } else if (file.getName().endsWith(".json")) {
            copyModel(file, new File(Main.OUTPUT_DIR, "assets/minecraft/models/item/custom/" + relativePath).getPath());
            createItemsFile(new File(Main.OUTPUT_DIR, "assets/minecraft/items/custom/" + relativePath).getPath());
        }
    }

    @SneakyThrows
    private void copyTexture(File texture, String outputPath) {
//        BufferedImage bimg = ImageIO.read(texture);
//        if (bimg.getWidth() >= 256 || bimg.getHeight() >= 256) {
//            System.out.println("Texture " + texture.getName() + " is too large, skipping...");
//            return;
//        }
        FileUtils.copyFile(texture, new File(outputPath));
    }

    @SneakyThrows
    private void copyModel(File model, String outputPath) {
        String content = FileUtils.readFileToString(model, StandardCharsets.UTF_8).replace("\ufeff", "");
        ObjectNode modelNode = (ObjectNode) objectMapper.readTree(content);

        if (modelNode.has("parent")) {
            String parent = modelNode.get("parent").asText().replace("./", "");
            String parentPath = outputPath.replaceFirst("/[^/]+$", "") + "/" + parent;
            modelNode.put("parent", updatePath(parentPath));
        }

        ObjectNode textures = modelNode.withObject("textures");
        Iterator<String> fieldNames = textures.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            String texture = textures.get(fieldName).asText().replace("./", "");
            if (texture.startsWith("block/")) {
                textures.put(fieldName, texture.replace("block/", "minecraft:block/"));
                continue;
            }
            String texturePath = outputPath.replaceFirst("/[^/]+$", "") + "/" + texture;
            textures.put(fieldName, updatePath(texturePath));
        }

        FileUtils.writeStringToFile(new File(outputPath), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(modelNode), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private void createItemsFile(String outputPath) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode modelNode = rootNode.putObject("model");
        modelNode.put("type", "minecraft:model");
        modelNode.put("model", updatePath(outputPath));
        FileUtils.writeStringToFile(new File(outputPath), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), StandardCharsets.UTF_8);
    }

    private String updatePath(String path) {
        path = path.replace(" ", "_").replace(".json", "");

        if (path.contains("/custom/")) {
            return "minecraft:item" + path.substring(path.indexOf("/custom/"));
        }

        return "minecraft:item/custom/" + path.substring(path.lastIndexOf("/") + 1);
    }

}