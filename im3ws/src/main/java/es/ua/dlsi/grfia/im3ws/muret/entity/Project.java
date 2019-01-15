package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import es.ua.dlsi.im3.core.score.NotationType;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Column
    private String path;
    @Column
    private Date created;
    @Column
    private Date lastChange;
    @Column
    private String composer;
    @Column
    private String comments;

    @Column (name="notation_type")
    @Enumerated(EnumType.STRING)
    private NotationType notationType;

    @Column (name="manuscript_type")
    @Enumerated(EnumType.STRING)
    private ManuscriptType manuscriptType;

    /**
     * Comma separated list of image ids - when an image is not present here is sorted at the end of the list
     */
    @Column (name = "images_ordering")
    private String imagesOrdering;

    @Lob
    @Column (name = "thumbnail_base64_encoding", columnDefinition = "LONGTEXT")
    private String thumbnailBase64Encoding;

    @JsonBackReference (value="createdBy") // it avoids circular relationships
    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName="id")
    private User createdBy;

    @JsonBackReference (value="changedBy") // it avoids circular relationships
    @ManyToOne
    @JoinColumn(name="changed_by", referencedColumnName="id")
    private User changedBy;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY, mappedBy = "project")
    private List<Image> images;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="state_id")
    State state;

    public Project() {
    }

    public Project(String name, String path, String composer, Date created, Date lastChange, User createdBy, User changedBy, String thumbnailBase64Encoding, String comments, String imagesOrdering, NotationType notationType, ManuscriptType manuscriptType, State state, List<Image> images) {
        this.name = name;
        this.composer = composer;
        this.notationType = notationType;
        this.path = path;
        this.thumbnailBase64Encoding = thumbnailBase64Encoding;
        this.created = created;
        this.lastChange = lastChange;
        this.createdBy = createdBy;
        this.changedBy = changedBy;
        this.images = images;
        this.comments = comments;
        this.imagesOrdering = imagesOrdering;
        this.manuscriptType = manuscriptType;
        this.state = state;
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
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getThumbnailBase64Encoding() {
        return thumbnailBase64Encoding;
    }

    public void setThumbnailBase64Encoding(String thumbnailBase64Encoding) {
        this.thumbnailBase64Encoding = thumbnailBase64Encoding;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public NotationType getNotationType() {
        return notationType;
    }

    public void setNotationType(NotationType notationType) {
        this.notationType = notationType;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public ManuscriptType getManuscriptType() {
        return manuscriptType;
    }

    public void setManuscriptType(ManuscriptType manuscriptType) {
        this.manuscriptType = manuscriptType;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getImagesOrdering() {
        return imagesOrdering;
    }

    public void setImagesOrdering(String imagesOrdering) {
        this.imagesOrdering = imagesOrdering;
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
