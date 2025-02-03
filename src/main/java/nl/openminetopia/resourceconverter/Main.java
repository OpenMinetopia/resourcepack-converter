package nl.openminetopia.resourceconverter;

import nl.openminetopia.resourceconverter.jobs.ConversionJob;
import nl.openminetopia.resourceconverter.jobs.generic.CopyPackMetaJob;
import nl.openminetopia.resourceconverter.jobs.generic.CopySoundsJob;
import nl.openminetopia.resourceconverter.jobs.model.ItemModelJob;
import nl.openminetopia.resourceconverter.jobs.optifine.OptifineArmorJob;
import nl.openminetopia.resourceconverter.jobs.optifine.OptifineItemJob;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final List<ConversionJob> jobs = new ArrayList<>();

    //public static final File BASE_DIR = new File(System.getProperty("user.dir"));
    public static final File BASE_DIR = new File("/Users/aaron/Library/Application Support/ModrinthApp/profiles/Modpack 1.21.3/resourcepacks/resourcepack");
    public static final File MINECRAFT_DIR = new File(BASE_DIR, "assets/minecraft");

    public static final File OPTIFINE_DIR = new File(MINECRAFT_DIR, "optifine");
    public static final File OUTPUT_DIR = new File(BASE_DIR, "../resourcepack-converted");

    public static void main(String[] args) {
        registerJobs(
                new CopyPackMetaJob(),
                new CopySoundsJob(),
                new OptifineArmorJob(),
                new OptifineItemJob(),
                new ItemModelJob()
        );

        runJobs();
    }

    private static void registerJobs(ConversionJob... conversionJobs) {
        Arrays.stream(conversionJobs).forEach(conversionJob -> {
            System.out.println("Registering " + conversionJob.getClass().getSimpleName());
            jobs.add(conversionJob);
        });
    }

    private static void runJobs() {
        jobs.forEach(ConversionJob::run);
    }
}