package es.ua.dlsi.im3.omr.muret.editpage;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.muret.DashboardController;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;

import java.util.List;

public interface IPagesController {
    void setPages(OMRPage omrPage, List<OMRPage> pagesToOpen) throws IM3Exception;

    void setDashboard(DashboardController dashboardController);
}
