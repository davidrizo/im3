package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.classifiers.segmentation.SymbolClusterer;
import es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation.CalvoDocumentSegmenter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.entities.Image;
import es.ua.dlsi.im3.omr.model.io.XMLWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It converts a file, named equal to the image file plus .txt, with bounding boxes, symbol type, and position in staff
 * to a Muret XML file, all images in a project
 * @autor drizo
 */
public class Calco2MuretXML {
    Calco2Agnostic calco2Agnostic = new Calco2Agnostic();
    int nsymbols = 0;

    public void convert(String name, File imageFilesFolder, File folder, File output, int expectedNPages, boolean leaveAllInOnePageRegion) throws IOException, IM3Exception {
        Project project = new Project(ProjectVersion.v1);
        project.setName(name);
        project.setNotationType(NotationType.eMensural);

        ArrayList<File> files = new ArrayList<>();
        FileUtils.readFiles(folder, files, "txt");

        Comparator<File> fileComparator = FileUtils.getFileNameComparator();
        files.sort(fileComparator); // sort by filename
        int nimage=1;
        for (File file: files) {
            importFile(imageFilesFolder, project, file, nimage, expectedNPages, leaveAllInOnePageRegion);
            nimage++;
        }

        XMLWriter xmlWriter = new XMLWriter(AgnosticVersion.v2);
        xmlWriter.save(project, output);
        System.out.println("Done!");
    }

    /*private Integer [] getRasterImage(Raster raster, BoundingBox boundingBox) {
        int [] pixels = new int[raster.getWidth()*raster.getHeight()];
        raster.getPixels((int)boundingBox.getFromX(), (int)boundingBox.getFromY(), (int)boundingBox.getWidth(), (int)boundingBox.getHeight(), pixels);
        Integer [] result = new Integer[pixels.length];
        for (int i=0; i<pixels.length; i++) {
            result[i] = pixels[i];
        }
        return result;
    }*/

    private void importFile(File imageFilesFolder, Project project, File input, int nimage, int expectedNPages, boolean leaveAllInOnePageRegion) throws IOException, IM3Exception {
        System.out.println("Processing " + input.getName());
        String imageName = FileUtils.getFileWithoutPathOrExtension(input);

        File imageFile = new File(imageFilesFolder, imageName);
        if (!imageFile.exists()) {
            throw new ImportException("Cannot find the file " + imageFile.getAbsolutePath());
        }

        Image image = new Image(imageName);
        image.setOrder(nimage);
        project.addImage(image);

        BufferedImage bufferedImage = ImageIO.read(imageFile);
        int width          = bufferedImage.getWidth();
        int height         = bufferedImage.getHeight();
        Raster raster = bufferedImage.getData();


        InputStreamReader isr = new InputStreamReader(new FileInputStream(input));
        BufferedReader br = new BufferedReader(isr);
        String line;

        List<Symbol> symbolList = new LinkedList<>();
        int n=1;
        while ((line=br.readLine())!=null) {
            String [] components = line.split(";");
            if (components.length != 4) {
                throw new IOException("Invalid line, must have 4 components and it has just " + components.length);
            }
            String from = components[0];
            String[] fromXY = from.split(",");
            if (fromXY.length != 2) {
                throw new IOException("Expected 2 numbers in " + from);
            }
            String to = components[1];
            String[] toXY = to.split(",");
            if (toXY.length != 2) {
                throw new IOException("Expected 2 numbers in " + to);
            }

            double fromX = Double.parseDouble(fromXY[0]);
            double fromY = Double.parseDouble(fromXY[1]);
            double toX = Double.parseDouble(toXY[0]);
            double toY = Double.parseDouble(toXY[1]);

            try {
                String label = components[2];
                String position = components[3];

                nsymbols ++;
                PositionInStaff positionInStaff = PositionInStaff.parseString(position);
                Symbol symbol = new Symbol();
                symbol.setBoundingBox(new BoundingBoxXY(fromX, fromY, toX, toY));
                AgnosticSymbolType agnosticSymbolType = calco2Agnostic.convert(label);
                postProcess(agnosticSymbolType, positionInStaff);

                AgnosticSymbol agnosticSymbol = new AgnosticSymbol(AgnosticVersion.v1, agnosticSymbolType, positionInStaff);
                symbol.setAgnosticSymbol(agnosticSymbol);

                //TODO symbol.setRasterMonochromeImage(new RasterMonochromeImage((int)symbol.getWidth(), (int)symbol.getHeight(), getRasterImage(raster, symbol.getBoundingBox())));
                symbolList.add(symbol);
                n++;
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error in " + input.getAbsolutePath() + ", line #" + n);
                throw e;
            }
        }

        if (leaveAllInOnePageRegion) {
            Page onePage = new Page(0, 0, width, height);
            image.addPage(onePage);
            Region oneRegion = new Region(RegionType.all, 0, 0, width, height);
            onePage.add(oneRegion);
            for (Symbol symbol : symbolList) {
                oneRegion.addSymbol(symbol);
            }

        } else {
            CalvoDocumentSegmenter calvoDocumentSegmenter = new CalvoDocumentSegmenter(imageFile);

            int splitingColumn = 0;
            if (expectedNPages > 1) {
                splitingColumn = calvoDocumentSegmenter.findPagesDivisionPoint();
            }
            List<Page> pages = new LinkedList<>();
            if (splitingColumn == 0 || splitingColumn >= bufferedImage.getWidth() - 1) {
                System.out.println("\tJust one page");
                pages.add(new Page(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1));
            } else {
                System.out.println("Two pages split in column " + splitingColumn);
                pages.add(new Page(0, 0, splitingColumn, bufferedImage.getHeight()));
                pages.add(new Page(splitingColumn + 1, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1));
            }

            // now collect all symbols belonging to each page and add to a single region in each one
            for (Page page : pages) {
                image.addPage(page);
                List<Symbol> symbolsInPage = new LinkedList<>();
                for (Symbol symbol : symbolList) {
                    double[] xy = symbol.getBoundingBox().getCenter();
                    if (page.getBoundingBox().contains(xy[0], xy[1])) {
                        symbolsInPage.add(symbol);
                    }
                }
                if (!symbolsInPage.isEmpty()) {
                    List<Region> regions = calvoDocumentSegmenter.segment((int) page.getBoundingBox().getFromX(), (int) page.getBoundingBox().getToX());
                    SymbolClusterer clusterer = new SymbolClusterer(); // it works bettern than the Calvo segmenter
                    SortedSet<Region> clustererRegions = clusterer.cluster(symbolsInPage, regions.size());
                    for (Region region : clustererRegions) {
                        page.add(region);
                    }
                }
            }
        }

        isr.close();
    }

