package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.RGBA;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @autor drizo
 */
public class InteractionPresenter {
    RGBA defaultColor = new RGBA(0,0,0,1);
    RGBA hoverColor = new RGBA(0,0,1,1);
    RGBA selectedColor = new RGBA(1,0,0,1);


    private final HashMap<EventType, HashMap<InteractionElementType, List<IScoreSongViewEventSubscriptor>>> eventSubscriptors;

    private final HashMap<String, GraphicsElement> layoutElementsByID;

    private List<GraphicsElement> selectedElements;

    public InteractionPresenter() {
        this.layoutElementsByID = new HashMap<>();
        eventSubscriptors = new HashMap<>();
        for (EventType event: EventType.values()) {
            HashMap<InteractionElementType, List<IScoreSongViewEventSubscriptor>> map = new HashMap<>();
            for (InteractionElementType interactionElementType: InteractionElementType.values()) {
                map.put(interactionElementType, new LinkedList<>());
            }
            eventSubscriptors.put(event, map);
        }

        selectedElements = new LinkedList<>();
    }

    public void register(GraphicsElement graphicsElement) {
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

    /**
     * It removes previous selection
     * @param elementsToSelect
     */
    public void selectElements(GraphicsElement ... elementsToSelect) {
        clearSelection();

        for (GraphicsElement elementToSelect: elementsToSelect) {
            selectedElements.add(elementToSelect);
            elementToSelect.setRGBColor(selectedColor);
        }
    }

    /**
     * It does not remove previous selection
     * @param elementsToSelect
     */
    public void selectMoreElements(GraphicsElement ... elementsToSelect) {
        for (GraphicsElement elementToSelect: elementsToSelect) {
            selectedElements.add(elementToSelect);
            elementToSelect.setRGBColor(selectedColor);
        }
    }

    public void onMouseEntererd(GraphicsElement graphicsElement) {
        graphicsElement.setRGBColor(hoverColor);
    }

    public void onMouseExited(GraphicsElement graphicsElement) {
        if (selectedElements.contains(graphicsElement)) {
            graphicsElement.setRGBColor(selectedColor);
        } else {
            graphicsElement.setRGBColor(defaultColor);
        }
    }

    public List<GraphicsElement> getSelectedElements() {
        return selectedElements;
    }

    public void clearSelection() {
        for (GraphicsElement graphicElement : selectedElements) {
            graphicElement.setRGBColor(defaultColor);
        }

        selectedElements.clear();
    }

    //TODO Seleccionar varios elements
}
