package es.ua.dlsi.im3.omr.interactive.editpage;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.gui.javafx.JavaFXUtils;
import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;

/**
 * Contains a list of polymorphic PageViews inside a ScrollPane with the possibility of focusing a page
 * @param <PageViewType>
 */
public abstract class PageBasedController<PageViewType extends Node> implements Initializable, IPagesController {
    @FXML
    protected ToolBar toolbarImages;
    @FXML
    protected VBox vboxPages;

    @FXML
    protected ScrollPane scrollPane;

    protected DashboardController dashboard;

    protected HashMap<OMRPage, PageViewType> pages;
    private PageViewType selectedPageView;


    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardController getDashboard() {
        return dashboard;
    }

    @Override
    public void setPages(OMRPage omrPage, List<OMRPage> pagesToOpen) {
        pages = new HashMap<>();

        // create the buttons
        createImageViews(pagesToOpen, omrPage);
    }

    private void createImageViews(List<OMRPage> pagesToOpen, OMRPage selectedOMRPage) {
        selectedPageView = null;
        for (OMRPage omrPage : pagesToOpen) {
            PageViewType pageView = createPageView(omrPage, this, vboxPages.widthProperty());

            pages.put(omrPage, pageView);
            vboxPages.getChildren().add(pageView);
            if (omrPage == selectedOMRPage) {
                selectedPageView = pageView;
            }

            Button btn = new Button(omrPage.toString());
            toolbarImages.getItems().add(btn);
            btn.setOnAction(event -> {
                focus(pageView);
            });
        }

        focus(selectedPageView);
    }

    protected abstract PageViewType createPageView(OMRPage omrPage, PageBasedController<PageViewType> pageViewTypePageBasedController, ReadOnlyDoubleProperty widthProperty);

    private void focus(PageViewType selectedPageView) {
        boolean found = false;
        for (Node node : vboxPages.getChildren()) {
            if (node == selectedPageView) {
                JavaFXUtils.ensureVisibleY(scrollPane, node);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IM3RuntimeException("Cannot find the selected page");
        }
    }


}
