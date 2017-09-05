package es.ua.dlsi.im3.core.metadata;

public enum PersonRoles {
    COMPOSER("Composer"),
    EDITOR("Editor");

    String title;

    PersonRoles(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
