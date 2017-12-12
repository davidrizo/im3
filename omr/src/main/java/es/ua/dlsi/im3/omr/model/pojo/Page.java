package es.ua.dlsi.im3.omr.model.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Page {
    String imageRelativeFileName;
    List<Staff> staves;
    int order;
    Set<Instrument> instruments;
    List<Region> regions;

    public Page() {
        staves = new ArrayList<>();
        instruments = new TreeSet<>();
        regions = new ArrayList<>();
    }

    public Page(String imageRelativeFileName) {
        this.imageRelativeFileName = imageRelativeFileName;
        staves = new ArrayList<>();
        instruments = new TreeSet<>();
        regions = new ArrayList<>();
    }

    public String getImageRelativeFileName() {
        return imageRelativeFileName;
    }

    public void setImageRelativeFileName(String imageRelativeFileName) {
        this.imageRelativeFileName = imageRelativeFileName;
    }

    public List<Staff> getStaves() {
        return staves;
    }

    public void setStaves(List<Staff> staves) {
        this.staves = staves;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Set<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<Instrument> instruments) {
        this.instruments = instruments;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
