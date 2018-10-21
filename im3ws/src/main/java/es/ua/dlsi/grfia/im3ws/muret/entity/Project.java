package es.ua.dlsi.grfia.im3ws.muret.entity;

import javax.persistence.*;
import java.util.Date;

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
    //@Column (name = "createdBy")
    @ManyToOne(cascade = CascadeType.ALL)
    private User createdBy;
    //@Column (name = "changedBy")
    @ManyToOne(cascade = CascadeType.ALL)
    private User changedBy;

    public Project() {
    }

    public Project(String name, String path, Date created, Date lastChange, User createdBy, User changedBy) {
        this.name = name;
        this.path = path;
        this.created = created;
        this.lastChange = lastChange;
        this.createdBy = createdBy;
        this.changedBy = changedBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }
}
