package es.ua.dlsi.im3.omr.muret;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * All controllers have as parent the dashboard
 * @autor drizo
 */
public abstract class MuretAbstractControllerOLD implements Initializable {
    protected DashboardController dashboard;

    public DashboardController getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardController dashboard) {
        this.dashboard = dashboard;
    }

    public abstract Node getRoot();

    @FXML
    private void handleClose() {
        dashboard.openImagesView();
    }
}
