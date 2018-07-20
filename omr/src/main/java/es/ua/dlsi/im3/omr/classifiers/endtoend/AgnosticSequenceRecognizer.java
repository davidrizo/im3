package es.ua.dlsi.im3.omr.classifiers.endtoend;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.core.utils.CommandLine;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.conversions.PagedCapitan2Agnostic;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Directions;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Note;
import es.ua.dlsi.im3.omr.imageprocessing.IStaffNormalizer;
import es.ua.dlsi.im3.omr.imageprocessing.JCalvoStaffNormalizer;
import es.ua.dlsi.im3.omr.imageprocessing.StaffNormalizerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//TODO Cargar con Java
/**
 * @autor drizo
 */
public class AgnosticSequenceRecognizer {
    public List<AgnosticSymbol> recognize(File inputImage) throws IOException, IM3Exception {
        IStaffNormalizer normalizer = StaffNormalizerFactory.getInstance().create();
        String tmpFileName = FileUtils.getFileWithoutPathOrExtension(inputImage);
        File tmp = File.createTempFile(tmpFileName, "_normalized.jpg");
        normalizer.normalize(inputImage, tmp);

        File imgTxtEnhCommandFolder = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/software/CRNN-antiguo/OMR-CRNN");
        String output = CommandLine.execShellCommand(imgTxtEnhCommandFolder, "image2agnostic_antiguo.sh " + tmp.getAbsolutePath());
        String [] tokens = output.substring(1, output.length()-2).split(",");
        PagedCapitan2Agnostic pagedCapitan2Agnostic = new PagedCapitan2Agnostic();
        LinkedList<AgnosticSymbol> agnosticSymbolLinkedList = new LinkedList<>();
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
