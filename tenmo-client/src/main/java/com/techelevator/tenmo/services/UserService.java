package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    private  List<User> usersList = new ArrayList();
    public UserService(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    private void setListOfUsers(AuthenticatedUser user){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        boolean success=false;
        try{
            //GET request
            ResponseEntity<List<User>> response =
                    restTemplate.exchange(baseUrl + "Users", HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
                    });

            //sets account to response
            usersList = response.getBody();
            success=true;

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }

    }

    public String getListOfUsersAsString(AuthenticatedUser user){
        //getting the most updated list of users
        setListOfUsers(user);

        StringBuilder sb = new StringBuilder();
        sb.append("ID | Username \n");

        for(User u: usersList){
            sb.append(u.getUserId()+ " | " +u.getUsername() +"\n");
        }


        return sb.toString();
    }

    public boolean idExists(int id){
        for(User u: usersList){
            if(u.getUserId().equals(((long) id))){
                return true;
            }
        }
        return false;
    }

    public String getUsernameById(int id){
        for(User u : usersList){
            if(u.getUserId().equals(id)){
                return u.getUsername();
            }
        }

        return "";

    }
}
