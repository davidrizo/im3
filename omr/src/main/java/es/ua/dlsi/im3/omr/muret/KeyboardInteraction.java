package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.useractionlogger.ActionLogger;
import es.ua.dlsi.im3.omr.old.mensuraltagger.loggeractions.UserActionsPool;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.t;

public class KeyboardInteraction {
    private final Scene scene;
    HashMap<EventType<KeyEvent>, EventHandler<KeyEvent>> actionHashMap;

    public KeyboardInteraction(Scene scene) {
        this.scene = scene;
        actionHashMap = new HashMap<>();
    }

    public void addInteraction(EventType<KeyEvent> event, IAction action) throws IM3Exception {
        if (actionHashMap.containsKey(event)) {
            throw new IM3Exception("The event " + event + " is already registered");
        }

        EventHandler<KeyEvent> eventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                action.run();
            }
        };
        actionHashMap.put(event, eventHandler);
        scene.addEventFilter(event, eventHandler);
    }

    public void removeInteraction(EventType<KeyEvent> event) {
        actionHashMap.remove(event);
        scene.removeEventFilter(event, actionHashMap.get(event));
    }
}
