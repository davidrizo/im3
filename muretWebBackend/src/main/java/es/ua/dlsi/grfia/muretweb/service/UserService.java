package es.ua.dlsi.grfia.muretweb.service;


import es.ua.dlsi.grfia.muretweb.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User user);
    Optional<User> findById(int id);
    User update(User user);
    User delete(int id);
    List findAll();
}
