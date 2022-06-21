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

    //Creates headers with token
    //Uses restTemplate.exchange to retrieve List<users> (id & username)
    //Sets userList member to response
    private void setListOfUsers(AuthenticatedUser user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try{
            ResponseEntity<List<User>> response =
                    restTemplate.exchange(baseUrl + "Users", HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {
                    });

            usersList = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
    }

    //updates userList member
    //returns formatted string of list
    public String getListOfUsersAsString(AuthenticatedUser user){

        setListOfUsers(user);

        StringBuilder sb = new StringBuilder();
        sb.append("ID  | Username \n");

        for(User u: usersList){
            sb.append(u.getUserId()+ " | " +u.getUsername() +"\n");
        }
        return sb.toString();
    }

    //checks list for id and returns boolean
    public boolean idExists(int id){
        for(User u: usersList){
            if(u.getUserId()== id){
                return true;
            }
        }
        return false;
    }

    //returns username by id from list or null if id doesn't exist
    public String getUsernameById(int id){
        for(User u : usersList){
            if(u.getUserId()==(id)){
                return u.getUsername();
            }
        }
        return null;

    }
}
