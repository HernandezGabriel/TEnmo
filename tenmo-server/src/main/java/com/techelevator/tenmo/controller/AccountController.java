package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Controller To Handle Account Requests
 */

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserDao userDao;

    @GetMapping("/MyAccount")
    public Account getAccount(Principal principal){

        System.out.println("principal name"+ principal.getName());
        return accountRepository.findAccountByUserId(userDao.findIdByUsername(principal.getName()));
    }



















}
