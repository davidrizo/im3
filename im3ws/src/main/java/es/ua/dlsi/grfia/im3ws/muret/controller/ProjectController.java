package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.muret.entity.Project;
import es.ua.dlsi.grfia.im3ws.muret.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) // angular
@RequestMapping("/project")
@RestController
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @PostMapping
    public Project create(@RequestBody Project project){
        return projectService.create(project);
    }

    @GetMapping(path = {"/{id}"})
    public Optional<Project> findOne(@PathVariable("id") int id){
        return projectService.findById(id);
    }

    @PutMapping
    public Project update(@RequestBody Project project){
        return projectService.update(project);
    }

    @DeleteMapping(path ={"/{id}"})
    public Project delete(@PathVariable("id") int id) {
        return projectService.delete(id);
    }

    @GetMapping
    public List findAll(){
        return projectService.findAll();
    }
}
