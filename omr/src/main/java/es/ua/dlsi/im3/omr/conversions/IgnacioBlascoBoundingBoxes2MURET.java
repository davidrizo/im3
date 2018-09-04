package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * It creates MURET projects from the Ignacio Blasco Bounding Boxes
 * @autor drizo
 */
public class IgnacioBlascoBoundingBoxes2MURET {
    public static void main(String [] args) throws IOException, IM3Exception {
        File inputFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/catedral_barcelona/regiones_identificadas_ignacio_blasco");
        File outputFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_barcelona");

        for (File workFolder: inputFolder.listFiles()) {
            if (workFolder.isDirectory()) {
                new IgnacioBlascoBoundingBoxes2MURET().process(workFolder, outputFolder);
            }
        }
    }

    private void process(File workFolder, File muretProjectsFolder) throws IOException, IM3Exception {
        System.out.println("Working with " + workFolder);
        String normalizedName = FileUtils.leaveValidCaracters(workFolder.getName());
        File projectFolder = new File(muretProjectsFolder, normalizedName);
        projectFolder.mkdir();
        File projectImagesFolder = new File(projectFolder, "imagesold");
        projectImagesFolder.mkdir();

        Project project = new Project(ProjectVersion.v1, NotationType.eMensural);
        project.setName(workFolder.getName());

        ArrayList<File> imageFiles = new ArrayList<>();
        FileUtils.readFiles(workFolder, imageFiles, "jpg");

        imageFiles.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        int order = 0;
        for (File imageFile: imageFiles) {
            File copiedImageFile = new File(projectImagesFolder, imageFile.getName());
            if (!copiedImageFile.exists()) {
                FileUtils.copy(imageFile, copiedImageFile);
            }
            Image image = new Image(imageFile.getName());
            image.setOrder(order++);
            project.addImage(image);

            // read regions of image
            File bbfile = new File(workFolder.getAbsolutePath() + "/gt", FileUtils.getFileWithoutPathOrExtension(imageFile) + ".txt");
            if (!bbfile.exists()) {
                throw new IOException("Cannot find " + bbfile);
            }
            readRegions(projectImagesFolder, image, bbfile);
        }


        XMLWriter writer = new XMLWriter(AgnosticVersion.v2);
        writer.save(project, new File(projectFolder, normalizedName + ".mrt"));
    }

    /**
     *
     * @param projectImagesFolder
     * @param image
     * @param bbfile File with tagged regions and bounding boxes
     */
    private void readRegions(File projectImagesFolder, Image image, File bbfile) throws IOException, IM3Exception {
        BufferedReader br = new BufferedReader(new FileReader(bbfile));
        String line;
        // they are always in a page
        BoundingBoxXY imageBoundingBox = image.computeBoundingBox(projectImagesFolder);
        Page page = new Page(imageBoundingBox);
        image.addPage(page);

        int n=1;
        while ((line=br.readLine())!=null) {
            if (!line.trim().isEmpty()) {
                String [] components = line.split(";");
                if (components.length != 5) {
                    System.err.println("Expected 5 components and found " + components.length + " in line #" + n + " in file " + bbfile);
                } else {
                    RegionType regionType;
                    BoundingBox boundingBox = null;
                    try {
                        boundingBox = new BoundingBoxXY(Double.parseDouble(components[0]), Double.parseDouble(components[1]), Double.parseDouble(components[2]), Double.parseDouble(components[3]));
                        switch (components[4]) {
                            case "text":
                                regionType = RegionType.lyrics;
                                break;
                            case "title":
                                regionType = RegionType.title;
                                break;
                            case "staff":
                                regionType = RegionType.staff;
                                break;
                            default:
                                throw new IM3Exception("Unknown region type: '" + components[4] + "'");
                        }
                        page.add(new Region(regionType, boundingBox.getFromX(), boundingBox.getFromY(), boundingBox.getToX(), boundingBox.getToY()));
                    } catch (IM3Exception e) {
                        System.err.println("Error in bounding box in line " + n + " in file " + bbfile + ": " + e);
                    }

                }
            }
            n++;
        }
        System.out.println("#" + n + " lines processed");
    }
}
