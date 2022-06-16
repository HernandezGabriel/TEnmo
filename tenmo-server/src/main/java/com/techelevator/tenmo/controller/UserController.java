package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/Users")
    public ResponseEntity<List<User>> listUsers(){
        try {
            List<User> list = userDao.findUserIdAndUsername();
            if(list.isEmpty()){
                return new ResponseEntity(null, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(list,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //return list;

    }
}
