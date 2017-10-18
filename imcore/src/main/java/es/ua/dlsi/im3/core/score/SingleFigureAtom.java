package es.ua.dlsi.im3.core.score;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import org.apache.commons.lang3.math.Fraction;

public abstract class SingleFigureAtom extends Atom implements ITimedSymbolWithConnectors {
	AtomFigure atomFigure;
	BeamGroup belongsToBeam;
    /**
     * Note that the NotePitches can also have connectors
     */
    ConnectorCollection connectorCollection;

    public SingleFigureAtom(Figures figure, int dots) {
		atomFigure = new AtomFigure(this, figure, dots);
		addDuration(atomFigure.getDuration());
	}
	
	/**
	 * Package visibility, used by tuplets and mensural
	 * @param figure
	 * @param dots
	 */
	SingleFigureAtom(Figures figure, int dots, Time alteredDuration) {
		atomFigure = new AtomFigure(this, figure, dots, alteredDuration);
		addDuration(atomFigure.getDuration());
	}
	
	public void setRelativeToAtomOnset(Time currentRelativeOnset) {
		atomFigure.setRelativeOnset(currentRelativeOnset);
	}

	/**
	 * Note this is the represented figure, its duration may be different for tuplet elements or multiple and measure rests
	 * @return
	 */
	public final AtomFigure getAtomFigure() {
		return atomFigure;
	}
	
	@Override
	public List<AtomFigure> getAtomFigures() {
		return Arrays.asList(atomFigure);
	}
	@Override
	public List<Atom> getAtoms() {
		return Arrays.asList(this);
	}

	public void setRelativeOnset(Time relativeOnset) {
		this.atomFigure.setRelativeOnset(relativeOnset);
	}

	@Override
	public String toString() {
		return super.toString() + ", " + atomFigure;
	}

	/**
	 * Use with care, usually this method should be used just by importers in cases such as tuplets.
	 * It can be used only when the current figure is Figure.NO_DURATION
	 * @param figure
	 */
	public void setFigure(Figures figure) {
		this.atomFigure.setFigure(figure);
		addDuration(atomFigure.getDuration());
    }

    @Override
    public void setDuration(Time duration) {
        super.setDuration(duration);
        this.atomFigure.setDuration(duration);
    }

    public BeamGroup getBelongsToBeam() {
        return belongsToBeam;
    }

    public void setBelongsToBeam(BeamGroup belongsToBeam) {
        this.belongsToBeam = belongsToBeam;
    }

    @Override
    public Collection<Connector> getConnectors() {
        if (connectorCollection == null) {
            return null;
        } else {
            return connectorCollection.getConnectors();
        }
    }

    @Override
    public void addConnector(Connector connector) {
        if (connectorCollection == null) {
            connectorCollection = new ConnectorCollection();
        }
        connectorCollection.add(connector);
    }

    @Override
    public boolean containsConnectorFrom(ISymbolWithConnectors fromSymbol) {
        if (connectorCollection == null) {
            return false;
        } else {
            return connectorCollection.containsConnectorFrom(fromSymbol);
        }

    }

    @Override
    public boolean containsConnectorTo(ISymbolWithConnectors fromSymbol) {
        if (connectorCollection == null) {
            return false;
        } else {
            return connectorCollection.containsConnectorTo(fromSymbol);
        }
    }
}
