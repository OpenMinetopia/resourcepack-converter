package nl.openminetopia.resourceconverter.jobs.generic;

import lombok.SneakyThrows;
import nl.openminetopia.resourceconverter.Main;
import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class CopySoundsJob extends ConversionJob {

    @Override
    @SneakyThrows
    public void run() {
        System.out.println("Copying sounds...");

        File source = new File(Main.MINECRAFT_DIR, "sounds");
        if (!source.exists() || source.listFiles() == null) {
            System.out.println("No sounds found in source directory, skipping...");
            return;
        }

        FileUtils.copyDirectory(source, new File(Main.OUTPUT_DIR, "assets/minecraft/sounds"));
        FileUtils.copyFile(new File(Main.MINECRAFT_DIR, "sounds.json"), new File(Main.OUTPUT_DIR, "assets/minecraft/sounds.json"));
    }
}
