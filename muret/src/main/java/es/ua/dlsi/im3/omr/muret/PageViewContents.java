package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.collections.ObservableListViewSetModelLink;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;

import java.util.function.Function;

/**
 * @autor drizo
 */
class PageViewContents implements Comparable<PageViewContents> {
    OMRPage omrPage;
    PageView pageView;
    Group regionViewsGroup;
    ObservableListViewSetModelLink<OMRRegion, RegionView> regions;

    public PageViewContents(DocumentAnalysisSymbolsController controller, OMRPage page) {
        this.omrPage = page;
        this.pageView = new PageView("Page" + page.hashCode(), controller, page, DocumentAnalysisSymbolsController.PAGE_COLOR);
        regionViewsGroup = new Group();

        regions = new ObservableListViewSetModelLink<OMRRegion, RegionView>(page.regionsProperty(), new Function<OMRRegion, RegionView>() {
            @Override
            public RegionView apply(OMRRegion omrRegion) {
                return new RegionView("Region" + omrRegion.hashCode(), controller, pageView, omrRegion, DocumentAnalysisSymbolsController.REGION_COLOR);
            }
        });

        regionViewsGroup.getChildren().setAll(regions.getViews());
        regions.getViews().addListener(new ListChangeListener<RegionView>() {
            @Override
            public void onChanged(Change<? extends RegionView> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (RegionView regionView: c.getRemoved()) {
                            regionViewsGroup.getChildren().remove(regionView);
                        }
                    } else if (c.wasAdded()) {
                        for (RegionView regionView: c.getAddedSubList()) {
                            regionViewsGroup.getChildren().add(regionView);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int compareTo(PageViewContents o) {
        return omrPage.compareTo(o.omrPage);
    }
}