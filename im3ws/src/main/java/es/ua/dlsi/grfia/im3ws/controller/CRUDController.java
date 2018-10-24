package es.ua.dlsi.grfia.im3ws.controller;

import com.fasterxml.jackson.annotation.JsonView;
import es.ua.dlsi.grfia.im3ws.muret.entity.JSONFilteredDataViews;
import es.ua.dlsi.grfia.im3ws.service.ICRUDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * It uses JsonView to filter JSON results. When using findAll it just returns the fields in the entity object annotated with @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class).
 * The method findOne returns all objects and relationships. Note that SpringBoot + JPA ignore the LAZY or EAGER JPA annotations
 * @author drizo
 */
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) // angular
public abstract class CRUDController<EntityType, PrimaryKeyType, CRUDServiceType extends ICRUDService<EntityType, PrimaryKeyType>> {
    CRUDServiceType service;

    private CRUDServiceType getService() {
        synchronized (CRUDController.class) {
            if (service == null) {
                service = initService();
            }
        }
        return service;
    }

    protected abstract CRUDServiceType initService();

    @PostMapping
    public EntityType create(@RequestBody EntityType entity){
        return getService().create(entity);
    }

    @GetMapping(path = {"/{id}"})
    public Optional<EntityType> findOne(@PathVariable("id") PrimaryKeyType id){
        Optional<EntityType> result = getService().findById(id);
        System.out.println(result);
        return result;
    }

    @PutMapping
    public EntityType update(@RequestBody EntityType user){
        return getService().update(user);
    }

    @DeleteMapping(path ={"/{id}"})
    public EntityType delete(@PathVariable("id") PrimaryKeyType id) {
        return getService().delete(id);
    }

    /**
     * It just returns the fields annotated with @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
     * @return
     */
    @JsonView(JSONFilteredDataViews.ObjectWithoutRelations.class)
    @GetMapping
    public List<EntityType> findAll(){
        return getService().findAll();
    }
}
