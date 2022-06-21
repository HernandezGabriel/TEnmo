package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class TransferService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AccountService accountService;

    private List<Transfer> myTransferHistory=new ArrayList<>();

    public TransferService(String baseUrl, AccountService accountService) {
        this.baseUrl = baseUrl;
        this.accountService = accountService;
    }

    //Accepts a transfer, method is called after various checks have been performed on the client side
    //Uses RestTemplate.Exchange to retrieve the new Transfer if successful
    //accepts all types of transfer
    //Creates Headers with token and transfer object for exchange method
    //Returns updated transfer
    public Transfer postTransfer(AuthenticatedUser user, Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer,headers);

        Transfer returnedTransfer = null;
        try{
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseUrl+"InitTransfer", HttpMethod.POST, entity, Transfer.class );

            returnedTransfer = response.getBody();
            if(returnedTransfer.getTransferId()==0)
                System.out.println("Something went wrong");
        }catch (RestClientResponseException | ResourceAccessException e){
//            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }
        return returnedTransfer;
    }

    //creates http headers with token
    //uses RestTemplate.Exchange to get a List of Transfer where 'my' user is present
    //Sets response to myTransferHistory member of class
    //called by several members of this class to retrieve updated list of my transfers
    private void setListOfTransfers(AuthenticatedUser user){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try{
            ResponseEntity<List<Transfer>> response =
                    restTemplate.exchange(baseUrl+"/MyTransfers", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Transfer>>(){} );

            myTransferHistory = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e){
//            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }

    }

    //updates list of Transfers,
    //Transfers are sorted into 4 String builders and are neatly formatted in the process
    public String getMyTransferHistoryAsFormattedString(AuthenticatedUser user){
        setListOfTransfers(user);
        try{
            if(myTransferHistory.isEmpty())
                return "NO TRANSFERS FOUND";
        } catch (Exception e){
            return "NO TRANSFERS FOUND";
        }
        //build 2 strings for sent and received
        StringBuilder sbTransfersSent = new StringBuilder("\n");
        StringBuilder sbTransfersReceived = new StringBuilder("\n");
        StringBuilder sbRequestsSent = new StringBuilder("\n");
        StringBuilder sbRequestsReceived= new StringBuilder("\n");
        StringBuilder line= new StringBuilder("|");
        for (int i = 0; i < (52) ; i++) {
            line.append("-");
        }
        line.append("|\n");

        sbTransfersSent.append((String.format("| %-50s |\n", "TRANSFERS SENT")));
        sbTransfersReceived.append((String.format("| %-50s |\n", "TRANSFERS RECEIVED")));
        sbRequestsReceived.append((String.format("| %-50s |\n", "REQUESTS RECEIVED")));
        sbRequestsSent.append((String.format("| %-50s |\n", "REQUESTS SENT")));

        String headersFormat= "| %-4s | %-7s | %-8s | %-9s | %-10s |";

        String headers= String.format(headersFormat,"ID","TYPE", "STATUS","USER_TO","AMOUNT");
        sbTransfersSent.append(line+headers+"\n"+line);

        headers= String.format(headersFormat,"ID","TYPE","STATUS","USER_FROM","AMOUNT");
        sbTransfersReceived.append(line+headers+"\n"+line);

        headers= String.format(headersFormat,"ID","TYPE","STATUS","USER_TO","AMOUNT");
        sbRequestsReceived.append(line+headers+"\n"+line);

        headers= String.format(headersFormat,"ID","TYPE","STATUS","USER_FROM","AMOUNT");
        sbRequestsSent.append(line+headers+"\n"+line);

        int myAccountId= accountService.getMyAccount(user).getAccountId();

        for(Transfer t: myTransferHistory) {

            int acc_from_id=t.getAccountFrom().getAccountId();
            int acc_to_id=t.getAccountTo().getAccountId();
            int type_id=t.getTransferType().getTransferTypeId();
            String acc_to_username = t.getAccountTo().getUser().getUsername();
            String acc_from_username = t.getAccountFrom().getUser().getUsername();

            if (type_id == 2) { //TYPE:SEND

                if (acc_from_id == myAccountId) { //OUTGOING TRANSFERS
                    sbTransfersSent.append( String.format (headersFormat,
                            t.getTransferId(),
                            t.getTransferType().getTransferTypeDesc(),
                            t.getTransferStatus().getTransferStatusDesc(),
                            acc_to_username,
                            t.getAmount())+ "\n");

                } else if (acc_to_id == myAccountId) {   //incoming
                    sbTransfersReceived.append(String.format(headersFormat,
                            t.getTransferId(),
                            t.getTransferType().getTransferTypeDesc(),
                            t.getTransferStatus().getTransferStatusDesc(),
                            acc_from_username,
                            t.getAmount())+ "\n");
                }
            }
            if (type_id == 1) { //TYPE:REQUEST
                if (acc_from_id == myAccountId) { // INCOMING REQUESTS
                    sbRequestsReceived.append(String.format(headersFormat,
                            t.getTransferId(),
                            t.getTransferType().getTransferTypeDesc(),
                            t.getTransferStatus().getTransferStatusDesc(),
                            acc_to_username,
                            t.getAmount())+"\n");

                } else if (acc_to_id == myAccountId) {   // REQUEST SENT
                    sbRequestsSent.append(String.format(headersFormat,
                            t.getTransferId(),
                            t.getTransferType().getTransferTypeDesc(),
                            t.getTransferStatus().getTransferStatusDesc(),
                            acc_from_username,
                            t.getAmount())+"\n");
                    }
            }
        }
        sbTransfersReceived.append(line);
        sbTransfersSent.append(line);
        sbRequestsReceived.append(line);
        sbRequestsSent.append(line);

        sbTransfersSent.append(sbTransfersReceived.append(sbRequestsSent.append(sbRequestsReceived)));

        return sbTransfersSent.toString();
    }

    //gets a transfer from my transfers and performs various checks
    //first checks for null, type,  status, account from, then checks balance if approve
    // sets transfer status to boolean approve
    // uses postTransfer which will attempt writing to the server
    // returns String
    public String approveOrDenyTransfer(AuthenticatedUser user, int transferId, boolean approve ){
        Transfer t = getTransferFromMyTransfersUsingTransferID(user, transferId);
        if(t==null){
            return "Transfer: "+ transferId+" Not Found";
        }
        else if(t.getTransferType().getTransferTypeId()==2){ //TYPE has to be REQUEST
            return "Transfer: " +t.getTransferId()+" Is Not A Request Transfer";
        }
        else if(t.getTransferStatus().getTransferStatusId()!=1){ //Status has to be pending
            return "Transfer: "+t.getTransferId()+ " Is Not Pending";
        }
        else if(t.getAccountFrom().getAccountId()!=accountService.getMyAccount(user).getAccountId()){
            return "Account from must belong to you";
        }
        else {
            if (approve){
                if((accountService.getMyBalance(user)-t.getAmount())<0)
                    return "Not Enough Funds To Approve";
                t.setTransferStatus(new TransferStatus(2)); //2= approve
            }
            else {
                t.setTransferStatus(new TransferStatus(3)); //3 = DENIED
            }
            return postTransfer(user,t).toString();
        }
   }

   //get a transfer from MyTransfers and returns formatted and more detailed view.
    //uses helper method getTransferFromMyTransfersUsingTransferID to return a single transfer
    public String getTransferDetails(AuthenticatedUser user, int transferId){
            Transfer t = getTransferFromMyTransfersUsingTransferID(user, transferId);
            if (t == null) {
                return "Invalid Transfer ID";
            } else {
                String format = "\nTRANSFER_ID: %s " +
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

    //returns a single transfer from list of MyTransfers
    //updates list everytime
    //returns null if transfer doesn't exist
    private Transfer getTransferFromMyTransfersUsingTransferID (AuthenticatedUser user,int id){
        try{
            setListOfTransfers(user);
            for (Transfer t : myTransferHistory) {
                if (t.getTransferId() == id) {
                    return t;
                }
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }


}