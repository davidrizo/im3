package es.ua.dlsi.im3.omr.interactive.editpage;

import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;

import java.util.List;

public interface IPagesController {
    void setPages(OMRPage omrPage, List<OMRPage> pagesToOpen);

    void setDashboard(DashboardController dashboardController);
}
