package es.ua.dlsi.grfia.im3ws.muret.service.impl;

import es.ua.dlsi.grfia.im3ws.muret.entity.Preferences;
import es.ua.dlsi.grfia.im3ws.muret.repository.PreferencesRepository;
import es.ua.dlsi.grfia.im3ws.muret.service.PreferencesService;
import es.ua.dlsi.grfia.im3ws.service.impl.CRUDServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author drizo
 */
@Service
public class PreferencesServiceImpl extends CRUDServiceImpl<Preferences, Integer, PreferencesRepository> implements PreferencesService {
    @Autowired
    PreferencesRepository preferencesRepository;

    @Override
    protected PreferencesRepository initRepository() {
        return preferencesRepository;
    }
}

