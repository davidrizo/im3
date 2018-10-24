package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * @author drizo
 */
@Entity
public class Image {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String path;
    @Column
    private int ordering;
    @JsonIgnore // avoid circular references in REST result
    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="project_id", referencedColumnName="id")
    private Project project;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="image_id", referencedColumnName="id")
    private Set<Page> pages;


    public Image() {
    }

    public Image(String path, int ordering, Project project) {
        this.path = path;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void setPages(Set<Page> pages) {
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
