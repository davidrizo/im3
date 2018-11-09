package es.ua.dlsi.grfia.im3ws.muret.model;

import es.ua.dlsi.grfia.im3ws.IM3WSException;
import es.ua.dlsi.grfia.im3ws.muret.MURETConfiguration;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import es.ua.dlsi.im3.core.utils.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to be able to work also with command line
 */
public class ProjectModel {
    ProjectService projectService;
    MURETConfiguration muretConfiguration;

    public ProjectModel(ProjectService projectService, MURETConfiguration muretConfiguration) {
        this.projectService = projectService;
        this.muretConfiguration = muretConfiguration;
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

    public Project newProject(Project project) throws IM3WSException {
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
                project.getComposer(),
                now,
                now,
                null,
                null,
                project.getThumbnailBase64Encoding(),
                project.getComments(),
                null,
                project.getNotationType(),
                null,
                null
        );

        return projectService.create(newProject);
    }
}
