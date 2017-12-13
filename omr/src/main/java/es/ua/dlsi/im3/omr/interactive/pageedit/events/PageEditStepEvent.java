package es.ua.dlsi.im3.omr.interactive.pageedit.events;

import es.ua.dlsi.im3.omr.interactive.pageedit.Event;
import es.ua.dlsi.im3.omr.interactive.pageedit.PageEditStep;

public class PageEditStepEvent extends Event<PageEditStep> {
    public PageEditStepEvent(PageEditStep content) {
        super(content);
    }
}
