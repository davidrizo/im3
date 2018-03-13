package es.ua.dlsi.im3.gui.score;

import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public interface IScoreSongViewEventSubscriptor {
    void onEvent(EventType eventType, GraphicsElement graphicsElement);
}
