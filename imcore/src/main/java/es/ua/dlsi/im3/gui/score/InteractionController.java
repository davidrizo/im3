package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @autor drizo
 */
public class InteractionController {
    HashMap<EventType, List<IScoreSongViewEventSubscriptor>> eventSubscriptors;

    HashMap<String, GraphicsElement> layoutElementsByID;

    public InteractionController() {
        this.layoutElementsByID = new HashMap<>();
        eventSubscriptors = new HashMap<>();
        for (EventType event: EventType.values()) {
            eventSubscriptors.put(event, new LinkedList());
        }
    }

    public void register(GraphicsElement graphicsElement) throws IM3Exception {
        if (graphicsElement.getID() == null) {
            throw new IM3Exception("The graphics element " + graphicsElement + " has not an ID");
        }
        layoutElementsByID.put(graphicsElement.getID(), graphicsElement);
    }

    /**
     *
     * @param ID
     * @return null if not found
     */
    public GraphicsElement get(String ID) {
        return layoutElementsByID.get(ID);
    }

    public void subscribe(EventType eventType, IScoreSongViewEventSubscriptor subscriptor) {
        eventSubscriptors.get(eventType).add(subscriptor);
    }


    public void unsubscribe(EventType eventType, IScoreSongViewEventSubscriptor subscriptor) {
        eventSubscriptors.get(eventType).remove(subscriptor);
    }

    public void handleEvent(EventType eventType, String ID) throws IM3Exception {
        GraphicsElement graphicsElement = get(ID);
        if (graphicsElement == null) {
            throw new IM3Exception("No graphics element with ID " + ID);
        }

        for (IScoreSongViewEventSubscriptor subscriptor: eventSubscriptors.get(eventType)) {
            subscriptor.onEvent(eventType, graphicsElement);
        }
    }


}
