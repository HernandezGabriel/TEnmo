package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/AccountId")  //"/AccountId?UserId=?"
    public Integer getAccountIdFromUserId(@RequestParam int userId){
        Account returnedAccount = accountRepository.findAccountByUserId(userId);
        return returnedAccount.getAccountId();
    }

    @GetMapping("/Username") // Username?accountId=?
    public String getUsernameFromAccountId(@RequestParam int accountId){
        int userId=accountRepository.findAccountByAccountId(accountId).getUserId();
        return userDao.findUsernameById(userId);
    }

    @GetMapping("/AccountIdsAndUsernames")
    public Map<Long, String> getAccountIdsAndUsernames(){
        List<Account> listOfAccounts = accountRepository.findAll();
        List<User> list = userDao.findUserIdAndUsername();
        Map<Long,String > map= new HashMap<>();


        //worried because it's accessing the db in a for loop.
        // I believe this could be avoided using annotations
        for(Account a: listOfAccounts){
            map.put((long) a.getAccountId(),getUsernameFromAccountId(a.getAccountId()));
        }
        return map;
    }



















}
