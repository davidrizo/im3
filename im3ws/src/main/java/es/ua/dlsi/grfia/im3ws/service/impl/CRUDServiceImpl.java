package es.ua.dlsi.grfia.im3ws.service.impl;

import es.ua.dlsi.grfia.im3ws.service.ICRUDService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author drizo
 */
@Service
public abstract class CRUDServiceImpl<EntityType, PrimaryKeyType, BaseJPARepositoryType extends JpaRepository<EntityType, PrimaryKeyType>> implements ICRUDService<EntityType, PrimaryKeyType> {
    BaseJPARepositoryType repository;

    protected BaseJPARepositoryType getRepository() {
        synchronized (CRUDServiceImpl.class) {
            if (repository == null) {
                repository = initRepository();
            }
        }
        return repository;
    }

    protected abstract BaseJPARepositoryType initRepository();

    @Override
    public EntityType create(EntityType entity) {
        return getRepository().save(entity);
    }

    @Override
    public EntityType delete(PrimaryKeyType id) {
        Optional<EntityType> user = findById(id);
        if(user.isPresent()){
            getRepository().delete(user.get());
        }
        return user.get();
    }

    @Override
    public List<EntityType> findAll() {
        return getRepository().findAll();
    }


    @Override
    public Optional<EntityType> findById(PrimaryKeyType id) {
        return getRepository().findById(id);
    }

    @Override
    public EntityType update(EntityType entity) {
        return null;
    }
}
