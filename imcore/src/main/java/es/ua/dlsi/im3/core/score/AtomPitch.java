package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.*;

/**
 * This class implements IStaffElementWithoutLayer for those pitches of a chord that are placed in a different staff than the Atom (the chord)
 * @author drizo
 */
public class AtomPitch implements ITimedElementInStaff, Comparable<AtomPitch>, IUniqueIDObject, ITimedSymbolWithConnectors, IStaffElementWithoutLayer {
	/**
	 * The atom figure is the one this pitch is attached to, that in turn will belong to an Atom
	 */
	AtomFigure atomFigure;
	private ScientificPitch scientificPitch;
	private AtomPitch tiedFromPrevious;
	private AtomPitch tiedToNext;
    // TODO: 1/5/18 Possibly we should refactor accidental things to other class
	/**
	 * Force this accidental to appear, even it is different from that in the pitch (this may happen in mensural notation).
	 * We encode it different from MEI. In MEI acc means the shown accidental, and acc.ges denote the played one. We prefer encoding the played one in
     * the pitch and optionally encode the written one if it is different
	 */
	private Accidentals writtenExplicitAccidental;

    /**
     * Editorial accidental
     */
	private boolean editorialAccidental;
	/**
	 * Implied by the context
	 */
	private boolean fictaAccidental;
    /**
     * The editor may want to hide it (played using the accidental but accidental not shown)
     */
    private boolean hideAccidental;
	/**
	 * The editor may want to hide it (played using the accidental but accidental not shown)
	 */
	private boolean cautionaryAccidental;

	/**
	 * For pitches that are not contained in the same staff as the atom they belong to
	 */
	private Staff staffChange;
	private String ID;

	/**
	 * Analytical information
	 */
	private MelodicFunction melodicFunction;

	/**
	 * Texts: indexed by verse number
	 */
	TreeMap<Integer, ScoreLyric> lyrics;

    ConnectorCollection connectorCollection;

    HashSet<Attachment<AtomPitch>> attachments;

    HashSet<DisplacedDot> displacedDots;

    Integer horizontalOrderInStaff;

	public AtomPitch(AtomFigure atomFigure, ScientificPitch spitch) {
		this.atomFigure = atomFigure;
		this.scientificPitch = spitch;
		tiedFromPrevious = null;
		tiedToNext = null;
	}

	public ScientificPitch getScientificPitch() {
		return scientificPitch;
	}

	//TODO Test (de todos los compareTo, equals, hashCode) !!!! URGENT
	@Override
	public int compareTo(AtomPitch o) {
		return getScientificPitch().compareTo(o.scientificPitch);
	}

	public final AtomFigure getAtomFigure() {
		return atomFigure;
	}

	public boolean isTiedToNext() {
		return tiedToNext != null;
	}

	public final AtomPitch getTiedToNext() {
		return tiedToNext;
	}

	public final void setTiedToNext(AtomPitch tiedTo) throws IM3Exception {
		if (this.tiedToNext != null) {
			if (this.tiedToNext != tiedTo) {
				tiedTo.tiedFromPrevious = null;  
			} // else it is the same
		} else {
			if (tiedTo == null) {
				this.tiedToNext = null;
			} else {
				if (!scientificPitch.equals(tiedTo.getScientificPitch())) {
					throw new IM3Exception("Cannot tie different pitches: " + scientificPitch + " and " + tiedTo.getScientificPitch());
				}
				tiedTo.tiedFromPrevious = this;
				this.tiedToNext = tiedTo;
			}
		}
	}
	

	public boolean isTiedFromPrevious() {
		return tiedFromPrevious != null;
	}

	public final AtomPitch getTiedFromPrevious() {
		return tiedFromPrevious;
	}

	public final void setTiedFromPrevious(AtomPitch tiedFrom) throws IM3Exception {
		if (this.tiedFromPrevious != null) {
			if (this.tiedFromPrevious != tiedFrom) {
				tiedFrom.tiedToNext = null;  
			} // else it is the same
		} else {
			if (tiedFrom == null) {
				this.tiedFromPrevious = null;
			} else {
				if (!scientificPitch.equals(tiedFrom.getScientificPitch())) {
					throw new IM3Exception("Cannot tie different pitches: " + scientificPitch + " and " + tiedFrom.getScientificPitch());
				}
		
				tiedFrom.tiedToNext = this;
				this.tiedFromPrevious = tiedFrom;
			}
		}
	}
	
	public final Staff getStaffChange() {
		return staffChange;
	}

	public final void setStaffChange(Staff staff) throws IM3Exception {
		this.setStaff(staff);
	}

	public final Accidentals getWrittenExplicitAccidental() {
		return writtenExplicitAccidental;
	}

	public final void setWrittenExplicitAccidental(Accidentals writtenExplicitAccidental)  {
		this.writtenExplicitAccidental = writtenExplicitAccidental;
		//setAccidental(writtenExplicitAccidental); // the written accidental may be different from the actual pitch one
	}

	public void setAccidental(Accidentals accidental) throws IM3Exception {
		if (this.scientificPitch == null) {
			throw new IM3Exception("No scientific pitch defined yet");
		}
		this.scientificPitch.getPitchClass().setAccidental(accidental);		
	}

	@Override
	public Staff getStaff() {
		if (staffChange != null) {
			return staffChange;
		} else {
			return atomFigure.getStaff();
		}
	}

