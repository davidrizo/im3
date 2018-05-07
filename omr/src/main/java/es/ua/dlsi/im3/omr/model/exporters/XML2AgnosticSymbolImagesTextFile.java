package es.ua.dlsi.im3.omr.model.exporters;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.ImageUtils;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * It exports a file containing a line for each symbol with the image, region number, symbol, image pixels
 * @autor drizo
 */
public class XML2AgnosticSymbolImagesTextFile {
    private static final String FIELD_SEPARATOR = ";";
    ImageUtils imageUtils;

    public void run(File inputXMLFile, File outputTextFile) throws FileNotFoundException, IM3Exception {
        File imagesFolder = new File(inputXMLFile.getParent(), Project.IMAGES_FOLDER);
        imageUtils = new ImageUtils();
        XMLReader reader = new XMLReader();
        Project project = reader.load(inputXMLFile);

        PrintStream ps = new PrintStream(outputTextFile);
        ps.print("#Image");
        ps.print(FIELD_SEPARATOR);
        ps.print("Page");
        ps.print(FIELD_SEPARATOR);
        ps.print("Region");
        ps.print(FIELD_SEPARATOR);
        ps.print("Agnostic symbol");
        ps.print(FIELD_SEPARATOR);
        ps.print("Image pixels:");
        ps.print(Image.RESIZE_W);
        ps.print('x');
        ps.println(Image.RESIZE_H);

        for (Image image: project.getImages()) {
            System.out.println("Image " + image.getImageRelativeFileName());
            int npage = 1;
            for (Page page: image.getPages()) {
                if (!page.getRegions().isEmpty()) {
                    System.out.println("\tPage #" + npage);
                    int nregion = 1;
                    for (Region region : page.getRegions()) {
                        if (!region.getSymbols().isEmpty()) {
                            System.out.println("\tRegion #" + nregion);
                            for (Symbol symbol : region.getSymbols()) {
                                printSymbol(ps, imagesFolder, image, npage, nregion, symbol);
                            }
                            nregion++;
                        }
                    }
                    npage++;
                }
            }

        }
        ps.close();
    }

    private void printSymbol(PrintStream ps, File fileImagesFolder, Image image, int page, int region, Symbol symbol) throws IM3Exception {
        ps.print(image.getImageRelativeFileName());
        ps.print(FIELD_SEPARATOR);
        ps.print(page);
        ps.print(FIELD_SEPARATOR);
        ps.print(region);
        ps.print(FIELD_SEPARATOR);
        ps.print(symbol.getAgnosticSymbol().getAgnosticString());
        ps.print(FIELD_SEPARATOR);

        StringBuilder stringBuilder = new StringBuilder();
        File imageFile = new File(fileImagesFolder, image.getImageRelativeFileName());
        BufferedImage subimage = imageUtils.generateBufferedImage(imageFile, symbol.getBoundingBox());
        BufferedImage scaledImage = imageUtils.rescaleToGray(subimage, Image.RESIZE_W, Image.RESIZE_H);
        int[][] imagePixels = imageUtils.readGrayScaleImage(scaledImage);

        if (imagePixels.length != Image.RESIZE_W) {
            throw new IM3Exception("Expected width " +  Image.RESIZE_W + " and found " + imagePixels.length);
        }
        if (imagePixels[0].length != Image.RESIZE_H) {
            throw new IM3Exception("Expected height " +  Image.RESIZE_H + " and found " + imagePixels[0].length);
        }
        boolean first = true;
        for (int i=0; i<imagePixels.length; i++) {
            for (int j=0; j<imagePixels[i].length; j++) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(imagePixels[i][j]);
            }
        }
        ps.println(stringBuilder);
    }


    public static final void main(String [] args) throws IM3Exception, FileNotFoundException {
        if (args.length == 2) {
            File input = new File(args[0]);
            File output = new File(args[1]);
            new XML2AgnosticSymbolImagesTextFile().run(input, output);
        } else {
            // generate all corpus given an absolute path
            String basePath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/";
            String [] projects = new String[]
                    {
                    "B-3.28",
                    "B-50.747",
                    "B-53.781",
                    "B-59.850"};

            String outputPath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/zaragoza/staves_symbols_images";

            for (String project: projects) {
                try {
                    System.out.println("-------- PROJECT: " + project + " ----------------");
                    File input = new File(new File(basePath, project), project + ".mrt");
                    File output = new File(outputPath, project + ".symbolsimages.txt");
                    new XML2AgnosticSymbolImagesTextFile().run(input, output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
