package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;
import es.ua.dlsi.im3.omr.model.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * It creates MURET projects from the Vicente Gilabert Bounding Boxes (similar to those of Ignacio Blasco).
 * If the work is already found, it deletes all regions. If those region contain symbols, they are moved to the new regions
 * @autor drizo
 */
public class VicenteGilabertBoundingBoxes2MURET {
    public static void main(String [] args) throws IOException, IM3Exception {
        VicenteGilabertBoundingBoxes2MURET obj = new VicenteGilabertBoundingBoxes2MURET();
        obj.processDataset(new File("/Users/drizo/GCLOUDUA/HISPAMUS/muret/catedral_barcelona"),
                new File("/Users/drizo/GCLOUDUA/HISPAMUS/repositorios/regiones_identificadas_vicente_gilabert/Regiones_Barcelona_VGM"),
                true, true);
        obj.processDataset(new File("/Users/drizo/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza"),
                new File("/Users/drizo/GCLOUDUA/HISPAMUS/repositorios/regiones_identificadas_vicente_gilabert/Regiones_Zaragoza_VGM"),
                false, false);

    }

    private void processDataset(File inputMuRETFolder, File inputTagsFolder, boolean replaceDots, boolean deletePrevious) throws IM3Exception, IOException {
        for (File workFolder: inputTagsFolder.listFiles()) {
            if (workFolder.isDirectory()) {
                //new VicenteGilabertBoundingBoxes2MURET().process(workFolder, outputFolder);
                String projectName = FileUtils.getFileWithoutPath(workFolder.getAbsolutePath());

                //if (projectName.equals("B-59.850"))
                {
                    String folder = workFolder.getParent();
                    String inputTagsWorkingFolder = projectName.replaceAll(" ", "-").replaceAll(",", "-");
                    if (replaceDots) {
                        inputTagsWorkingFolder = inputTagsWorkingFolder.replaceAll("\\.", "-");
                    }

                    File muretFolder = new File(inputMuRETFolder, inputTagsWorkingFolder);
                    if (!muretFolder.exists()) {
                        throw new IM3Exception("MuRET project does not exists: " + muretFolder.getAbsolutePath());
                    }

                    File mrtFile = new File(muretFolder, inputTagsWorkingFolder + ".mrt");
                    if (!mrtFile.exists()) {
                        throw new IM3Exception("MuRET project file does not exists: " + mrtFile.getAbsolutePath());
                    }

                    File gtFilesFolder = new File(workFolder, "gt");
                    ArrayList<File> gtFiles = new ArrayList<>();
                    FileUtils.readFiles(gtFilesFolder, gtFiles, "txt");

                    System.out.println("Reading project " + mrtFile.getAbsolutePath());
                    Project mrtProject = new XMLReader(AgnosticVersion.v2).load(mrtFile);

                    gtFiles.forEach(file -> {
                        try {
                            boolean newImage = process(muretFolder, mrtProject, file, deletePrevious);
                            if (newImage) {
                                sortImages(mrtProject);
                            }
                        } catch (Exception e) {
                            System.err.println("\t\t" + e.getMessage());
                        }
                    });

                    XMLWriter writer = new XMLWriter(AgnosticVersion.v2);
                    File newProjectFile = new File(mrtFile.getParent(), inputTagsWorkingFolder + ".mrt2");
                    System.out.println("Saving into " + newProjectFile.toString());
                    writer.save(mrtProject, newProjectFile);
                }
            }
        }

    }

    private void sortImages(Project mrtProject) {
        System.out.println("\tSorting images again...");
        ArrayList<Image> images = new ArrayList<>();
        images.addAll(mrtProject.getImages());
        images.sort((o1, o2) -> {
            return o1.getImageRelativeFileName().compareTo(o2.getImageRelativeFileName());
        });
        for (int i=0; i<images.size(); i++) {
            images.get(i).setOrder(i+1);
        }
        mrtProject.getImages().clear();
        mrtProject.getImages().addAll(images);
    }

