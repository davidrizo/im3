package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.IM3Exception;

public class GUIException extends IM3Exception {
    public GUIException(String msg) {
        super(msg);
    }

    public GUIException(Exception e) {
        super(e);
    }

    public GUIException(Throwable cause) {
        super(cause);
    }

    public GUIException(String message, Throwable cause) {
        super(message, cause);
    }
}