    private void postProcess(AgnosticSymbolType agnosticSymbolType, PositionInStaff positionInStaff) {
        boolean belowThirdLine = positionInStaff.getLineSpace() < PositionsInStaff.LINE_3.getLineSpace();
        if (agnosticSymbolType instanceof Note) {
            Note note = (Note) agnosticSymbolType;
            if (note.getDurationSpecification() instanceof NoteFigures && ((NoteFigures) note.getDurationSpecification()).isUsesStem()) {
                if (belowThirdLine) {
                    ((Note) agnosticSymbolType).setStemDirection(Directions.up);
                } else {
                    ((Note) agnosticSymbolType).setStemDirection(Directions.down);
                }
            }
        } else if (agnosticSymbolType instanceof Slur) {
            if (belowThirdLine) {
                ((Slur) agnosticSymbolType).setPositions(Positions.below);
            } else {
                ((Slur) agnosticSymbolType).setPositions(Positions.above);
            }
        } else if (agnosticSymbolType instanceof Fermata) {
            if (belowThirdLine) {
                ((Fermata)agnosticSymbolType).setPositions(Positions.below);
            } else {
                ((Fermata)agnosticSymbolType).setPositions(Positions.above);
            }
        }
    }



    private void copyImagesToProjectFolder(File images, File outputFolderProject) throws IOException {
        ArrayList<File> files = new ArrayList<>();
        FileUtils.readFiles(images, files, "JPG");
        FileUtils.readFiles(images, files, "jpg");

        for (File file: files) {
            Path from = Paths.get(file.toURI());

            File targetFile = new File(outputFolderProject, FileUtils.getFileWithoutPath(file.getName()));
            if (!targetFile.exists()) {
                Path to = Paths.get(targetFile.toURI());
                Files.copy(from, to);
            }
        }
    }

    public static final void main(String [] args) throws IOException, IM3Exception {
        // Used once, this is why it is not parametrized
        File imageFilesFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/catedral_zaragoza/8Abril2014/MuestrasTipografia");
        File inputFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/catedral_zaragoza/BoundingBoxesCapitanJCalvo/Anotaciones-Capitan-BB");
        File outputFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza");
        String [] subfolders = new String[] {
                "B-3.28",
                "B-50.747",
                "B-53.781",
                "B-59.850"
        };
        boolean leaveAllInOnePageRegion = true; // it's worthless to split pages and regions, it's better to correct it by hand
        int [] expectedNPages = {1, 1, 1, 2};
        Calco2MuretXML c = new Calco2MuretXML();
        int i=0;
        for (String subfolder: subfolders) {
            File outputFolderProject = new File(outputFolder, subfolder);
            outputFolderProject.mkdirs();
            File imagesFolder = new File(outputFolderProject, "images");
            imagesFolder.mkdirs();

            File images = new File(imageFilesFolder, subfolder);
            File outputXML = new File(outputFolderProject, subfolder + ".mrt");
            File input = new File(inputFolder, subfolder);
            c.copyImagesToProjectFolder(images, imagesFolder);
            c.convert(subfolder, images, input, outputXML, expectedNPages[i], leaveAllInOnePageRegion);
            i++;
        }
        System.out.println("Total symbols: " + c.nsymbols);


    }


}
