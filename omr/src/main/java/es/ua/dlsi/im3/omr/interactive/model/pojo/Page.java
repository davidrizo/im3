package es.ua.dlsi.im3.omr.interactive.model.pojo;

import java.util.ArrayList;
import java.util.List;

public class Page {
    String imageRelativeFileName;
    List<Staff> staves;

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
}
