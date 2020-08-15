package es.ua.dlsi.im3.core.metadata;

public enum PersonRoles {
    COMPOSER("Composer"),
    EDITOR("Editor"),
    ENCODER("Encoder");

    String title;

    PersonRoles(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
