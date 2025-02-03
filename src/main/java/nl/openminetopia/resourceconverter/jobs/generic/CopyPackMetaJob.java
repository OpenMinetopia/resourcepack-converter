package nl.openminetopia.resourceconverter.jobs.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CopyPackMetaJob extends ConversionJob {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public void run() {
        System.out.println("Copying and modifying pack.mcmeta...");

        File source = new File(Main.BASE_DIR + "/pack.mcmeta");
        File destination = new File(Main.OUTPUT_DIR + "/pack.mcmeta");

        ObjectNode packMeta;

        if (source.exists()) {
            String content = FileUtils.readFileToString(source, StandardCharsets.UTF_8);
            if (content.charAt(0) == '\ufeff') {
                content = content.substring(1);
            }
            packMeta = (ObjectNode) objectMapper.readTree(content);
            System.out.println("Existing pack.mcmeta found, modifying pack_format...");
        } else {
            System.out.println("No pack.mcmeta found, creating a new one...");
            packMeta = objectMapper.createObjectNode();
            ObjectNode packNode = packMeta.putObject("pack");
            packNode.put("description", "§e[OpenMinetopia] Converted with \n§7openminetopia.nl");
        }

        packMeta.withObject("pack").put("pack_format", 61);

        FileUtils.writeStringToFile(destination, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(packMeta), StandardCharsets.UTF_8);
        System.out.println("pack.mcmeta successfully created/modified and copied.");
    }
}