package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.io.File;
import java.io.PrintStream;

public class MusicXMLExporter implements ISongExporter {
    protected ScoreSong scoreSong;
    protected StringBuilder sb;
    private static final String PARTWISE_DOCTYPE = "<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML "
            + "3.0" + " Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\n";

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";


    @Override
    public void exportSong(File file, ScoreSong song) throws ExportException {
        this.scoreSong = scoreSong;
        sb = new StringBuilder();

        PrintStream ps = null;
        try {
            ps = new PrintStream(file, "UTF-8");
            ps.print(exportSong());
        } catch (Exception e) {
            throw new ExportException(e);
        }
        if (ps != null) {
            ps.close();
        }
    }

    /**
     * TODO PIERRE
     * @return
     * @throws IM3Exception
     * @throws ExportException
     */
    public String exportSong() throws IM3Exception, ExportException {
        sb = new StringBuilder();

        sb.append(XML);
        sb.append(PARTWISE_DOCTYPE);
        XMLExporterHelper.start(sb, 0, "score-partwise", "version", "3.0");
        XMLExporterHelper.start(sb, 1, "identification");
        XMLExporterHelper.start(sb, 2, "encoding");
        XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "software", "IM3");
        //XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "encoding-date", new Date()); //TODO Con este formato: 2016-11-15

        XMLExporterHelper.end(sb, 2, "encoding");
        XMLExporterHelper.end(sb, 1, "identification");
        XMLExporterHelper.end(sb, 0, "score-partwise");

        return sb.toString();
    }
}
