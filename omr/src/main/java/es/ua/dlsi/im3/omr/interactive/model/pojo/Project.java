package es.ua.dlsi.im3.omr.interactive.model.pojo;

import java.util.ArrayList;
import java.util.List;

public class Project {
    List<Page> pages;

    public Project() {
        pages = new ArrayList<>();
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
