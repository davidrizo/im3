package es.ua.dlsi.grfia.im3ws.muret.service.impl;

import es.ua.dlsi.grfia.im3ws.muret.entity.User;
import es.ua.dlsi.grfia.im3ws.muret.repository.UserRepository;
import es.ua.dlsi.grfia.im3ws.muret.service.UserService;
import es.ua.dlsi.grfia.im3ws.service.impl.CRUDServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends CRUDServiceImpl<User, Integer, UserRepository> implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    protected UserRepository initRepository() {
        return userRepository;
    }
}

