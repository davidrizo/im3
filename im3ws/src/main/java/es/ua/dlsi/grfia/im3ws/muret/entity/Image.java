package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import es.ua.dlsi.im3.core.score.NotationType;

import javax.persistence.*;
import java.util.List;

/**
 * @author drizo
 */
@Entity
public class Image {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String filename;
    @Column
    private String comments;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="project_id", referencedColumnName="id")
    private Project project;

    @JsonManagedReference
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="image_id", referencedColumnName="id")
    private List<Page> pages;

    public Image() {
    }

    public Image(String path, String comments, Project project) {
        this.filename = path;
        this.project = project;
        this.comments = comments;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", project=" + project +
                '}';
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }
}
