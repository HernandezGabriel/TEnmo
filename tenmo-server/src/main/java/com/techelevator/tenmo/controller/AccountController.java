package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
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

    @GetMapping("/AccountId")  //"/AccountId?userId=1002
    public int getAccountIdFromUserId(@RequestParam int userId){
       // Account returnedAccount = accountRepository.findAccountByUserId(userId);
    return accountRepository.findAccountIdByUserId(userId);
//        return returnedAccount.getAccountId();
    }


    //could be more secure with joins/annotations??
    @GetMapping("/Username") // Username?accountId=?
    public String getUsernameFromAccountId(@RequestParam int accountId){
//        int userId=accountRepository.findAccountByAccountId(accountId).getUserId();
        int userId= (int) accountRepository.findUserIdByAccountId(accountId);
        return userDao.findUsernameByUserId(userId);
    }

    @GetMapping("/AccountIdsAndUsernames")
    public Map<Integer, String> getAccountIdsAndUsernames(){
       // List<Account> listOfAccounts = accountRepository.findAll();
        //List<User> listOfUsers = userDao.findUserIdAndUsername();

        List<Object[]> list = accountRepository.findAccountIdAndUsername();
        Map<Integer,String > map = new HashMap<>();

        for(Object[] ob:list){
            map.put((Integer) ob[0], (String) ob[1]);
            System.out.println(ob[0]);
            System.out.println(ob[1]);

        }
//        map=accountRepository.findAccountIdAndUsername();

        //System.out.println(map.toString());

        //worried because it's accessing the db in a for loop.
        // I believe this could be avoided using annotations
//        for(Account a: listOfAccounts){
//            map.put((long) a.getAccountId(),getUsernameFromAccountId(a.getAccountId()));
//        }
        return map;
    }



















}
