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
public class XML2AgnosticSymbolImagesStrokesTextFile {
    public static final String FIELD_SEPARATOR = ";";
    public static final String STROKES_SEPARATOR = ":";
    public static final String COMMA = ",";

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
            ps.print(Image.RESIZE_H);
        } else {
            ps.print("Width");
            ps.print(FIELD_SEPARATOR);
            ps.print("Height");
        }
        ps.print(FIELD_SEPARATOR);
        ps.println("Optional strokes list in the form (stroke1:stroke2:...:strokeN) where each stroke is made of time1,x1,y1,time2,x2,y2,time3,x3,y3,...,timeN,xN,yN");

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

        ps.print(stringBuilder);
        ps.print(FIELD_SEPARATOR);
        if (symbol.getStrokes() != null && !symbol.getStrokes().isEmpty()) {
            boolean firstStroke = true;
            for (Stroke stroke: symbol.getStrokes().getStrokeList()) {
                if (firstStroke) {
                    firstStroke = false;
                } else {
                    ps.print(STROKES_SEPARATOR);
                }
                boolean firstPoint = true;
                for (Point point: stroke.pointsProperty()) {
                    if (firstPoint) {
                        firstPoint = false;
                    } else {
                        ps.print(COMMA);
                    }
                    ps.print(stroke.getFirstPointTime());
                    ps.print(COMMA);
                    ps.print(point.getX());
                    ps.print(COMMA);
                    ps.print(point.getY());
                }
            }
        }


        ps.println();
    }


    public static final void main(String [] args)  {
        // generate all corpus given an absolute path
        String basePath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/";
        String [] projects = new String[]
                {
                "B-3.28", // generated 2018, july 13th, 2018
                //"B-50.747",
                //"B-53.781",
                "B-59.850" // generated 2018, july 13th, 2018
                };

        String outputPath30x30 = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_images_30x30_strokes";

        for (String project: projects) {
            try {
                System.out.println("-------- PROJECT: " + project + " ----------------");
                File input = new File(new File(basePath, project), project + ".mrt");
                File output = new File(outputPath30x30, project + ".symbolsimages_30x30_strokes.txt");
                new XML2AgnosticSymbolImagesStrokesTextFile().run(input, output, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
