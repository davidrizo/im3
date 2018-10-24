package es.ua.dlsi.grfia.im3ws.muret.controller;


import com.fasterxml.jackson.annotation.JsonView;
import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author drizo
 */
@RequestMapping("/muret/project")
@RestController
public class ProjectController extends CRUDController<Project, Integer, ProjectService> {
    @Autowired
    ProjectService projectService;


    @Override
    protected ProjectService initService() {
        return projectService;
    }
}
