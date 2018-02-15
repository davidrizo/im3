package es.ua.dlsi.im3.omr.interactive.editpage.regions;

import es.ua.dlsi.im3.omr.interactive.editpage.RegionBaseView;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;


public class RegionEditView extends RegionBaseView<RegionEditPageView> {

    public RegionEditView(RegionEditPageView pageView, OMRRegion omrRegion) {
        super(pageView, omrRegion);
    }

    protected void onRegionMouseClicked(MouseEvent event) {
        pageView.handleEvent(new RegionEditEvent(event, this));
    }

    protected void onLabelContextMenuRequested(ContextMenuEvent event) {
        showRegionTypeContextMenu(event.getScreenX(), event.getScreenY());
    }

    public void showRegionTypeContextMenu(double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();
        for (RegionType regionType: RegionType.values()) {
            MenuItem menuItem = new MenuItem(regionType.name());
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                //TODO Comando para poder deshacer
                omrRegion.setRegionType(regionType);
                contextMenu.hide();
            });
        }
        contextMenu.show(label, screenX, screenY);
    }

    public void beginEdit() {
        //this.getParent().requestFocus();
        rectangle.setStrokeWidth(3);
        rectangle.beginEdit();
    }

    public void acceptEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(true);
    }

    public void cancelEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(false);
    }

}
