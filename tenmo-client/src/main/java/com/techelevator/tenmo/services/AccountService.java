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

    private Account account;

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }



    public void setAccount(AuthenticatedUser user){

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
    public long getBalance(){
        return account.getBalance();

    }

}
