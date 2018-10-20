package es.ua.dlsi.grfia.muretweb.service.impl;

import es.ua.dlsi.grfia.muretweb.entity.User;
import es.ua.dlsi.grfia.muretweb.repository.UserRepository;
import es.ua.dlsi.grfia.muretweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository repository;

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User delete(int id) {
        Optional<User> user = findById(id);
        if(user.isPresent()){
            repository.delete(user.get());
        }
        return user.get();
    }

    @Override
    public List findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public User update(User user) {
        return null;
    }}
