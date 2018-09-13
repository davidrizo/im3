package es.ua.dlsi.im3.omr.muret;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.conversions.ScoreToPlayed;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.Intervals;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.lilypond.LilypondExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLExporter;
import es.ua.dlsi.im3.core.score.layout.AutomaticPageLayout;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.DiplomaticLayout;
import es.ua.dlsi.im3.core.score.layout.pdf.PDFExporter;
import es.ua.dlsi.im3.omr.muret.model.InputOutput;
import es.ua.dlsi.im3.omr.muret.model.OMRInstrument;
import es.ua.dlsi.im3.omr.muret.model.OMRProject;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

public class Model {
    Classifiers classifiers;

    ObjectProperty<OMRProject> currentProject;

    public Model() {
        currentProject = new SimpleObjectProperty<>();
        classifiers = new Classifiers();
    }

    public OMRProject getCurrentProject() {
        return currentProject.get();
    }

    public void setCurrentProject(OMRProject currentProject) {
        this.currentProject.setValue(currentProject);
    }

    public ObjectProperty<OMRProject> currentProjectProperty() {
        return currentProject;
    }

    public void clearProject() {
        currentProject.setValue(null);
    }

    public void createProject(File projectFolder, NotationType notationType) throws IM3Exception {
        OMRProject project = new OMRProject(projectFolder);
        project.setNotationType(notationType);
        currentProject.setValue(project);
        InputOutput io = new InputOutput();
        save(); // create structure
    }

    public void save() throws IM3Exception {
        if (currentProject.isNull().get()) {
            throw new IM3Exception("No current project");
        }

        InputOutput io = new InputOutput();
        io.save(currentProject.get());
    }

    public void openProject(File projectFolder) throws IM3Exception {
        InputOutput io = new InputOutput();
        OMRProject project = io.load(projectFolder);
        currentProject.setValue(project);
    }

    public Classifiers getClassifiers() {
        return classifiers;
    }

    /**
     * It returns the layout that corresponds to the selected region for the diplomatic edition
     * @param instrumentHierarchical
     * @param owner
     * @return
     * @throws IM3Exception
     */
    public DiplomaticLayout getDiplomaticScoreLayout(OMRInstrument instrumentHierarchical, OMRRegion owner) throws IM3Exception {
        return currentProject.get().getDiplomaticLayout(instrumentHierarchical, owner);
    }

    private void checkMensural() throws IM3Exception {
        if (currentProject.get().getNotationType() != NotationType.eMensural) {
            throw new IM3Exception("Notation type is not mensural, it is " + currentProject.get().getNotationType());
        }
    }
    public void exportMensuralMEI(File file) throws IM3Exception {
        checkMensural();
        MEISongExporter exporter = new MEISongExporter();
        exporter.exportSong(file, currentProject.get().getDiplomaticEdition());
    }

    public void exportMensuralMens(File file) throws IM3Exception {
        checkMensural();
        KernExporter exporter = new KernExporter();
        exporter.exportSong(file, currentProject.get().getDiplomaticEdition()); //TODO MensExporter
    }

    public void exportMensuralDiplomaticPDF(File file) throws IM3Exception {
        checkMensural();
        PDFExporter exporter = new PDFExporter();
        AutomaticPageLayout automaticPageLayout = new AutomaticPageLayout(currentProject.get().getDiplomaticEdition(), null, true,
                new CoordinateComponent(1000.0), new CoordinateComponent(2000.0)); //TODO Tamaño seleccionable
        exporter.exportLayout(file, automaticPageLayout);
    }

    public void exportMensuralEditorialPDF(File file) throws IM3Exception {
        checkMensural();
        //TODO versión editorial
    }

    private ScoreSong getModernTranslation() throws IM3Exception {
        checkMensural();
        //TODO Que se pueda guardar, que no se genere siempre
        MensuralToModern mensuralToModern = new MensuralToModern(new Clef[] {}); //TODO estos valores que estén guardados
        ScoreSong modern = mensuralToModern.convertIntoNewSong(currentProject.get().getDiplomaticEdition(), Intervals.UNISON_PERFECT); //TODO estos valores que estén guardados

        return modern;
    }

    public void exportMensuralAndModernPDF(File file) throws IM3Exception {
        checkMensural();
        //TODO El merge se carga la versión mensural
        MensuralToModern mensuralToModern = new MensuralToModern(new Clef[] {}); //TODO estos valores que estén guardados
        mensuralToModern.merge(currentProject.get().getDiplomaticEdition(), getModernTranslation());
        PDFExporter exporter = new PDFExporter();
        AutomaticPageLayout automaticPageLayout = new AutomaticPageLayout(currentProject.get().getDiplomaticEdition(), null, true,
                new CoordinateComponent(1000.0), new CoordinateComponent(2000.0)); //TODO Tamaño seleccionable
        exporter.exportLayout(file, automaticPageLayout);
    }

    public void exportMensuralLilypond(File file) throws IM3Exception {
        checkMensural();
        LilypondExporter lilypondExporter = new LilypondExporter();
        lilypondExporter.exportSong(file, currentProject.get().getDiplomaticEdition());
    }

    public void exportPDFWithImagesAndEditorialComments(File file) throws IM3Exception {
        checkMensural();
        //TODO
    }

    public void exportModernMusicXML(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();
        MusicXMLExporter exporter = new MusicXMLExporter();
        exporter.exportSong(file, modern);
    }

    public void exportModernMEI(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();
        MusicXMLExporter exporter = new MusicXMLExporter();
        exporter.exportSong(file, modern);
    }

    public void exportModernKern(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();
        KernExporter exporter = new KernExporter();
        exporter.exportSong(file, modern);
    }

    public void exportModernPDF(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();
        PDFExporter exporter = new PDFExporter();
        AutomaticPageLayout automaticPageLayout = new AutomaticPageLayout(currentProject.get().getDiplomaticEdition(), null, true,
                new CoordinateComponent(1000.0), new CoordinateComponent(2000.0)); //TODO Tamaño seleccionable
        exporter.exportLayout(file, automaticPageLayout);
    }

    public void exportModernLilypond(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();
        LilypondExporter exporter = new LilypondExporter();
        exporter.exportSong(file, modern);
    }

    public void exportModernMIDI(File file) throws IM3Exception {
        ScoreSong modern = getModernTranslation();

        ScoreToPlayed scoreToPlayed = new ScoreToPlayed();
        PlayedSong played = scoreToPlayed.createPlayedSongFromScore(modern);
        MidiSongExporter exporter = new MidiSongExporter();
        exporter.exportSong(file, played);
    }
}
