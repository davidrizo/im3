package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.entity.ProjectURLs;
import es.ua.dlsi.grfia.im3ws.muret.model.ProjectModel;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo
 */
@RequestMapping("/muret/project")
@RestController
public class ProjectController extends CRUDController<Project, Integer, ProjectService> {
    @Autowired
    ProjectService projectService;

    @Autowired
    MURETConfiguration muretConfiguration;

    ProjectModel projectModel;

    public ProjectController() {
    }

    @Override
    protected ProjectService initService() {
        projectModel = new ProjectModel(projectService, muretConfiguration);
        return projectService;
    }


    /**
     * It obtains the public path where thumbnails are to be found. We use this method rather than a service returning the images themselves
     * to avoid the server computing things that can be done by the web server
     * @param id
     * @return
     * @throws IM3WSException
     */
    @GetMapping(path = {"/projectURLS/{id}"})
    public ProjectURLs constructThumbnailsURL(@PathVariable("id") Integer id) throws IM3WSException {
        Optional<Project> project = projectService.findById(id);
        if (!project.isPresent()) {
            throw new IM3WSException("Cannot find a project with id " + id);
        }

        return new ProjectURLs(
                muretConfiguration.getUrl() + "/" + project.get().getPath() + "/" + MURETConfiguration.MASTER_IMAGES,
                muretConfiguration.getUrl() + "/" + project.get().getPath() + "/" + MURETConfiguration.THUMBNAIL_IMAGES,
                muretConfiguration.getUrl() + "/" + project.get().getPath() + "/" + MURETConfiguration.PREVIEW_IMAGES);
    }

    @PostMapping(path = {"/new"})
    public Project newProject(@RequestBody Project project) throws IM3WSException {
        return projectModel.newProject(project);
    }

    /* Getters and setters used in command line */
    public MURETConfiguration getMuretConfiguration() {
        return muretConfiguration;
    }

    public void setMuretConfiguration(MURETConfiguration muretConfiguration) {
        this.muretConfiguration = muretConfiguration;
    }
}
