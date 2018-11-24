package es.ua.dlsi.grfia.im3ws.muret.repository;

import es.ua.dlsi.grfia.im3ws.muret.entity.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author drizo
 */
@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, Integer> {

}
