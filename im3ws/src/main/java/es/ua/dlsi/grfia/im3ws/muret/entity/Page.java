package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.ua.dlsi.grfia.im3ws.IM3WSException;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author drizo
 */
@Entity
public class Page {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Format: fromX,fromY,toX,toY
     */
    @Column (name = "bounding_box")
    @Convert(converter = BoundingBoxConverter.class)
    private BoundingBox boundingBox;
    @JsonIgnore // avoid circular references in REST result
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="image_id", referencedColumnName="id")
    Image image;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="page_id", referencedColumnName="id")
    private Set<Region> regions;

    public Page() {
    }

    public Page(BoundingBox boundingBox, Image image, Set<Region> regions) throws IM3WSException {
        this.boundingBox = boundingBox;
        this.image = image;
        this.regions = regions;
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
    public final void setBoundingBox(BoundingBox boundingBox) throws IM3WSException {
        this.boundingBox = boundingBox;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Set<Region> getRegions() {
        return regions;
    }

    public void setRegions(Set<Region> regions) {
        this.regions = regions;
    }
}
