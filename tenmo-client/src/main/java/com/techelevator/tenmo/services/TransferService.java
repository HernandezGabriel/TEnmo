package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TransferService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AccountService accountService;

    private List<Transfer> myTransferHistory;

    public TransferService(String baseUrl, AccountService accountService) {
        this.baseUrl = baseUrl;
        this.accountService = accountService;
    }

    public Transfer postTransfer(AuthenticatedUser user, Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,headers);

        Transfer returnedTransfer = null;
        boolean success=false;
        try{
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl+"InitTransfer", HttpMethod.POST, entity, Transfer.class );

            returnedTransfer = response.getBody();

            if(returnedTransfer.getTransferId()!=0){

                success=true;
            }else{
                System.out.println("SUCCESS = false");
                System.out.println(returnedTransfer);
            }
        }catch (RestClientResponseException | ResourceAccessException e){
            //BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }

        return returnedTransfer;
    }

    private void setListOfTransfers(AuthenticatedUser user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try{
            ResponseEntity<List<Transfer>> response =
                    restTemplate.exchange(baseUrl+"/MyTransfers", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Transfer>>(){} );

            myTransferHistory = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
            //BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }

    }

    public String getMyTransferHistoryAsFormattedString(AuthenticatedUser user){
        //get most recent list of transfers by user id
        setListOfTransfers(user);

        //build 2 strings for sent and received
        StringBuilder sbSent = new StringBuilder("\n");
        StringBuilder sbReceived = new StringBuilder("\n");
        StringBuilder line= new StringBuilder();
        for (int i = 0; i < 56 ; i++) {
            line.append("_");
        }
        line.append("\n");

        sbSent.append(line);
        sbReceived.append(line);
        sbSent.append((String.format("| %-52s |\n", "TRANSFERS SENT")));
        sbReceived.append((String.format("| %-52s |\n", "TRANSFERS RECEIVED")));

        String headersFormat= "| %-10s | %-11s | %-12s | %-10s |";
        String headers= String.format(headersFormat, "STATUS","TRANSFER_ID","USER_TO","AMOUNT");
        sbSent.append(headers+"\n");

        headers= String.format(headersFormat,"STATUS","TRANSFER_ID","USER_FROM","AMOUNT");
        sbReceived.append(headers+"\n");

        sbSent.append(line);
        sbReceived.append(line);

        int myAccountId= accountService.getMyAccount(user).getAccountId();

        String username;
        for(Transfer t: myTransferHistory){
            username="NOT FOUND";

            if(t.getAccountFrom().getAccountId()==myAccountId){
                username= t.getAccountTo().getUser().getUsername();
                sbSent.append(String.format(headersFormat,
                        t.getTransferStatus().getTransferStatusDesc(), t.getTransferId(),username,t.getAmount()));
                sbSent.append("\n");
            }
            else if(t.getAccountTo().getAccountId()==myAccountId){
                username= t.getAccountFrom().getUser().getUsername();
                sbReceived.append(String.format(headersFormat,
                        t.getTransferStatus().getTransferStatusDesc(), t.getTransferId(),username,t.getAmount()));
                sbReceived.append("\n");
            }
        }

        sbReceived.append(line);
        sbSent.append(line);
        sbReceived.append(sbSent);

        return sbReceived.toString();
    }

    private Transfer getTransferFromMyTransfersUsingTransferID(AuthenticatedUser user, int id){
//        setListOfTransfers(user);
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
                    "\nAMOUNT: $ %s";

            String string = ((String.format(format,
                    t.getTransferId(),
                    t.getAccountFrom(),
                    t.getAccountTo(),
                    t.getTransferStatus(),
                    t.getTransferType(),
                    t.getAmount())));

            return string;
        }
    }

}
