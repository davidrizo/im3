package es.ua.dlsi.im3.omr.interactive.pageedit;

public class Event<ContentType> {
    ContentType content;

    public Event(ContentType content) {
        this.content = content;
    }

    public Event() {
    }

    public ContentType getContent() {
        return content;
    }
}
