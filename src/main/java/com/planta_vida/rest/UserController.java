package com.planta_vida.rest;

import com.planta_vida.Constantes.Contants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.planta_vida.service.UserService;
import com.planta_vida.util.Utils;

import java.util.Map;

@RestController
@RequestMapping(path="/user")

public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);

        } catch (Exception exception) {
            exception.printStackTrace();
            return Utils.getResponseEntity(Contants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap) {
        try {
            return userService.login(requestMap);

        }catch (Exception exception) {
            exception.printStackTrace();
        }
        return Utils.getResponseEntity(Contants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



