package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO Fusionarlo con el anterior MEISongImporter y acabarlo con StAX - véase código comentado
public class MEIScoreLayoutImporter {
    /*It imports the score and layout information from a MEI file using the same Verovio MEI page customization (https://www.verovio.org/structure.xhtml)
    public ScoreLayout parse(File file) throws ImportException  {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return parse(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        }
    }
    public ScoreLayout parse(InputStream inputStream) throws ImportException  {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = factory.createXMLEventReader(new InputStreamReader(inputStream));
            parse(eventReader);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot import", e);
            throw new ImportException(e);
        }

    }

    private void parse(XMLEventReader eventReader) {

    }*/

    public ScoreLayout parse(InputStream inputStream) throws IM3Exception {
        MEISongImporter meiSongImporter = new MEISongImporter();
        ScoreSong scoreSong = meiSongImporter.importSong(inputStream);
        return convert(scoreSong);
    }

    public ScoreLayout parse(File file) throws IM3Exception {
        MEISongImporter meiSongImporter = new MEISongImporter();
        ScoreSong scoreSong = meiSongImporter.importSong(file);
        return convert(scoreSong);
    }

    //TODO
    // it looks for pages, systems....
    private ScoreLayout convert(ScoreSong scoreSong) throws IM3Exception {
        PageLayout pageLayout = new AutomaticPageLayout(scoreSong, scoreSong.getStaves(), true, new CoordinateComponent(1000), new CoordinateComponent(1000));
        pageLayout.layout(true);
        return pageLayout;
    }


}
