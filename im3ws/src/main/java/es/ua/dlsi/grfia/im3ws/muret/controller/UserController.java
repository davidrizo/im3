package es.ua.dlsi.grfia.im3ws.muret.controller;

import es.ua.dlsi.grfia.im3ws.muret.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequestMapping("/muret/auth")
@CrossOrigin("${angular.url}")
@RestController
public class UserController {

    @RequestMapping("login")
    public boolean login(@RequestBody User user) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Login with user '" + user.getUsername() + "'");
        return user.getUsername().equals("username") && user.getPassword().equals("password"); //TODO - see BasicAuthConfiguration
        //return user.getUsername().equals("user") && user.getPassword().equals("password"); //TODO - see BasicAuthConfiguration
    }

    @RequestMapping("user")
    public Principal user(HttpServletRequest request) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User request" + request);

        String authToken = request.getHeader("Authorization")
                .substring("Basic".length()).trim();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "... authToken = " + authToken);

        return () ->  new String(Base64.getDecoder()
                .decode(authToken)).split(":")[0];
    }
}

