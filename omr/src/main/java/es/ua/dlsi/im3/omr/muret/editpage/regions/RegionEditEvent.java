package es.ua.dlsi.im3.omr.muret.editpage.regions;

import es.ua.dlsi.im3.omr.muret.Event;
import javafx.scene.input.MouseEvent;


public class RegionEditEvent extends Event<MouseEvent> {
    private final RegionEditView regionView;

    public RegionEditEvent(MouseEvent event, RegionEditView regionView) {
        super(event);
        this.regionView = regionView;
    }

    public RegionEditView getRegionView() {
        return regionView;
    }
}
