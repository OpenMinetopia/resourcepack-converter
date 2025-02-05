package nl.openminetopia.resourceconverter.jobs.optifine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OptifineArmorJob extends ConversionJob {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public void run() {
        System.out.println("Running OptifineArmorJob");

        // loop through every directory in every subdirectory in assets/minecraft/optifine/cit/armor
        for (File parentArmorDir : new File(Main.OPTIFINE_DIR, "cit/armor").listFiles()) {
            if (!parentArmorDir.isDirectory()) continue;

            for (File armorDir : parentArmorDir.listFiles()) {
                if (!armorDir.isDirectory()) continue;
                String armorName = armorDir.getName();
                System.out.println("Found armor: " + armorName);
                createEquipmentFile(armorDir);
                createItemsArmorFiles(armorDir);
                createModelsItemArmorFiles(armorDir);

                for (File armorTexture : armorDir.listFiles()) {
                    if (!armorTexture.isFile()) continue;
                    String textureName = armorTexture.getName();

                    System.out.println("Found texture: " + textureName);

                    if (textureName.endsWith("_layer_1.png")) {
                        copyLayer1(armorTexture);
                        continue;
                    }

                    if (textureName.endsWith("_layer_2.png")) {
                        copyLayer2(armorTexture);
                        continue;
                    }

                    if (textureName.endsWith("_boots_icon.png")) {
                        copyIcon(armorTexture, "boots");
                        continue;
                    }

                    if (textureName.endsWith("_chestplate_icon.png")) {
                        copyIcon(armorTexture, "chestplate");
                        continue;
                    }

                    if (textureName.endsWith("_helmet_icon.png")) {
                        copyIcon(armorTexture, "helmet");
                        continue;
                    }

                    if (textureName.endsWith("_leggings_icon.png")) {
                        copyIcon(armorTexture, "leggings");
                        continue;
                    }


                    // copy the file to the output directory
                    //FileUtils.copyFile(armorTexture, new File(Main.OUTPUT_DIR, "assets/minecraft/optifine/cit/armor/" + armorName + "/" + textureName));
                }
            }
        }
    }

    @SneakyThrows
    private void copyLayer1(File source) {
        String armorName = source.getParentFile().getName() + ".png";

        System.out.println("Copying layer 1: " + armorName);
        // copy the file to the output directory
        FileUtils.copyFile(source, new File(Main.OUTPUT_DIR, "assets/minecraft/textures/entity/equipment/humanoid/" + armorName));
    }

    @SneakyThrows
    private void copyLayer2(File source) {
        String armorName = source.getParentFile().getName() + ".png";

        System.out.println("Copying layer 2: " + armorName);
        // copy the file to the output directory
        FileUtils.copyFile(source, new File(Main.OUTPUT_DIR, "assets/minecraft/textures/entity/equipment/humanoid_leggings/" + armorName));
    }

    @SneakyThrows
    private void copyIcon(File source, String type) {
        String armorName = source.getParentFile().getName();

        // copy the file to the output directory
        FileUtils.copyFile(source, new File(Main.OUTPUT_DIR, "assets/minecraft/textures/item/armor/" + armorName + "/" + type + ".png"));
    }

    @SneakyThrows
    public void createEquipmentFile(File source) {
        String armorName = source.getName();
        System.out.println("Creating equipment file for: " + armorName);

        // Root JSON node
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode layersNode = rootNode.putObject("layers");

        layersNode.putArray("humanoid")
                .add(objectMapper.createObjectNode().put("texture", "minecraft:" + armorName));

        layersNode.putArray("humanoid_leggings")
                .add(objectMapper.createObjectNode().put("texture", "minecraft:" + armorName));

        // Ensure the file is written correctly
        File destination = new File(Main.OUTPUT_DIR, "assets/minecraft/equipment/" + armorName + ".json");
        FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public void createItemsArmorFiles(File source) {
        String armorName = source.getName();
        System.out.println("Creating item armor file for: " + armorName);

        List<String> armorTypes = List.of("helmet", "chestplate", "leggings", "boots");
        for (String armorType : armorTypes) {
            // Root JSON node
            ObjectNode rootNode = objectMapper.createObjectNode();
            ObjectNode modelNode = rootNode.putObject("model");
            modelNode.put("type", "minecraft:model");
            modelNode.put("model", "minecraft:item/armor/" + armorName + "/" + armorType);

            // Ensure the file is written correctly
            File destination = new File(Main.OUTPUT_DIR, "assets/minecraft/items/armor/" + armorName + "/" + armorType + ".json");
            FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    public void createModelsItemArmorFiles(File source) {
        String armorName = source.getName();
        System.out.println("Creating models item armor file for: " + armorName);

        List<String> armorTypes = List.of("helmet", "chestplate", "leggings", "boots");
        for (String armorType : armorTypes) {
            // Root JSON node
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("parent", "minecraft:item/generated");

            ObjectNode textures = rootNode.putObject("textures");
            textures.put("layer0", "minecraft:item/armor/" + armorName + "/" + armorType);

            // Ensure the file is written correctly
            File destination = new File(Main.OUTPUT_DIR, "assets/minecraft/models/item/armor/" + armorName + "/" + armorType + ".json");
            FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode), StandardCharsets.UTF_8);
        }
    }
}
