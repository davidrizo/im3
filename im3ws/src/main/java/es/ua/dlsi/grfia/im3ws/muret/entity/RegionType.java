package es.ua.dlsi.grfia.im3ws.muret.entity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

/**
 * @author drizo
 */
@Entity
public class RegionType {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * RGB Color without #, e.g. FF0000
     */
    @Column
    String hexargb;

    public RegionType() {
    }

    public RegionType(String hexargb) {
        this.hexargb = hexargb;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    public String getHexargb() {
        return hexargb;
    }

    public void setHexargb(String hexargb) {
        this.hexargb = hexargb;
    }
}
