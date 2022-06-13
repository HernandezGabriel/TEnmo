package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private HttpEntity<Void> getAuthenticationEntity(AuthenticatedUser user){
        //Sets user's token in a http entity

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }

    public long getMyBalance(AuthenticatedUser user){
        Long balance= null;
        try{
            ResponseEntity<Long> response =
                    restTemplate.exchange(baseUrl+"MyBalance", HttpMethod.GET, getAuthenticationEntity(user), Long.class );

            balance=response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Account findAccountFromUserId(int userId, AuthenticatedUser user){

        Account a=null;
        boolean success=false;
        try{
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl+"Account-userId?userId="+userId, HttpMethod.GET, getAuthenticationEntity(user), Account.class );

            a=response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return a;
    }

    public Account getMyAccount(AuthenticatedUser user){
        Account account = null;
        try{
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl+"MyAccount", HttpMethod.GET, getAuthenticationEntity(user), Account.class );

            account=response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public boolean hasEnoughFunds(Long amount, AuthenticatedUser user){
        Long balance = getMyBalance(user);
        if(balance<amount){return false;}
        if(balance>=amount){return true;}
        return false;
    }



    //    }
//
//        return map;
//        }
//            //BasicLogger.log(e.getMessage());
//            System.out.println(e.getMessage());
//        }catch (RestClientResponseException | ResourceAccessException e){
//
//            map=response.getBody();
//            //sets account to response
//
//                    });
//                    restTemplate.exchange(baseUrl + "AccountIdsAndUsernames", HttpMethod.GET, getAuthenticationEntity(user), new ParameterizedTypeReference<Map<Integer, String>>() {
//            ResponseEntity<Map<Integer,String>> response =
//            //GET request
//        try{
//        Map<Integer,String > map = null;
//    public Map<Integer,String> getAccountIdsAndUsernames(AuthenticatedUser user){
//    }
//        return username;
//        }
//            BasicLogger.log(e.getMessage());
//        }catch (RestClientResponseException | ResourceAccessException e){
//
//            username=response.getBody();
//
//                    restTemplate.exchange(baseUrl+"Username?accountId="+accountId, HttpMethod.GET, getAuthenticationEntity(user), String.class);
//            ResponseEntity<String> response =
//            //GET request
//        try{
//        String username=null;
//    public String findUsernameFromAccountID(int accountId, AuthenticatedUser user){
//
//    public Account getAccountWithAccountID(int accountId,AuthenticatedUser user){
//        Account newAccount=null;
//        try{
//            //GET request
//            ResponseEntity<Account> response =
//                    restTemplate.exchange(baseUrl+"Account-accountId?accountId="+accountId, HttpMethod.GET, getAuthenticationEntity(user), Account.class );
//
//            newAccount =response.getBody();
//
//        }catch (RestClientResponseException | ResourceAccessException e){
//            BasicLogger.log(e.getMessage());
//        }
//        return newAccount;
//    }
}
