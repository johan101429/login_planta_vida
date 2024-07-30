package com.planta_vida.service.impl;

import com.planta_vida.Constantes.Contants;
import com.planta_vida.dao.UserDAO;
import com.planta_vida.security.CustomerDetailsService;
import com.planta_vida.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.planta_vida.pojo.User;
import com.planta_vida.service.UserService;
import com.planta_vida.util.Utils;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service

public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private JwtUtil jwtUtil;




    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Sign Up User {}",requestMap);
        try {
            if(validateSignUp(requestMap)){
                User user = userDAO.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDAO.save(getUserFromMap(requestMap));
                    return Utils.getResponseEntity("Usuario registrado con exito!!!!", HttpStatus.CREATED);
               }
                else{
                    return Utils.getResponseEntity(" El usuario con ese email ya existe"
                , HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return Utils.getResponseEntity(Contants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

        } catch(Exception exception){
           exception.printStackTrace();
        }
        return Utils.getResponseEntity(Contants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro de login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (authentication.isAuthenticated()) {
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),
                            customerDetailsService.getUserDetail().getRole())+"\"}", HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"mensaje\":\""+" Espere la aprobacion del Administrador "+"\"}",HttpStatus.BAD_REQUEST);
                }
            }

        }catch (Exception exception){
            log.error("{}",exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\""+" Credenciales Incorrectas "+"\"}",HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUp(Map<String, String> requestMap) {
        if(requestMap.containsKey("nombre")&& requestMap.containsKey("telefono")&& requestMap.containsKey("email")
                && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap){
        User user=new User();
        user.setNombreCompleto(requestMap.get("nombre"));
        user.setTelefono(requestMap.get("telefono"));
        user.setEmail(requestMap.get("email"));
        user.setDireccionCompleta(requestMap.get("direccion"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

}

