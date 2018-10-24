package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author drizo
 */
@Entity
public class Region {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Format: fromX,fromY,toX,toY - absolute values
     */
    @Column (name = "bounding_box")
    @Convert(converter = BoundingBoxConverter.class)
    private BoundingBox boundingBox;

    @JsonIgnore // avoid circular references in REST result
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="page_id", referencedColumnName="id")
    private Page page;

    public Region() {
    }

    public Region(BoundingBox boundingBox, Page page) {
        this.boundingBox = boundingBox;
        this.page = page;
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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
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
