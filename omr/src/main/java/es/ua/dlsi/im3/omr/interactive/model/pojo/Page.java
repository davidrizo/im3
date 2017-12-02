package es.ua.dlsi.im3.omr.interactive.model.pojo;

import es.ua.dlsi.im3.omr.interactive.model.Instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Page {
    String imageRelativeFileName;
    List<Staff> staves;
    int order;
    Set<Instrument> instrumentList;


    public Page() {
        staves = new ArrayList<>();
    }

    public Page(String imageRelativeFileName) {
        this.imageRelativeFileName = imageRelativeFileName;
        staves = new ArrayList<>();
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

    public Set<Instrument> getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(Set<Instrument> instrumentList) {
        this.instrumentList = instrumentList;
    }
}
