package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class OMRPageListCell extends ListCell<OMRPage> {
    @Override
    public void updateItem(OMRPage item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            ImageView imageView = new ImageView();
            if (item.imageProperty().get() == null) {
                throw new IM3RuntimeException("The image of the page is null");
            }
            imageView.imageProperty().bind(item.imageProperty());
            imageView.fitWidthProperty().bind(this.widthProperty());
            imageView.setPreserveRatio(true);

            final Text text = new Text();
            //text.textProperty().bind(item.tagsFileProperty().asString());
            // TODO: 9/10/17 Añadir una descripción
            text.setScaleX(1.5);
            text.setScaleY(1.5);
            StackPane panel = new StackPane(imageView, text);
            setGraphic(panel);
        }

    }
}
