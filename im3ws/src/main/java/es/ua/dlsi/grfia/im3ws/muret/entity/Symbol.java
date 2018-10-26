package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import javax.persistence.*;

/**
 * @author drizo
 */
@Entity
public class Symbol {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="region_id", referencedColumnName="id")
    private Region region;

    /**
     * Format: fromX,fromY,toX,toY - absolute values
     */
    @Column (name = "bounding_box")
    @Convert(converter = BoundingBoxConverter.class)
    private BoundingBox boundingBox;

    @Column
    @Convert(converter = StrokesConverter.class)
    private Strokes strokes;

    @Column (name="agnostic_encoding")
    @Convert(converter = AgnosticSymbolConverter.class)
    private AgnosticSymbol agnosticSymbol;

    public Symbol() {
    }

    public Symbol(Region region, AgnosticSymbol agnosticSymbol, BoundingBox boundingBox, Strokes strokes) {
        this.region = region;
        this.agnosticSymbol = agnosticSymbol;
        this.boundingBox = boundingBox;
        this.strokes = strokes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore // use getAgnosticSymbolType + getPositionInStaff instead
    public AgnosticSymbol getAgnosticSymbol() {
        return agnosticSymbol;
    }

    @Transient
    public String getAgnosticSymbolType() {
        return agnosticSymbol.getSymbol().toAgnosticString();
    }

    @Transient
    public String getPositionInStaff() {
        return agnosticSymbol.getPositionInStaff().toString();
    }

    public void setAgnosticSymbol(AgnosticSymbol agnosticSymbol) {
        this.agnosticSymbol = agnosticSymbol;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Strokes getStrokes() {
        return strokes;
    }

    public void setStrokes(Strokes strokes) {
        this.strokes = strokes;
    }
}