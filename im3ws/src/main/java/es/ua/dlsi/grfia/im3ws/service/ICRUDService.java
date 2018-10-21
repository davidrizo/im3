package es.ua.dlsi.grfia.im3ws.service;

import java.util.List;
import java.util.Optional;

public interface ICRUDService<EntityType, PrimaryKeyType> {
    EntityType create(EntityType user);
    Optional<EntityType> findById(PrimaryKeyType id);
    EntityType update(EntityType user);
    EntityType delete(PrimaryKeyType id);
    List<EntityType> findAll();
}
