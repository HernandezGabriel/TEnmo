package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
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

    //Uses restTemplate.exchange to retrieve and Account obj belonging to the user
    public Account getMyAccount(AuthenticatedUser user) {
        Account account = null;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl + "MyAccount", HttpMethod.GET, getAuthenticationEntity(user), Account.class);
            account = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());
//            BasicLogger.log(e.getMessage());

        }
        return account;
    }

    //Uses restTemplate.exchange to retrieve the balance of the account belonging to the user
    //necessary because we made the design choice not to include balance
    //in the account model to avoid displaying other user's balance
    public long getMyBalance(AuthenticatedUser user) {
        Long balance = null;
        try {
            ResponseEntity<Long> response =
                    restTemplate.exchange(baseUrl + "MyBalance", HttpMethod.GET, getAuthenticationEntity(user), Long.class);

            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());
//            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    //Returns an account obj:{Account ID and User:{User ID and Username}} using restTemplate.exchange
    //takes a URL parameter corresponding to a UserID
    //I.e. we can retrieve the username and account id using user id
    public Account findAccountFromUserId(int userId, AuthenticatedUser user) {
        Account a = null;
        boolean success = false;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl + "Account?userId=" + userId, HttpMethod.GET, getAuthenticationEntity(user), Account.class);

            a = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());
//            BasicLogger.log(e.getMessage());
        }
        return a;
    }

    //accepts a long and returns true or false
    //uses getMyBalance to receive an updated balance to compare
    public boolean hasEnoughFunds(Long amount, AuthenticatedUser user) {
        if(amount<=0){return false;}
        Long balance = getMyBalance(user);
        return balance >= amount; //simplified if else
    }

    //Sets user's token into a http entity
    private HttpEntity<Void> getAuthenticationEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }

}