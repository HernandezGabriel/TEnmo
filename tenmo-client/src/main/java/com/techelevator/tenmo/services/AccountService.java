package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private Account account;

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private void setAccount(AuthenticatedUser user){

        //Sets user's token in a http entity
       HttpHeaders headers = new HttpHeaders();
       headers.setBearerAuth(user.getToken());
       HttpEntity<Void> entity = new HttpEntity<>(headers);


        boolean success=false;
        try{
            //GET request
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl+"MyAccount", HttpMethod.GET, entity, Account.class );

            //sets account to response
            this.account=response.getBody();
            success=true;

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
    }


    //after setAccount()
    public long getBalance(AuthenticatedUser user){
        setAccount(user);
        return account.getBalance();

    }

    public Account getMyAccount(AuthenticatedUser user){
        setAccount(user);
        return account;
    }

    //make a get request to the server to obtain accountId from a username
    public int findAccountIdFromUserId(int userId, AuthenticatedUser user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        int accountId=0;
        boolean success=false;
        try{
            //GET request
            ResponseEntity<Integer> response =
                    restTemplate.exchange(baseUrl+"AccountId?userId="+userId, HttpMethod.GET, entity, Integer.class );

            //sets accountId to response
            accountId=response.getBody();
            success=true;

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return accountId;
    }

    public boolean hasEnoughFunds(Long amount, AuthenticatedUser user){
        //going to get most recent account info
        setAccount(user);
        if(account.getBalance()<amount){return false;}
        if(account.getBalance()>=amount){return true;}
        return false;
    }

    public String findUsernameFromAccountID(int accountId, AuthenticatedUser user){
        //Sets user's token in a http entity
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String username="";
        try{
            //GET request
            ResponseEntity<String> response =
                    restTemplate.exchange(baseUrl+"Username?accountId="+accountId, HttpMethod.GET, entity, String.class);

            //sets account to response
            username=response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

        return username;
    }

    public Map<Integer,String> getAccountIdsAndUsernames(AuthenticatedUser user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        Map<Integer,String > map = null;
        try{
            //GET request
            ResponseEntity<Map<Integer,String>> response =
                    restTemplate.exchange(baseUrl + "AccountIdsAndUsernames", HttpMethod.GET, entity, new ParameterizedTypeReference<Map<Integer, String>>() {
                    });

            //sets account to response
            map=response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            System.out.println(e.getMessage());
            //BasicLogger.log(e.getMessage());
        }

        return map;

    }

}