	@Override
	public void setStaff(Staff staff) throws IM3Exception {
		if (staffChange != null && staffChange != staff) {
			staffChange.remove(this);
		}

		this.staffChange = staff;
		if (!staff.contains(this)) {
			staff.addElementWithoutLayer(this);
		}
	}

	@Override
	public String toString() {
		return "AtomPitch [scientificPitch=" + scientificPitch + ", writtenExplicitAccidental="
				+ writtenExplicitAccidental + ", onsetRelativeToFigure=" + ", tiedFromPrevious=" + (tiedFromPrevious != null) + ", tiedToNext=" + (tiedToNext != null)
				+ ", staffChange=" + staffChange + "]";
	}

	@Override
	public Time getTime() {
		return atomFigure.getTime();
	}

	public Time getEndTime() {
		return atomFigure.getEndTime();
	}

    @Override
    public void move(Time offset)  {
        //no-op
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AtomPitch atomPitch = (AtomPitch) o;

		if (!atomFigure.equals(atomPitch.atomFigure)) return false;
		if (!scientificPitch.equals(atomPitch.scientificPitch)) return false;
		if (writtenExplicitAccidental != atomPitch.writtenExplicitAccidental) return false;
		return staffChange != null ? staffChange.equals(atomPitch.staffChange) : atomPitch.staffChange == null;
	}

    //TODO Ver esto con detenimiento
	@Override
	public int hashCode() {
	    if (ID != null) {
	        return ID.hashCode();
        } else {
            int result = atomFigure.hashCode();

            result = 31 * result + scientificPitch.hashCode();
           // result = 31 * result + (writtenExplicitAccidental != null ? writtenExplicitAccidental.hashCode() : 0);
            result = 31 * result + (staffChange != null ? staffChange.hashCode() : 0);
            return result;
        }
	}

	@Override
	public String __getID() {
		return ID;
	}

	@Override
	public void __setID(String id) {
		this.ID = id;
	}

	@Override
	public String __getIDPrefix() {
		return "P";
	}

	public Time getDuration() {
		return atomFigure.getDuration();
	}

	public static final Comparator<AtomPitch> TIME_COMPARATOR = new Comparator<AtomPitch>() {
		@Override
		public int compare(AtomPitch o1, AtomPitch o2) {
			int diff = o1.getTime().compareTo(o2.getTime());
			if (diff == 0) {
				diff = o1.getStaff().compareTo(o2.getStaff());
				if (diff == 0) {
					diff = o1.compareTo(o2);
				}
			}
			return diff;
		}
	};

	/**
	 * @return
	 */
	public MelodicFunction getMelodicFunction() {
		return melodicFunction;
	}

	public void setMelodicFunction(MelodicFunction melodicFunction) {
		this.melodicFunction = melodicFunction;
	}

	public TreeMap<Integer, ScoreLyric> getLyrics() {
		return lyrics;
	}

    public void addLyric(Integer ssl, String text, Syllabic syllabic) {
        if (lyrics == null) {
            lyrics = new TreeMap<>();
        }
        lyrics.put(ssl, new ScoreLyric(ssl, this, text, syllabic));
    }

	public void addLyric(ScoreLyric scoreLyric) {
		if (lyrics == null) {
			lyrics = new TreeMap<>();
		}

		Integer verseNumber = scoreLyric.getVerse();
		if (verseNumber == null) {
			verseNumber = lyrics.size() + 1;
			scoreLyric.setVerseNumber(verseNumber);
		}

		lyrics.put(verseNumber, scoreLyric);
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

    public void addAttachment(Attachment<AtomPitch> attachment) {
	    if (attachments == null) {
            attachments = new HashSet<>();
        }
        attachments.add(attachment);
	    Staff staff = getStaff();
	    if (attachment instanceof AttachmentInStaff && staff != null) {
            staff.addAttachment((AttachmentInStaff<?>) attachment); //TODO ¿Está esto bien diseñado?
        }
    }

    public void addDisplacedDot(DisplacedDot displacedDot) {
	    if (displacedDots == null) {
	        displacedDots = new HashSet<>();
        }
        displacedDots.add(displacedDot);
	    addAttachment(displacedDot); //TODO ¿Es necesario también meterlo aquí para algo?
    }

    public HashSet<Attachment<AtomPitch>> getAttachments() {
        return attachments;
    }

    public HashSet<DisplacedDot> getDisplacedDots() {
        return displacedDots;
    }


    public void transpose(Interval interval) throws IM3Exception {
	    this.scientificPitch = interval.computeScientificPitchFrom(this.scientificPitch);
        // TODO: 15/3/18 ¿Y si se cambia el accidental? 
    }

    public boolean isEditorialAccidental() {
        return editorialAccidental;
    }

    public void setEditorialAccidental(boolean editorialAccidental) {
        this.editorialAccidental = editorialAccidental;
    }

    public boolean isHideAccidental() {
        return hideAccidental;
    }

    public void setHideAccidental(boolean hideAccidental) {
        this.hideAccidental = hideAccidental;
    }

	public boolean isFictaAccidental() {
		return fictaAccidental;
	}

	public void setFictaAccidental(boolean fictaAccidental) {
		this.fictaAccidental = fictaAccidental;
	}

	public boolean isCautionaryAccidental() {
		return cautionaryAccidental;
	}

	public void setCautionaryAccidental(boolean cautionaryAccidental) {
		this.cautionaryAccidental = cautionaryAccidental;
	}
}
