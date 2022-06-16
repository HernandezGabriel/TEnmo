package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<Account> getAccount(Principal principal){
        try {
            Account myAccount = accountRepository.findAccountByUsername(principal.getName());
            if(myAccount.equals(null)){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(myAccount,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/MyBalance")
    public ResponseEntity<Long> getMyBalance(Principal principal){
        try {
            Long balance = accountRepository.findAccountByUsername(principal.getName()).getBalance();
            if(balance.equals(null)){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(balance,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        return accountRepository.findAccountByUsername(principal.getName()).getBalance();

    }

    @GetMapping("/Account")  //"/AccountId?userId=1002
    public ResponseEntity<Account> getAccountFromUserId(@RequestParam int userId){
        try {
            Account account = accountRepository.findAccountByUserId(userId);
            if(account.equals(null)){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(account,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        return accountRepository.findAccountByUserId(userId);
    }

}
