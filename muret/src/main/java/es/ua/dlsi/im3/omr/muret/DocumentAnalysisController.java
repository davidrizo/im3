package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class DocumentAnalysisController extends MuRETBaseController {
    private static final Color PAGE_COLOR = Color.BLUE; //TODO
    private static final Color REGION_COLOR = Color.RED;

    @FXML
    ScrollPane scrollPane;

    @FXML
    AnchorPane imagePane;

    ImageView imageView;

    /**
     * Use this one to browse
     */
    SortedList<PageContents> pageViews;
    /**
     * But add to the underlying list
     */
    LinkedList<PageContents> pageViews_data;

    Group pageViewsGroup;

    class PageContents {
        OMRPage omrPage;
        PageView pageView;
        Group regionViewsGroup;
        /**
         * Use this one to browse
         */
        SortedList<RegionView> regionViews;
        /**
         * But add to the underlying list
         */
        LinkedList<RegionView> regionViews_data;

        public PageContents(OMRPage page) {
            this.omrPage = page;
            this.pageView = new PageView("Page" + page.hashCode(), DocumentAnalysisController.this, page, PAGE_COLOR);
            regionViewsGroup = new Group();
            imagePane.getChildren().add(regionViewsGroup);
            pageViewsGroup.getChildren().add(pageView);

            regionViews_data = new LinkedList<>();
            regionViews = new SortedList<>(FXCollections.observableList(regionViews_data), new Comparator<RegionView>() {
                @Override
                public int compare(RegionView o1, RegionView o2) {
                    return o1.owner.compareTo(o2.owner);
                }
            });

            for (OMRRegion region: page.regionsProperty()) {
                RegionView regionView = new RegionView("Region" + region.hashCode(), DocumentAnalysisController.this, pageView, region, REGION_COLOR);
                regionViewsGroup.getChildren().add(regionView);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        imagePane.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    @Override
    protected void bindZoom(Scale scaleTransformation) {
        scaleTransformation.pivotXProperty().bind(imagePane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(imagePane.layoutYProperty());
        imagePane.getTransforms().add(scaleTransformation);
    }

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;

        imageView = new ImageView(omrImage.getImage());
        imageView.setPreserveRatio(true);
        imagePane.setMinWidth(imageView.getImage().getWidth());
        imagePane.setMinHeight(imageView.getImage().getHeight());
        imagePane.getChildren().add(imageView);

        handleZoomToFit();
        loadData();
    }

    private void loadData() {
        pageViews_data = new LinkedList<>();
        pageViews = new SortedList<>(FXCollections.observableList(pageViews_data), new Comparator<PageContents>() {
            @Override
            public int compare(PageContents o1, PageContents o2) {
                return o1.omrPage.compareTo(o2.omrPage);
            }
        });

        pageViewsGroup = new Group();
        imagePane.getChildren().add(pageViewsGroup);
        for (OMRPage page: omrImage.getPages()) {
            PageContents pageContents = new PageContents(page);
            pageViews_data.add(pageContents);
        }
    }

    @Override
    protected double computeZoomToFitRatio() {
        double xRatio = this.scrollPane.getViewportBounds().getWidth() / this.imageView.getLayoutBounds().getWidth();
        double yRatio = this.scrollPane.getViewportBounds().getHeight() / this.imageView.getLayoutBounds().getHeight();
        if (xRatio > yRatio) {
            return xRatio;
        } else {
            return yRatio;
        }
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {

    }

    @Override
    public void unselect() {

    }

    @Override
    public ISelectable first() {
        return null;
    }

    @Override
    public ISelectable last() {
        return null;
    }

    @Override
    public ISelectable previous(ISelectable s) {
        return null;
    }

    @Override
    public ISelectable next(ISelectable s) {
        return null;
    }
}
