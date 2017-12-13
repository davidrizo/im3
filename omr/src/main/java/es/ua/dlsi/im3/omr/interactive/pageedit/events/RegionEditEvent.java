package es.ua.dlsi.im3.omr.interactive.pageedit.events;

import es.ua.dlsi.im3.omr.interactive.pageedit.Event;
import es.ua.dlsi.im3.omr.interactive.pageedit.RegionView;
import javafx.scene.input.MouseEvent;


public class RegionEditEvent extends Event<MouseEvent> {
    private final RegionView regionView;

    public RegionEditEvent(MouseEvent event, RegionView regionView) {
        super(event);
        this.regionView = regionView;
    }

    public RegionView getRegionView() {
        return regionView;
    }
}
