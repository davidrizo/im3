package es.ua.dlsi.im3.omr.interactive.pages;

import es.ua.dlsi.im3.omr.interactive.model.ImageFile;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PageThumbnailView {
    private final Label label;
    ImageFile imageFile;

    BorderPane borderPane;
    Rectangle image; //TODo Cambiar por el ImageView de ImageFile
    /**
     * Used to reorder using drag and drop
     */
    Rectangle leftDropbox;
    Rectangle rightDropbox;


    public PageThumbnailView(ImageFile imageFile) {
        this.imageFile = imageFile;
        borderPane = new BorderPane();
        borderPane.setUserData(this);
        image = new Rectangle(150, 100);
        image.setFill(Color.BLUE);

        label = new Label(imageFile.toString());

        borderPane.setBottom(label);
        borderPane.setCenter(image);

        leftDropbox = createDropbox();
        rightDropbox = createDropbox();
        borderPane.setLeft(leftDropbox);
        borderPane.setRight(rightDropbox);
    }

    private Rectangle createDropbox() {
        Rectangle r = new Rectangle(20, 100);
        r.setFill(Color.TRANSPARENT);
        return r;
    }

    public Node getRoot() {
        return borderPane;
    }

    public Rectangle getLeftDropbox() {
        return leftDropbox;
    }

    public Rectangle getRightDropbox() {
        return rightDropbox;
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void updateLabel() {
        label.setText(imageFile.toString());
    }
}
