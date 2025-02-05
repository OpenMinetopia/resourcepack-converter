package nl.openminetopia.resourceconverter.jobs.generic;

import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class CopyTexturesJob extends ConversionJob {

    private final File TEXTURES_DIR = new File(Main.MINECRAFT_DIR, "textures");

    @Override
    public void run() {
        System.out.println("Copying textures...");

        copyTexturesFromDirectory("misc");
        copyTexturesFromDirectory("temp");
        copyTexturesFromDirectory("special");

    }

    @SneakyThrows
    private void copyTexturesFromDirectory(String directory) {
        File source = new File(TEXTURES_DIR, directory);
        if (!source.exists() || source.listFiles() == null) {
            System.out.println("No " + directory + " textures found in source directory, skipping...");
            return;
        }

        FileUtils.copyDirectory(source, new File(Main.OUTPUT_DIR, "assets/minecraft/textures"));
    }
}
