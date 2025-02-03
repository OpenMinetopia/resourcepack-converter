package nl.openminetopia.resourceconverter.jobs.model;

import nl.openminetopia.resourceconverter.jobs.ConversionJob;

public class ItemModelJob extends ConversionJob {
    @Override
    public void run() {
        System.out.println("Running ItemModelJob");
    }
}
