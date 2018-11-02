package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.ua.dlsi.grfia.im3ws.IM3WSException;

import javax.persistence.*;
import java.util.List;

/**
 * @author drizo
 */
@Entity
public class Page {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Format: fromX,fromY,toX,toY
     */
    @Column (name = "bounding_box")
    @Convert(converter = BoundingBoxConverter.class)
    private BoundingBox boundingBox;

    @Column
    private String comments;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="image_id", referencedColumnName="id")
    Image image;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="page_id", referencedColumnName="id")
    private List<Region> regions;

    public Page() {
    }

    public Page(BoundingBox boundingBox, String comments, Image image, List<Region> regions) throws IM3WSException {
        this.boundingBox = boundingBox;
        this.image = image;
        this.regions = regions;
        this.comments = comments;
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
    public void setBoundingBox(BoundingBox boundingBox)  {
        this.boundingBox = boundingBox;
    }
    @JsonIgnore
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
