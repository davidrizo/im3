package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxYX;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;
import es.ua.dlsi.im3.omr.model.io.XMLReader;
import es.ua.dlsi.im3.omr.model.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Merge Calco strokes into MURET files
 * @autor drizo
 */
public class CalcoStrokesIntoMURET {
    public static final void main(String [] args) throws IOException, IM3Exception {
        new CalcoStrokesIntoMURET().run();
    }

    private void run() throws IOException, IM3Exception {
        // Load MURET files
        File calcoFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/catedral_zaragoza/Traced/solotrazos-javi");
        ArrayList<File> calcoFiles = new ArrayList<>();
        FileUtils.readFiles(calcoFolder, calcoFiles, "txt");

        File muretFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/muret/catedral_zaragoza");

        ArrayList<File> muretFiles = new ArrayList<>();
        FileUtils.readFiles(muretFolder, muretFiles, "mrt");
        for (File muretFile: muretFiles) {
            System.out.println("Reading " + muretFile);
            XMLReader xmlReader = new XMLReader(AgnosticVersion.v2);
            Project project = xmlReader.load(muretFile);

            for (Image image : project.getImages()) {
                    System.out.println("\t" + image.getImageRelativeFileName());

                    // look for the tagged file
                    for (File calcoFile : calcoFiles) {
                        if (calcoFile.getName().startsWith(image.getImageRelativeFileName().toUpperCase())) {
                            System.out.println("\t\tCalco file: " + calcoFile.getName());

                            ArrayList<Strokes> strokesList = readCalcoStrokes(calcoFile);
                            ArrayList<BoundingBox> strokesBoundingBoxes = new ArrayList<>();
                            for (Strokes strokes : strokesList) {
                                try {
                                    BoundingBox boundingBox = strokes.computeBoundingBox();
                                    strokesBoundingBoxes.add(boundingBox);
                                } catch (Exception e) {
                                    strokesBoundingBoxes.add(new BoundingBoxYX(0, 0, 1, 1));
                                }
                            }


                            // now look for matching bounding boxes
                            int cont = 0;
                            int total = 0;
                            TreeSet<Integer> usedStrokes = new TreeSet<>();
                            for (Page page : image.getPages()) {
                                for (Region region : page.getRegions()) {
                                    for (Symbol symbol : region.getSymbols()) {
                                        total++;
                                        //if (image.getImageRelativeFileName().equals("00522.JPG") && symbol.getBoundingBox().getFromX() == 139.0 && symbol.getBoundingBox().getFromY() == 201.8) {
                                        //    System.out.println("HOLA");
                                        //}
                                        //System.out.println("\t\t\t\tSymbol: " + symbol.getAgnosticSymbol().getAgnosticString() + " " + symbol.getBoundingBox().toString());
                                        for (int i = 0; i < strokesBoundingBoxes.size(); i++) {
                                            if (!usedStrokes.contains(i)) {
                                                //System.out.println("\t\t\t\t\tChecking strokes #" + strokesList.get(i).getStrokeList().size() + ", strokes" + strokesBoundingBoxes.get(i));
                                                if (symbol.getBoundingBox().overlaps(strokesBoundingBoxes.get(i))) {
                                                    cont++;
                                                    symbol.setBoundingBox(symbol.getBoundingBox());
                                                    symbol.setStrokes(strokesList.get(i));
                                                    usedStrokes.add(i);

                                                    //System.out.println("\t\t\t\t\t\tTRUE: " + strokesList.get(i).getStrokeList());
                                                    break;
                                                } else {
                                                    //System.out.println("\t\t\t\t\t\tNO");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //System.out.println("\t\t\tFound " + cont + "/" + total);

                            break;
                        }
                    }
            }
            XMLWriter xmlWriter = new XMLWriter(AgnosticVersion.v2);
            xmlWriter.save(project, muretFile);
        }
    }

    private ArrayList<Strokes> readCalcoStrokes(File calcoFile) throws IOException, IM3Exception {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(calcoFile));
		BufferedReader br = new BufferedReader(isr);
		String line;
        ArrayList<Strokes> result = new ArrayList<>();
		Integer nextStrokeSize = null;
        Strokes strokes = null;
		while ((line=br.readLine())!=null) {
            if (line.trim().isEmpty()) {
                //TODO Add stroke
                if (strokes.getStrokeList().size() != nextStrokeSize) {
                    throw new IM3Exception("Expected number of strokes " + nextStrokeSize + " and found " + strokes.getStrokeList().size() );
                }
                if (strokes.getStrokeList().size() > 0) {
                    result.add(strokes);
                }
                strokes = null;
            } else {
                String[] coordList = line.split(";");
                if (coordList.length == 1) {
                    nextStrokeSize = Integer.parseInt(coordList[0]);
                    strokes = new Strokes();
                } else {
                    Stroke stroke = new Stroke();
                    strokes.addStroke(stroke);
                    for (String sl : coordList) {
                        String[] coords = sl.split(",");
                        if (coords.length != 3) {
                            throw new IOException("Invalid coordinate, must have 3 components (time, x, y) and it has " + coords.length);
                        }
                        long time = Long.parseLong(coords[0]);
                        stroke.setFirstPointTime(time);
                        stroke.addPoint(time, Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
                    }
                }
            }
        }
        if (strokes != null && strokes.getStrokeList().size() > 0) {
            result.add(strokes);
        }

        return result;
    }
}
