package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<User> listUsers(){

        List<User> list = userDao.findUserIdAndUsername();
        //System.out.println(list);
        return list;

    }
}
