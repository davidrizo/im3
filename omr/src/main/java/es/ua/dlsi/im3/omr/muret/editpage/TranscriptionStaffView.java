package es.ua.dlsi.im3.omr.muret.editpage;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;

/**
 * It contains the excerpt of the manuscript, the digital score staff and the transduced score staff.
 * It may contain the associated lyrics
 */
public class TranscriptionStaffView extends VBox {
    private static final double MARGINS = 10; // added to fit the symbol rectangle handles (tiradores), it could be parametrized
    private static final double SEPARATION = 10;
    protected ImageView manuscriptStaffExcerptView;
    protected Pane manuscriptPane;
    protected Pane transcriptionPane;
    private ImageView manuscriptLyricsExcerptView;
    private Pane staffLyricsPane;
    protected OMRPage page;
    protected OMRRegion staffRegion;
    private OMRRegion lyricsRegion;

    public TranscriptionStaffView(OMRPage page, OMRRegion region) {
        super(SEPARATION);
        this.page = page;
        this.staffRegion = region;

        manuscriptPane = new Pane();
        this.getChildren().add(manuscriptPane);

        // TODO: 16/2/18 Que el tirador se pueda salir del panel para cubrir todo el símbolo
        manuscriptStaffExcerptView = extractRegion(region);
        manuscriptPane.setMinHeight(2 * MARGINS + manuscriptStaffExcerptView.getImage().getHeight());
        manuscriptPane.setMinWidth(2 * MARGINS + manuscriptStaffExcerptView.getImage().getWidth());
        manuscriptPane.getChildren().add(manuscriptStaffExcerptView);

        transcriptionPane = new Pane();
        transcriptionPane.setMinHeight(300); //TODO
        this.getChildren().add(transcriptionPane);
    }

    public void setLyricsRegion(OMRRegion lyricsRegion) {
        this.lyricsRegion = lyricsRegion;
        manuscriptLyricsExcerptView = extractRegion(lyricsRegion);
        this.getChildren().add(manuscriptLyricsExcerptView);
        staffLyricsPane = new Pane();
        staffLyricsPane.getChildren().add(new Label("TO-DO QUITAR: aquí el lyrics"));
        staffLyricsPane.setMinHeight(50); //TODO
        this.getChildren().add(staffLyricsPane);
    }

    private ImageView extractRegion(OMRRegion region) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        try {
            BufferedImage bufferedImage = page.getBufferedImage().getSubimage(
                    (int) region.getFromX(), (int) region.getFromY(),
                    (int) region.getWidth(), (int) region.getHeight());
            imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        } catch (Throwable t) {
            throw new IM3RuntimeException("Cannot extract image from region " + region.toString() + ": " + t.getMessage());
        }
        return imageView;
    }
}
