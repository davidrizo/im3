package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

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
    private String path;
    @Column
    private int ordering;

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

    public Image(String path, int ordering, Project project) {
        this.path = path;
        this.project = project;
    }
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
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
                ", path='" + path + '\'' +
                ", ordering='" + ordering + '\'' +
                ", project=" + project +
                '}';
    }
}
