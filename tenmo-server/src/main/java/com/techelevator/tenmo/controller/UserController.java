package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("IsAuthenticated()")
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/Users")
    public List<User> listUsers(){

        List<User> list = new ArrayList<User>();
        list= userDao.findUserIdAndUsername();
        return list;

    }
}
