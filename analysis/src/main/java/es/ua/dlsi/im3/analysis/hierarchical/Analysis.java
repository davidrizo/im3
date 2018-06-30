package es.ua.dlsi.im3.analysis.hierarchical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Descendants must include a default constructor (no parameters) 
 * @author drizo
 *
 */
public abstract class Analysis {
	protected String type;
	protected StringProperty name;
	protected StringProperty author;
	protected ObjectProperty<Date> date;
	protected ScoreSong scoreSong;
	protected List<HierarchicalAnalysis<?>> hierarchicalAnalyses;
    protected Graphical graphical;

	public Analysis(String type)  {
		this.type = type;
		hierarchicalAnalyses = new ArrayList<>();
		this.name = new SimpleStringProperty();
		this.author = new SimpleStringProperty();
		this.date = new SimpleObjectProperty<>();
        graphical = new Graphical();
	}
	
	
	public String getType() {
		return type;
	}


	public ScoreSong getScoreSong() {
		return scoreSong;
	}

	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getAuthor() {
		return author.get();
	}
	public void setAuthor(String author) {
		this.author.set(author);
	}
	public Date getDate() {
		return date.get();
	}
	public void setDate(Date date) {
		this.date.set(date);
	}
	public StringProperty nameProperty() {
		return name;
	}
	public StringProperty authorProperty() {
		return author;
	}

	public void setScoreSong(ScoreSong scoreSong) throws IM3Exception {
		this.scoreSong = scoreSong;
		if (!scoreSong.hasAnalysisStaff()) {
			Logger.getLogger(Analysis.class.getName()).info("Creating analysis staff");
			scoreSong.createAnalysisPartAndStaff();
			scoreSong.createAnalysisHooks(scoreSong.getAnalysisStaff());
		} 
		
	}


	public List<HierarchicalAnalysis<?>> getHierarchicalAnalyses() {
		return hierarchicalAnalyses;
	}

	public void addAnalysis(HierarchicalAnalysis<?> ha) throws IM3Exception {
		if (!this.hierarchicalAnalyses.contains(ha)) {
			this.hierarchicalAnalyses.add(ha);
		}
	}

	public void removeAnalysis(HierarchicalAnalysis<?> ha) {
		this.hierarchicalAnalyses.remove(ha);
	}

	@Override
	public String toString() {
		return "Analysis [name=" + name + "]";
	}


    public Graphical getGraphical() {
	    return graphical;
    }
}
