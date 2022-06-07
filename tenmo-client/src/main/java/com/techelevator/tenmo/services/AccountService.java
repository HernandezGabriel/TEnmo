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
      //  HttpEntity<AuthenticatedUser> entity = createAuthenticatedUserEntity(user);

      //added these 3 lines
       HttpHeaders headers = new HttpHeaders();
       headers.setBearerAuth(user.getToken());
       HttpEntity<Void> entity = new HttpEntity<>(headers);


        boolean success=false;
        try{
            ResponseEntity<Account> response =
                    restTemplate.exchange(baseUrl+"MyAccount", HttpMethod.GET, entity, Account.class );

            this.account=response.getBody();
            success=true;

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
    }

    public long getBalance(){
        return account.getBalance();

    }

//    private HttpEntity<AuthenticatedUser> createAuthenticatedUserEntity(AuthenticatedUser user) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return new HttpEntity<>(user, headers);
//    }
}
