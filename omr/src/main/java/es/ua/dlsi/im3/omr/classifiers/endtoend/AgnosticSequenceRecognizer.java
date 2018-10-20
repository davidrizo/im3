package es.ua.dlsi.im3.omr.classifiers.endtoend;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.CommandLine;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Cargar con Java
/**
 * @autor drizo
 */
public class AgnosticSequenceRecognizer {
    public List<HorizontallyPositionedSymbol> recognize(File inputImage) throws IOException, IM3Exception {
        //TODO En un paquete
        File commandFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/python");
        String output = CommandLine.execShellCommand(commandFolder, "staff2agnostic.sh " + inputImage.getAbsolutePath());

        LinkedList<HorizontallyPositionedSymbol> result = new LinkedList<>();

        String [] lines = output.split("\n");
        for (String line: lines) {
            String [] tokens = line.split(" ");
            if (tokens.length != 3) {
                throw new IM3Exception("Expected 3 tokens and found " + tokens.length + " in '" + line + "'");
            }
            double fromX = Double.parseDouble(tokens[0]);
            double toX = Double.parseDouble(tokens[1]);
            String agnosticString = tokens[2];
            AgnosticSymbol agnosticSymbol = AgnosticSymbol.parseAgnosticString(AgnosticVersion.v2, agnosticString);
            result.add(new HorizontallyPositionedSymbol(fromX, toX, agnosticSymbol));

        }

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} symbols recognized in image file {1}", new Object[] {result.size(), inputImage.getAbsolutePath()});
        return result;
    }

    @Override
    public String toString() {
        return "CRNN+CTC";
    }
}

        /*String [] tokens = output.substring(1, output.length()-2).split(",");
        PagedCapitan2Agnostic pagedCapitan2Agnostic = new PagedCapitan2Agnostic();
        for (String t: tokens) {
            String token = t.trim().replaceAll("'", "");
            AgnosticSymbol agnosticSymbol = pagedCapitan2Agnostic.convert(token);
            if (agnosticSymbol.getSymbol() instanceof Note) { //TODO Los demás
                Note note = (Note) agnosticSymbol.getSymbol();
                if (note.getDurationSpecification().isUsesStem()) {
                    if (agnosticSymbol.getPositionInStaff().getLineSpace() < PositionsInStaff.LINE_3.getLineSpace()) {
                        note.setStemDirection(Directions.up);
                    } else {
                        note.setStemDirection(Directions.down);
                    }
                }
            }
            agnosticSymbolLinkedList.add(agnosticSymbol);
        }

        return agnosticSymbolLinkedList;
    }
}
*/