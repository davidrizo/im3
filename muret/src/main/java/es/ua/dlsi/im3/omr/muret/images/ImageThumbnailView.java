package es.ua.dlsi.im3.omr.muret.images;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It contains low resolution / size versions of the image
 * @autor drizo
 */
public class ImageThumbnailView extends AnchorPane {
    private static final double PORTRAIT_WIDTH = 188;
    private static final double LANDSCAPE_WIDTH = 392;
    private static final double HEIGHT = 245;
    OMRImage omrImage;
    ImageView imageView;
    Image image;

    public ImageThumbnailView(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;
        createImageView();
    }

    private void createImageView() throws IM3Exception {
        URL url = null;
        try {
            url = omrImage.getImageFile().toURI().toURL();
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load image URL", e);
            throw new IM3Exception(e);
        }

        // resize, get resized pixels, if not, too much memory is stored in memory
        if (omrImage.getImage().getHeight() > omrImage.getImage().getWidth()) {
            image = new Image(url.toString(), PORTRAIT_WIDTH, HEIGHT, true, false);
        } else {
            image = new Image(url.toString(), LANDSCAPE_WIDTH, HEIGHT, true, false);
        }
        imageView = new ImageView(image);
        this.getChildren().add(imageView);
    }
}
