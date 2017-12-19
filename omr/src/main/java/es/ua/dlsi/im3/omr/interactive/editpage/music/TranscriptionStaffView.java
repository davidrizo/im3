package es.ua.dlsi.im3.omr.interactive.editpage.music;

import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;

/**
 * It contains the excerpt of the manuscript, the digital score staff and the transduced score staff.
 * It may contain the associated lyrics
 */
public class TranscriptionStaffView extends VBox {
    private static final double SEPARATION = 10;
    private ScoreSongView scoreSongView;
    private ImageView manuscriptStaffExcerptView;
    private Pane staffScoreViewPane;
    private ImageView manuscriptLyricsExcerptView;
    private Pane manuscriptScoreViewPane;
    private OMRPage page;
    private OMRRegion staffRegion;
    private OMRRegion lyricsRegion;

    public TranscriptionStaffView(OMRPage page, OMRRegion region) {
        super(SEPARATION);
        this.page = page;
        manuscriptStaffExcerptView = extractRegion(region);
        this.getChildren().add(manuscriptStaffExcerptView);
        staffScoreViewPane = new Pane();
        staffScoreViewPane.setPrefHeight(300); //TODO
        this.getChildren().add(staffScoreViewPane);
    }

    public void setLyricsRegion(OMRRegion lyricsRegion) {
        this.lyricsRegion = lyricsRegion;
        manuscriptLyricsExcerptView = extractRegion(lyricsRegion);
        this.getChildren().add(manuscriptLyricsExcerptView);
        manuscriptScoreViewPane = new Pane();
        staffScoreViewPane.setPrefHeight(100); //TODO
        this.getChildren().add(manuscriptScoreViewPane);
    }

    private ImageView extractRegion(OMRRegion region) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        BufferedImage bufferedImage = page.getBufferedImage().getSubimage(
                (int)region.getFromX(), (int)region.getFromY(),
                (int)region.getWidth(), (int)region.getHeight());
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        return imageView;
    }
}
