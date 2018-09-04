package es.ua.dlsi.im3.omr.muret.old;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 * Used because javafx handles key events at scene level
 */
public class KeyEventManager {
    Scene scene;
    EventHandler<KeyEvent> currentKeyEventHandler;

    public KeyEventManager(Scene scene) {
        this.scene = scene;
    }

    public EventHandler<KeyEvent> getCurrentKeyEventHandler() {
        return currentKeyEventHandler;
    }

    public void setCurrentKeyEventHandler(EventHandler<KeyEvent> eventHandler) {
        if (currentKeyEventHandler != null) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, currentKeyEventHandler);
        }
        scene.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        currentKeyEventHandler = eventHandler;
    }
}