    /**
     *
     * @param muretFolder
     * @param mrtProject
     * @param gtFile
     * @return true if new image created
     * @throws IOException
     * @throws IM3Exception
     */
    private boolean process(File muretFolder, Project mrtProject, File gtFile, boolean deletePrevious) throws IOException, IM3Exception {
        /*if (!gtFile.getName().contains("12608")) {
            return false;
        }*/

        System.out.println("\tProcessing: " + gtFile + " of project " + muretFolder.getName());

        String imageName = FileUtils.getFileWithoutPathOrExtension(gtFile) + ".jpg";
        // look for the image
        Image image = null;
        for (Image img: mrtProject.getImages()) {
            if (img.getImageRelativeFileName().toLowerCase().equalsIgnoreCase(imageName)) {
                image = img;
                break;
            }
        }

        File projectImagesFolder = new File(muretFolder, "images");

        boolean newImage = false;
        if (image == null) {
            System.out.println("\t\tCannot find image '" + imageName + "' in project " + muretFolder.getName()+ ", creating it");
            File imageFile = new File(muretFolder, "images/" + imageName);
            if (!imageFile.exists()) {
                imageName = FileUtils.getFileWithoutPathOrExtension(gtFile) + ".JPG";
                imageFile = new File(projectImagesFolder, imageName);
                if (!imageFile.exists()) {
                    throw new IM3Exception("Cannot find image " + imageFile.getAbsolutePath());
                }
            }
            image = new Image(imageName);
            mrtProject.addImage(image);
            newImage = true;
        }

        processImage(gtFile, image, deletePrevious, projectImagesFolder);


        return newImage;
    }

    private void processImage(File gtFile, Image image, boolean deletePrevious, File projectImagesFolder) throws IOException, IM3Exception {
        // 1. first we read all regions into objects
        BufferedReader br = new BufferedReader(new FileReader(gtFile));
        String line;

        List<Region> regionList = new ArrayList<>();
        int n=1;
        while ((line=br.readLine())!=null) {
            if (!line.trim().isEmpty()) {
                String [] components = line.split(",");
                if (components.length != 5) {
                    System.err.println("Expected 5 components and found " + components.length + " in line #" + n + " in file " + gtFile);
                } else {
                    RegionType regionType;
                    BoundingBox boundingBox = null;
                    try {
                        boundingBox = new BoundingBoxXY(Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]), Double.parseDouble(components[3]));
                        try {
                            regionType = RegionType.valueOf(components[4]);
                        } catch (Throwable e) {
                            throw new IM3Exception("Unknown region type: '" + components[4] + "'");
                        }
                        regionList.add(new Region(regionType, boundingBox.getFromX(), boundingBox.getFromY(), boundingBox.getToX(), boundingBox.getToY()));
                    } catch (IM3Exception e) {
                        System.err.println("Error in bounding box in line " + n + " in file " + gtFile + ": " + e);
                    }

                }
            }
            n++;
        }
        System.out.println("\t\t#" + n + " lines processed");

        // 2. Then we add them to the image object
        if (deletePrevious) {
            image.getPages().clear();
        }

        List<Symbol> symbolList = new ArrayList<>();

        if (image.getPages() == null || image.getPages().isEmpty()) {
            // they are always in a page
            BoundingBoxXY imageBoundingBox = image.computeBoundingBox(projectImagesFolder);
            Page page = new Page(imageBoundingBox);
            image.addPage(page);

            regionList.forEach(region -> {
                page.add(region);
            });

            regionList.clear();
        } else {
            image.getPages().forEach(page -> {
                //System.out.println("Page: " + page.getBoundingBox());
                // first clear previous regions saving symbols
                page.getRegions().forEach(region -> {
                    symbolList.addAll(region.getSymbols());
                });
                System.out.println("\t#" + symbolList.size() + " symbols to process");
                page.getRegions().clear();

                for (Iterator<Region> regionIterator = regionList.iterator(); regionIterator.hasNext(); ) {
                    Region region = regionIterator.next();
                    //System.out.println("\tRegion:" + region.getBoundingBox());

                    if (page.getBoundingBox().containsCenterOf(region.getBoundingBox())) {
                        //System.out.println("\tIn page");
                        page.add(region);
                        regionIterator.remove();

                        // try to add symbols to the region
                        for (Iterator<Symbol> symbolIterator = symbolList.iterator(); symbolIterator.hasNext(); ) {
                            Symbol symbol = symbolIterator.next();
                            if (region.getBoundingBox().containsCenterOf(symbol.getBoundingBox())) {
                                region.addSymbol(symbol);
                                symbolIterator.remove();
                            }
                        }
                    }
                }
            });
        }

        if (!regionList.isEmpty()) {
            throw new RuntimeException("Still left #" + regionList.size() + " regions");
        }
        if (!symbolList.isEmpty()) {
            symbolList.forEach(symbol -> System.err.println("Symbol not inserted: " + symbol.getAgnosticSymbol().getAgnosticString() + "\t" + symbol.getBoundingBox().toString()));
            System.err.println("Still left #" + symbolList.size() + " symbols");
        }

    }
}

