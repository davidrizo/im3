package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.controller.CRUDController;
import es.ua.dlsi.grfia.im3ws.muret.entity.User;
import es.ua.dlsi.grfia.im3ws.muret.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author drizo
 */
@RequestMapping("/muret/user")
@RestController
public class UserController extends CRUDController<User, Integer, UserService> {
    @Autowired
    UserService userService;

    @Override
    protected UserService initService() {
        return userService;
    }
}
