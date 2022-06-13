package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class TransferService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AccountService accountService;
    private final UserService userService;

    private List<Transfer> myTransferHistory;

    public TransferService(String baseUrl, AccountService accountService, UserService userService) {
        this.baseUrl = baseUrl;
        this.accountService = accountService;
        this.userService = userService;
    }

    public Transfer postTransfer(AuthenticatedUser user, Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,headers);

        Transfer returnedTransfer = null;
        boolean success=false;
        try{
            //POST request
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl+"InitTransfer", HttpMethod.POST, entity, Transfer.class );

            //sets transfer to response
            returnedTransfer = response.getBody();
            if(returnedTransfer.getTransferId()!=0){

                success=true;
            }else{
                throw new ResourceAccessException("Error: Transfer not returned");
            }
        }catch (RestClientResponseException | ResourceAccessException e){
            //BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }

        return returnedTransfer;
    }

    private void setListOfTransfersByCurrentUser(AuthenticatedUser user){
        //Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try{
            //GET request
            ResponseEntity<List<Transfer>> response =
                    restTemplate.exchange(baseUrl+"/MyTransfers", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Transfer>>(){} );

            //sets myTransferHistory to response
            myTransferHistory = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e){
            //BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }

    }

    public String getMyTransferHistoryAsFormattedString(AuthenticatedUser user){

        //get most recent list of transfers by user id
        setListOfTransfersByCurrentUser(user);
        //get map of users and ids for switching to display usernames instead of account ID
        Map<Integer,String> usernameAndAccountIdMap=accountService.getAccountIdsAndUsernames(user);

        //build 2 strings for sent and received
        StringBuilder sbSent = new StringBuilder("\n");
        StringBuilder sbReceived = new StringBuilder("\n");

        //-----------------------------------
        StringBuilder line= new StringBuilder();
        for (int i = 0; i < 56 ; i++) {
            line.append("_");
        }
        line.append("\n");

        String headersFormat= "| %-10s | %-11s | %-12s | %-10s |";

        sbSent.append(line.toString());
        sbSent.append((String.format("| %-52s |\n", "TRANSFERS SENT")));

        String headers= String.format(headersFormat, "STATUS","TRANSFER_ID","USER_TO","AMOUNT");
        sbSent.append(headers+"\n");
        sbSent.append(line.toString());

        sbReceived.append(line.toString());
        sbReceived.append((String.format("| %-52s |\n", "TRANSFERS RECEIVED")));

        headers= String.format(headersFormat,"STATUS","TRANSFER_ID","USER_FROM","AMOUNT");
        sbReceived.append(headers+"\n");
        sbReceived.append(line.toString());

        //get Own user id and account id

        long myId=user.getUser().getId();
        int myAccountId= accountService.getMyAccount(user).getAccountId();
//        int myAccountId= accountService.findAccountIdFromUserId((int) myId,user);

        String status="";
        String username="";

        //going through each transaction in myTransferHistory and adding it to either String 1 or 2
        for(Transfer t: myTransferHistory){
            username="";
            //Transfer status
//            if(t.getTransferStatusId()==1){ status="Pending";}
//            else if(t.getTransferStatusId()==2){ status="Approved";}
//            else if(t.getTransferStatusId()==3){ status="Rejected";}
//            else{status="Unknown";}

            //sort transfers from sent and received
            if(t.getAccountFrom()==myAccountId){
                //find username passing account id and user for authentication
                //username=accountService.findUsernameFromAccountID(t.getAccountTo(),user);
                username= usernameAndAccountIdMap.get(t.getAccountTo());
                sbSent.append(String.format(headersFormat,
                        //status, t.getTransferId(),username,t.getAmount()));
                        t.getTransferStatus().getTransferStatusDesc(), t.getTransferId(),username,t.getAmount()));
                sbSent.append("\n");
            }
            else if(t.getAccountTo()==myAccountId){
                //find username passing account id and user for authentication
                //username=accountService.findUsernameFromAccountID(t.getAccountFrom(),user);
                username=usernameAndAccountIdMap.get(t.getAccountFrom());
                sbReceived.append(String.format(headersFormat,
//                        status, t.getTransferId(),username,t.getAmount()));
                        t.getTransferStatus().getTransferStatusDesc(), t.getTransferId(),username,t.getAmount()));
                sbReceived.append("\n");
            }
        }

        sbReceived.append(line.toString());
        sbSent.append(line.toString());

        return sbSent.toString()+sbReceived.toString();
    }

    private Transfer getTransferFromMyTransfersUsingTransferID(AuthenticatedUser user, int id){
        //no need to update since this gets called after viewing list of transfer?
        //setListOfTransfersByCurrentUser(user);
        for(Transfer t: myTransferHistory){
            if(t.getTransferId()==id){
                return t;
            }
        }
        return null;
    }

    public String getMyTransferDetails(AuthenticatedUser user, int transferId){
        Transfer t = getTransferFromMyTransfersUsingTransferID(user,transferId);
        if(t==null){
            return "Invalid Transfer ID";
        }
        else{
            String format ="\nTRANSFER_ID: %s " +
                    "\nACCOUNT FROM: %s " +
                    "\nACCOUNT TO: %s" +
                    "\nSTATUS: %s" +
                    "\nTYPE: %s " +
                    "\nAMOUNT: %s";

            String string = ((String.format(format,
                    t.getTransferId(),
                    t.getAccountFrom(),
                    t.getAccountTo(),
//                    t.getTransferStatusId(),
//                    t.getTransferTypeId(),
                    t.getTransferStatus().getTransferStatusDesc(),
                    t.getTransferType().getTransferTypeDesc(),
                    t.getAmount())));

            return string;
        }
    }

}
