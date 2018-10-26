package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

/**
 * @author drizo
 */
@Entity
public class Region {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Format: fromX,fromY,toX,toY - absolute values
     */
    @Column (name = "bounding_box")
    @Convert(converter = BoundingBoxConverter.class)
    private BoundingBox boundingBox;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="page_id", referencedColumnName="id")
    private Page page;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="region_id", referencedColumnName="id")
    private List<Symbol> symbols;


    public Region() {
    }

    public Region(BoundingBox boundingBox, Page page, List<Symbol> symbols) {
        this.boundingBox = boundingBox;
        this.page = page;
        this.symbols = symbols;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @JsonIgnore
    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", boundingBox='" + boundingBox + '\'' +
                ", page=" + page +
                '}';
    }
}
