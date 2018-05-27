package es.ua.dlsi.im3.omr.model.exporters;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * It exports a file containing a line for each symbol with the image relative path, region number, symbol, bounding box specified as x1;y1;x2;y2
 * @autor drizo
 */
public class XML2AgnosticSymbolBoundingBoxesTextFile {
    private static final String FIELD_SEPARATOR = ";";
    private static final String BOUNDING_BOX_SEPARATOR = ",";

    public void run(File inputXMLFile, File outputTextFile) throws FileNotFoundException, IM3Exception {
        File imagesFolder = new File(inputXMLFile.getParent(), Project.IMAGES_FOLDER);
        XMLReader reader = new XMLReader(AgnosticVersion.v2);
        Project project = reader.load(inputXMLFile);

        PrintStream ps = new PrintStream(outputTextFile);
        ps.print("#Image path");
        ps.print(FIELD_SEPARATOR);
        ps.print("Page");
        ps.print(FIELD_SEPARATOR);
        ps.print("Page Bounding Box (x1,y1,x2,y2) (printed just once)");
        ps.print(FIELD_SEPARATOR);
        ps.print("Region");
        ps.print(FIELD_SEPARATOR);
        ps.print("Region Bounding Box (x1,y1,x2,y2) (printed just once)");
        ps.print(FIELD_SEPARATOR);
        ps.print("Agnostic symbol");
        ps.print(FIELD_SEPARATOR);
        ps.println("Bounding box (x1,y1,x2,y2)");

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
                            int nsymbol = 1;
                            for (Symbol symbol : region.getSymbols()) {
                                printSymbol(ps, imagesFolder, image, npage, page, nregion, region, nsymbol++, symbol);
                                nsymbol++;
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

    private void printBoundingBox(PrintStream ps, BoundingBox bb) {
        ps.print(bb.getFromX());
        ps.print(BOUNDING_BOX_SEPARATOR);
        ps.print(bb.getFromY());
        ps.print(BOUNDING_BOX_SEPARATOR);
        ps.print(bb.getToX());
        ps.print(BOUNDING_BOX_SEPARATOR);
        ps.print(bb.getToY());

    }

    private void printSymbol(PrintStream ps, File fileImagesFolder, Image image, int npage, Page page, int nregion, Region region, int nsymbol, Symbol symbol) throws IM3Exception {
        ps.print(image.getImageRelativeFileName());
        ps.print(FIELD_SEPARATOR);
        ps.print(npage);
        ps.print(FIELD_SEPARATOR);
        if (nregion == 1 && nsymbol == 1) {
            printBoundingBox(ps, page.getBoundingBox());
        }
        ps.print(FIELD_SEPARATOR);
        ps.print(nregion);
        ps.print(FIELD_SEPARATOR);
        if (nsymbol == 1) {
            printBoundingBox(ps, region.getBoundingBox());
        }
        ps.print(FIELD_SEPARATOR);
        ps.print(symbol.getAgnosticSymbol().getAgnosticString());
        ps.print(FIELD_SEPARATOR);
        printBoundingBox(ps, symbol.getBoundingBox());
        ps.println();
    }


    public static final void main(String [] args) throws IM3Exception, FileNotFoundException {
        if (args.length == 2) {
            File input = new File(args[0]);
            File output = new File(args[1]);
            new XML2AgnosticSymbolBoundingBoxesTextFile().run(input, output);
        } else {
            // generate all corpus given an absolute path
            String basePath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza/";
            String [] projects = new String[]
                    {
                    "B-3.28",
                    "B-50.747",
                    "B-53.781",
                    "B-59.850"};

            String outputPath = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/catedral_zaragoza/staves_symbols_boundingboxes";

            for (String project: projects) {
                try {
                    System.out.println("-------- PROJECT: " + project + " ----------------");
                    File input = new File(new File(basePath, project), project + ".mrt");
                    File output = new File(outputPath, project + ".symbolsboundingboxes.txt");
                    new XML2AgnosticSymbolBoundingBoxesTextFile().run(input, output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
