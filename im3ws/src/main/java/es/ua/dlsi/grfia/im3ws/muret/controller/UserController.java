package es.ua.dlsi.grfia.im3ws.muret.controller;


import es.ua.dlsi.grfia.im3ws.muret.entity.User;
import es.ua.dlsi.grfia.im3ws.muret.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) // angular
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public User create(@RequestBody User user){
        return userService.create(user);
    }

    @GetMapping(path = {"/{id}"})
    public Optional<User> findOne(@PathVariable("id") int id){
        return userService.findById(id);
    }

    @PutMapping
    public User update(@RequestBody User user){
        return userService.update(user);
    }

    @DeleteMapping(path ={"/{id}"})
    public User delete(@PathVariable("id") int id) {
        return userService.delete(id);
    }

    @GetMapping
    public List findAll(){
        return userService.findAll();
    }
}
