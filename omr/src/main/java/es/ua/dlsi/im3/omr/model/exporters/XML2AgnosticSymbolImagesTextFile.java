package es.ua.dlsi.im3.omr.model.exporters;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * It exports a file containing a line for each symbol with the image, region number, symbol, image pixels
 * @autor drizo
 */
public class XML2AgnosticSymbolImagesTextFile {
    private static final String FIELD_SEPARATOR = ";";

    public void run(File inputXMLFile, File outputTextFile, boolean fixedSize) throws FileNotFoundException, IM3Exception {
        File imagesFolder = new File(inputXMLFile.getParent(), Project.IMAGES_FOLDER);
        XMLReader reader = new XMLReader(AgnosticVersion.v2);
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
        if (fixedSize) {
            ps.print("Image pixels:");
            ps.print(Image.RESIZE_W);
            ps.print('x');
            ps.println(Image.RESIZE_H);
        } else {
            ps.print("Width");
            ps.print(FIELD_SEPARATOR);
            ps.println("Height");
        }

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
                                printSymbol(ps, imagesFolder, image, npage, nregion, symbol, fixedSize);
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

    private void printSymbol(PrintStream ps, File fileImagesFolder, Image image, int page, int region, Symbol symbol, boolean fixedSize) throws IM3Exception {
        ps.print(image.getImageRelativeFileName());
        ps.print(FIELD_SEPARATOR);
        ps.print(page);
        ps.print(FIELD_SEPARATOR);
        ps.print(region);
        ps.print(FIELD_SEPARATOR);
        ps.print(symbol.getAgnosticSymbol().getAgnosticString());
        ps.print(FIELD_SEPARATOR);

        StringBuilder stringBuilder = new StringBuilder();
        if (fixedSize) {
            int[] imagePixels = image.getGrayscaleImagePixelsNormalized(fileImagesFolder, symbol.getBoundingBox());
            boolean first = true;
            for (int i = 0; i < imagePixels.length; i++) {
                if (first) {
                    first = false;
                } else {
                    stringBuilder.append(',');
                }
                stringBuilder.append(imagePixels[i]);
            }
        } else {
            int [][] imagePixels = image.getGrayscaleImagePixels(fileImagesFolder, symbol.getBoundingBox());
            ps.print(imagePixels.length);
            ps.print(FIELD_SEPARATOR);
            ps.print(imagePixels[0].length);
            ps.print(FIELD_SEPARATOR);
            boolean first = true;
            for (int i = 0; i < imagePixels.length; i++) {
                for (int j = 0; j < imagePixels[i].length; j++) {
                    if (first) {
                        first = false;
                    } else {
                        stringBuilder.append(',');
                    }
                    stringBuilder.append(imagePixels[i][j]);
                }
            }
        }
        ps.println(stringBuilder);
    }


    public static final void main(String [] args)  {
        // generate all corpus given an absolute path
        String basePath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/";
        String [] projects = new String[]
                {
                "B-3.28", // generated 2018, june 11th
                //"B-50.747",
                //"B-53.781",
                "B-59.850" // generated 2018, june 27th
                };

        String outputPath30x30 = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images_30x30";
        String outputPath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images";

        for (String project: projects) {
            try {
                System.out.println("-------- PROJECT: " + project + " ----------------");
                File input = new File(new File(basePath, project), project + ".mrt");
                File output = new File(outputPath30x30, project + ".symbolsimages_30x30.txt");
                //new XML2AgnosticSymbolImagesTextFile().run(input, output, true);

                output = new File(outputPath, project + ".symbolsimages.txt");
                new XML2AgnosticSymbolImagesTextFile().run(input, output, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
