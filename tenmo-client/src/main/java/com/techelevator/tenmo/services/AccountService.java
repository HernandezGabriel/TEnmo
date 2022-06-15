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

    public Account getMyAccount(AuthenticatedUser user) {
        Account account = null;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl + "MyAccount", HttpMethod.GET, getAuthenticationEntity(user), Account.class);

            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());

            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public long getMyBalance(AuthenticatedUser user) {
        Long balance = null;
        try {
            ResponseEntity<Long> response =
                    restTemplate.exchange(baseUrl + "MyBalance", HttpMethod.GET, getAuthenticationEntity(user), Long.class);

            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Account findAccountFromUserId(int userId, AuthenticatedUser user) {

        Account a = null;
        boolean success = false;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl + "Account?userId=" + userId, HttpMethod.GET, getAuthenticationEntity(user), Account.class);

            a = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e.toString() + e.getMessage() + e.getStackTrace());

            BasicLogger.log(e.getMessage());
        }
        return a;
    }

    public boolean hasEnoughFunds(Long amount, AuthenticatedUser user) {
        if(amount<=0){return false;}
        Long balance = getMyBalance(user);
        if (balance < amount) {
            return false;
        }
        if (balance >= amount) {
            return true;
        }
        return false;
    }

    private HttpEntity<Void> getAuthenticationEntity(AuthenticatedUser user) {
        //Sets user's token in a http entity
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }

}