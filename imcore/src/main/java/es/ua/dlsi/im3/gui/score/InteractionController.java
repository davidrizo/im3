package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @autor drizo
 */
public class InteractionController {
    HashMap<EventType, HashMap<InteractionElementType, List<IScoreSongViewEventSubscriptor>>> eventSubscriptors;

    HashMap<String, GraphicsElement> layoutElementsByID;

    public InteractionController() {
        this.layoutElementsByID = new HashMap<>();
        eventSubscriptors = new HashMap<>();
        for (EventType event: EventType.values()) {
            HashMap<InteractionElementType, List<IScoreSongViewEventSubscriptor>> map = new HashMap<>();
            for (InteractionElementType interactionElementType: InteractionElementType.values()) {
                map.put(interactionElementType, new LinkedList<>());
            }
            eventSubscriptors.put(event, map);
        }
    }

    public void register(GraphicsElement graphicsElement) throws IM3Exception {
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

    public void subscribe(IScoreSongViewEventSubscriptor subscriptor, EventType eventType, InteractionElementType ... interactionElementTypes) {
        for (InteractionElementType interactionElementType: interactionElementTypes) {
            eventSubscriptors.get(eventType).get(interactionElementType).add(subscriptor);
        }
    }

    public void unsubscribe(IScoreSongViewEventSubscriptor subscriptor, EventType eventType, InteractionElementType ... interactionElementTypes) {
        for (InteractionElementType interactionElementType: interactionElementTypes) {
            eventSubscriptors.get(eventType).get(interactionElementType).remove(subscriptor);
        }
    }

    public void handleEvent(EventType eventType, String ID) throws IM3Exception {
        GraphicsElement graphicsElement = get(ID);
        if (graphicsElement == null) {
            throw new IM3Exception("No graphics element with ID " + ID);
        }

        InteractionElementType interactionElementType = graphicsElement.getInteractionElementType();

        for (IScoreSongViewEventSubscriptor subscriptor: eventSubscriptors.get(eventType).get(interactionElementType)) {
            subscriptor.onEvent(eventType, graphicsElement);
        }
    }
}
