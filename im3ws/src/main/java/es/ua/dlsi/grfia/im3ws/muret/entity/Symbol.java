package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private String agnosticEncoding;

    public Symbol() {
    }

    public Symbol(Region region, String agnosticEncoding, BoundingBox boundingBox, Strokes strokes) {
        this.region = region;
        this.agnosticEncoding = agnosticEncoding;
        this.boundingBox = boundingBox;
        this.strokes = strokes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgnosticEncoding() {
        return agnosticEncoding;
    }

    public void setAgnosticEncoding(String agnosticEncoding) {
        this.agnosticEncoding = agnosticEncoding;
    }

    @JsonIgnore
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
