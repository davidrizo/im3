package es.ua.dlsi.im3.analysis.hierarchical.gui;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.DummyFormAnalyzer;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.io.IOFactory;
import es.ua.dlsi.im3.analysis.hierarchical.io.MEIHierarchicalAnalysesModernExporter;
import es.ua.dlsi.im3.analysis.hierarchical.io.MEIHierarchicalAnalysesModernImporter;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotive;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.gui.score.javafx.ISelectable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//TODO Quizás habría que partir en distintos modelos (motive, form...)
public class Model {
	ScoreSong scoreSong;
	ObjectProperty<ObservableList<FormAndMotivesAnalysis>> possibleAnalyses;
	Collection<? extends ISelectable> selectedElements;
	ObjectProperty<FormAndMotivesAnalysis> selectedAnalysis;
	File file;
	private StringProperty titleProperty;
	
	public Model() throws ImportException {
		possibleAnalyses = new SimpleObjectProperty<>(FXCollections.observableArrayList());
		selectedAnalysis = new SimpleObjectProperty<>();
		titleProperty = new SimpleStringProperty();
		
		IOFactory.getInstance().registerAnalysisCreator(FormAndMotivesAnalysis.TYPE, FormAndMotivesAnalysis.class);
	}

	public void importMusicXML(File file) throws ImportException {
		this.file = file;
		titleProperty.set(file.getName());
		MusicXMLImporter importer = new MusicXMLImporter();
		scoreSong = importer.importSong(file);
	}

	/**
	 * It is also useful for regular MEI files
	 * @param file
	 * @throws ImportException
	 */
	public void importHierarchicalAnalysisMEI(File file) throws ImportException {
		this.file = file;
		titleProperty.set(file.getName());
		MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
		importer.importSongAndAnalyses(file);
		scoreSong = importer.getScoreSong();
		this.possibleAnalyses.get().clear();
		
		ArrayList<Analysis> analyses = importer.getAnalyses();
		for (Analysis analysis : analyses) {
			Logger.getLogger(Model.class.getName()).log(Level.INFO, "Read analysis: " + analysis.getType());
			if (analysis.getType().equals(FormAndMotivesAnalysis.TYPE)) {
				this.possibleAnalyses.get().add((FormAndMotivesAnalysis) analysis);
			} else {
				Logger.getLogger(Model.class.getName()).log(Level.INFO, "Skipped non compatible analysis: " + analysis.getType());
			}
		}
	}
	
	public File getFile() {
		return file;
	}

	public void save() throws ExportException {
		MEIHierarchicalAnalysesModernExporter exporter = new MEIHierarchicalAnalysesModernExporter(possibleAnalyses.get());
		exporter.exportSong(file, scoreSong);
	}
	public ObjectProperty<ObservableList<FormAndMotivesAnalysis>> possibleAnalysesProperty() {
		return possibleAnalyses;
	}
	
	public ObjectProperty<FormAndMotivesAnalysis> selectedAnalysisProperty() {
		return selectedAnalysis;
	}
	
	public void doFormAnalysis() throws IM3Exception {
		//TODO Urgent - De momento cargo un análisis fijo
		DummyFormAnalyzer analyzer = new DummyFormAnalyzer(new String [] {"A", "B", "A'"});
		FormAndMotivesAnalysis fa = new FormAndMotivesAnalysis();
		fa.setScoreSong(scoreSong);
		fa.setName("Dummy");
		fa.setAuthor("drizo");
		fa.setDate(new Date());

		fa.setFormAnalysis(analyzer.analyze(scoreSong));
		possibleAnalyses.get().add(fa);
	}
	
	/*public FormAnalysis createNewFormAnalysis(String name) throws IM3Exception {
		FormAnalysis fa = new FormAnalysis(name, scoreSong);
		possibleAnalyses.get().add(fa);
		return fa;		
	}*/
	public void setSelectedElements(Collection<? extends ISelectable> selectedElements) {
		this.selectedElements = selectedElements;		
		Logger.getLogger(Model.class.getName()).log(Level.INFO, "Selected {0} elements", this.selectedElements.size());
	}

	//TODO Estoy cogiendo sólo el primer análisis
	/*public void createSectionWithSelectedElements() throws IM3Exception, TreeException {
		FormAnalysis fa = possibleAnalyses.get(0);
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		boolean found = false;
		for (ISelectable e: selectedElements) {
			if (e instanceof DurationalSymbolView) {
				DurationalSymbolView dsv = (DurationalSymbolView) e;
				if (dsv.getModelSymbol() instanceof ITimedElement) {
					ITimedElement te = (ITimedElement) dsv.getModelSymbol(); 
					min = Math.min(min, te.getTime());
					max = Math.max(max, te.getTime());
					found = true;
				}
			}
		}
		if (!found) {
			throw new IM3Exception("No timed element selected");
		}
		fa.addSection("Ejemplo", min, max); //TODO Preguntar nombre
	}*/
	
	public MelodicMotive createMotiveWithSelectedElements(String name, String description, String color) throws IM3Exception {
		if (selectedElements == null || selectedElements.isEmpty()) {
			throw new IM3Exception("No element selected");
		}
		MelodicMotive motive = new MelodicMotive();
		motive.setName(name);
		motive.setDescription(description);
		motive.setHexaColor(color);

		List<AtomFigure> atomFigureList = new LinkedList<>();

		for (ISelectable selectedElement: this.selectedElements) {
		    throw new UnsupportedOperationException();
			/*IM3 if (selectedElement instanceof ScoreNoteView
					|| selectedElement instanceof ScoreRestView
					|| selectedElement instanceof ScoreChordView) {
				DurationalSymbolView<?> dsv = (DurationalSymbolView<?>) selectedElement;
				notesChordsAndRests.add((ScoreDurationalSymbol<?>)dsv.getModelSymbol());
			}*/
		}
		
		if (atomFigureList.isEmpty()) {
			throw new IM3Exception("No note, chord or rest selected, cannot create melodic motive");
		}
		
		motive.setAtomFigures(atomFigureList);
		MelodicMotivesAnalysis motivesAnalysis = selectedAnalysis.get().getMotivesAnalysis();
		if (motivesAnalysis == null) {
			motivesAnalysis = new MelodicMotivesAnalysis(this.scoreSong);
			selectedAnalysis.get().setMotivesAnalysis(motivesAnalysis);
		}
		motivesAnalysis.addMotive(motive);
		return motive;
	}
	
	public void addSectionAt(String name, ScoreAnalysisHook analysisHook, String description, String hexaColor) throws IM3Exception, TreeException {
		//TODO Names
		selectedAnalysis.get().getFormAnalysis().addSection(name, analysisHook.getTime(), description, hexaColor);
	}
	/**
	 * It ensures the song has an analysis staff
	 * @throws IM3Exception
	 */
	public void ensureAnalysisStaff(boolean createAnalysisHooks) throws IM3Exception {
		if (!scoreSong.hasAnalysisStaff()) {
			scoreSong.createAnalysisPartAndStaff(createAnalysisHooks);
		}
	}
	public void setFile(File file) {
		this.file = file;
	}

	public FormAndMotivesAnalysis createNewAnalysis(String name) throws IM3Exception {
		FormAndMotivesAnalysis fa = new FormAndMotivesAnalysis();
		fa.setScoreSong(scoreSong);
		fa.setName(name);
		fa.setAuthor("drizo");
		fa.setDate(new Date());
		fa.setFormAnalysis(new FormAnalysis(scoreSong));
		possibleAnalyses.get().add(fa);
		return fa;		
	}

    public ScoreSong getScoreSong() {
	    return scoreSong;
    }
}
