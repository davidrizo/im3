package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.controller.StringResponse;
import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.entity.ProjectURLs;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import es.ua.dlsi.im3.core.utils.FileUtils;
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

    @Override
    protected ProjectService initService() {
        return projectService;
    }

    private File createProjectFileStructure(File parentFolder, String projectBaseName) throws IM3WSException {

        File path = new File(parentFolder, projectBaseName);
        if (path.exists()) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Path '{0}' alredy exists", path.getAbsolutePath());
            throw new IM3WSException("Path '" + projectBaseName + "' already exists in repository");
        }

        if (!path.mkdirs()) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot create path '{0}'", path.getAbsolutePath());
            throw new IM3WSException("Cannot create path '" + projectBaseName);
        }

        return path;
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
        Date now = new Date();

        String projectBaseName = FileUtils.leaveValidCaracters(project.getName()).toLowerCase();

        File muretFolder = new File(muretConfiguration.getFolder());
        if (!muretFolder.exists()) {
            muretFolder = createProjectFileStructure(null, muretConfiguration.getFolder());
        }

        File projectFolder = createProjectFileStructure(muretFolder, projectBaseName);
        createProjectFileStructure(projectFolder, MURETConfiguration.MASTER_IMAGES);
        createProjectFileStructure(projectFolder, MURETConfiguration.THUMBNAIL_IMAGES);
        createProjectFileStructure(projectFolder, MURETConfiguration.PREVIEW_IMAGES);

        Project newProject = new Project(project.getName(),
                projectBaseName,
                now,
                now,
                null,
                null,
                project.getThumbnailBase64Encoding(),
                project.getComments(),
                null,
                null
                );

        projectService.create(newProject);
        return newProject;
    }
}
