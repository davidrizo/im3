package es.ua.dlsi.im3.analysis.hierarchical.gui;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.HierarchicalAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotivesAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo 
 */
public class FormAndMotivesAnalysis extends Analysis {
	public static final String TYPE = "form_motives";
	FormAnalysis formAnalysis;
	MelodicMotivesAnalysis motivesAnalysis;

	public FormAndMotivesAnalysis() {		
		super(TYPE);
	}

	public FormAnalysis getFormAnalysis() {
		return formAnalysis;
	}

	public MelodicMotivesAnalysis getMotivesAnalysis() {
		return motivesAnalysis;
	}
	
	public void setFormAnalysis(FormAnalysis newFormAnalysis) {
		try {
			if (this.formAnalysis != null) {
				removeAnalysis(this.formAnalysis);
			}
			addAnalysis(newFormAnalysis);
			this.formAnalysis = newFormAnalysis;
		} catch (IM3Exception e) {
			throw new IM3RuntimeException(e); // this should'nt happen
		}
	}

	public void setMotivesAnalysis(MelodicMotivesAnalysis newMotivesAnalysis) {
		try {
			if (this.motivesAnalysis != null) {
				removeAnalysis(this.motivesAnalysis);
			}
			addAnalysis(newMotivesAnalysis);
			this.motivesAnalysis = newMotivesAnalysis;	
		} catch (IM3Exception e) {
			throw new IM3RuntimeException(e); // this should'nt happen
		}
	}
	
	@Override
	public void addAnalysis(HierarchicalAnalysis<?> ha) throws IM3Exception {
		if (ha instanceof FormAnalysis) {
			if (formAnalysis != null && formAnalysis != ha) {
				throw new IM3Exception("There is another form analysis");
			}
			this.formAnalysis = (FormAnalysis) ha;
			super.addAnalysis(ha);
			Logger.getLogger(FormAndMotivesAnalysis.class.getName()).log(Level.INFO, "Added form analysis");
		}

		if (ha instanceof MelodicMotivesAnalysis) {
			if (motivesAnalysis != null && motivesAnalysis != ha) {
				throw new IM3Exception("There is another motives analysis");
			}
			this.motivesAnalysis = (MelodicMotivesAnalysis) ha;
			super.addAnalysis(ha);
			Logger.getLogger(FormAndMotivesAnalysis.class.getName()).log(Level.INFO, "Added motives analysis");
		}
		
		super.addAnalysis(ha);
	}


	@Override
	public String toString() {
		return name.get();
	}
	
}
