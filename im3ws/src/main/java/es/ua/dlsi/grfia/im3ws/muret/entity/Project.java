package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author drizo
 */
@Entity
public class Project {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String name;
    @Column
    private String path;
    @Column
    private Date created;
    @Column
    private Date lastChange;

    @JsonBackReference // it avoids circular relationships
    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName="id")
    private User createdBy;

    @JsonBackReference // it avoids circular relationships
    @ManyToOne
    @JoinColumn(name="changed_by", referencedColumnName="id")
    private User changedBy;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY, mappedBy = "project")
    private List<Image> images;

    /**
     * The image used as identifying image
     */
    @Column (name = "poster_frame_path")
    private String posterFramePath;

    public Project() {
    }

    public Project(String name, String path, Date created, Date lastChange, User createdBy, User changedBy, List<Image> images) {
        this.name = name;
        this.path = path;
        this.created = created;
        this.lastChange = lastChange;
        this.createdBy = createdBy;
        this.changedBy = changedBy;
        this.images = images;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getPosterFramePath() {
        return posterFramePath;
    }

    public void setPosterFramePath(String posterFramePath) {
        this.posterFramePath = posterFramePath;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", created=" + created +
                ", lastChange=" + lastChange +
                ", createdBy=" + createdBy +
                ", changedBy=" + changedBy +
                '}';
    }
}
