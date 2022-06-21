package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.TransferRepository;
import com.techelevator.tenmo.dao.TransferStatusRepository;
import com.techelevator.tenmo.dao.TransferTypeRepository;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.security.Principal;

@Service
@Transactional
public class TransferService {
//TODO ADD TRY CATCH -> throw http status exceptions
    //todo add http responses

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private Account principalAccount;

    @Autowired
    TransferStatusRepository statusRepo;
    @Autowired
    TransferTypeRepository typeRepo;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Transfer> handleTransfer(@Valid Transfer t, Principal p){
        principalAccount = accountRepository.findAccountByUsername(p.getName());            //set principle account
        if(t.getAmount()<=0){return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);}         //if amount less than 0 return

        if(t.getTransferType().getTransferTypeId()==2){ return handleSend(t); } //TYPE: SEND
        else if(t.getTransferType().getTransferTypeId()==1){ //TYPE.REQUEST

            if (t.getTransferStatus().getTransferStatusId()==1){ return handleRequestPending(t); } // pending
            else if(t.getTransferStatus().getTransferStatusId()==2){ return handleRequestApproved(t); } //approved
            else if(t.getTransferStatus().getTransferStatusId()==3){ return handleRequestRejected(t); } // rejected

            else return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Transfer> handleSend(Transfer t){

        if(t.getTransferType().getTransferTypeId()!=2){ return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);} //type of 2
        if(t.getTransferId()!=0){ return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST); } //id has to be 0 to start
        if(!t.getAccountFrom().equals(principalAccount)){ return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST); } //account from = principal
        if(principalAccount.getBalance()<t.getAmount()){ return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST); } //then check balance
        else { //transfer funds
            //GO TO

            t.setTransferType(typeRepo.findByTransferTypeId(2)); //type: send
            return transferMoney(t);
        }

    }



    private ResponseEntity<Transfer> handleRequestRejected(Transfer t) {

        // denied
        //if transfer has a request and denied then you are trying to deny a pending request so
        //very if history that there is a pending request with that ID!
        Transfer verificationTransfer;
        try{
            verificationTransfer = transferRepository.findByTransferId(t.getTransferId());
        }catch (Exception e){
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        if(verificationTransfer.getTransferStatus()!=statusRepo.findByTransferStatusId(1)){
            System.out.println("TS NOT PENDING! ERROR");return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);} //HAS TO BE PENDING
        if(verificationTransfer.getAccountFrom()!=principalAccount){         //verify that account FROM is principal
            System.out.println("NOT YR ACCOUNT ERROR"); return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST); }
        //post transaction?
        else{
            t.setTransferStatus(statusRepo.findByTransferStatusId(3)); //rejected
            t.setTransferType(typeRepo.findByTransferTypeId(1)); //request
        }
        return new ResponseEntity<>(transferRepository.save(t), HttpStatus.CREATED);
    }

    private ResponseEntity<Transfer> handleRequestApproved(Transfer t) {
        //2 gets transfer from db via id
        Transfer verifyTransfer;
        try{
            verifyTransfer = transferRepository.findByTransferId(t.getTransferId());
        }catch (Exception e){
            System.out.println("HANDLE REQUEST APPROVED ERROR");
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        if(verifyTransfer.getTransferStatus()!=statusRepo.findByTransferStatusId(1)){
            System.out.println("ID MUST BE PENDING TO BE APPROVED"); return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        if(!(verifyTransfer.getAccountFrom().equals(principalAccount))){  //account from must = principal account
            System.out.println("handleRequestApproveERROR account from must be principal account");return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        if(verifyTransfer.getAccountTo().equals(principalAccount)){ //account to can't be yours if you're approving a transfer
            System.out.println("account to cant be yours when approving transfer"); return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
        if(accountRepository.findBalanceByAccountId(principalAccount.getAccountId())<verifyTransfer.getAmount()){
            System.out.println("not enough funds");return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }
//        t.setTransferStatus(statusRepo.findByTransferStatusId(2));
        t.setTransferType(typeRepo.findByTransferTypeId(1)); //request

        return transferMoney(t);

    }

    private ResponseEntity<Transfer> handleRequestPending(Transfer t) { //1

        if(t.getTransferId()!=0){
            System.out.println("handleRequestPending() T_ID CANT EXIStS MUST BE 0");return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);} //has to be 0 to initiate
        if(!(t.getAccountTo().equals(principalAccount))) {
            System.out.println("handleRequestPending() ACCOUNT to has to be principal"); return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);}
        if(t.getAccountFrom().equals(principalAccount)){
            System.out.println("Account FROM CANT BE PRINCIPAL ACCOUNT/ cant request money from self");  new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);}

        t.setTransferStatus(statusRepo.findByTransferStatusId(1)); //1 for pending
        t.setTransferType(typeRepo.findByTransferTypeId(1)); // request
        //note no funds being transferred
        return new ResponseEntity<>(transferRepository.save(t), HttpStatus.CREATED);
    }




    private ResponseEntity<Transfer> transferMoney(Transfer t) {

            long senderBalance= accountRepository.findBalanceByAccountId(t.getAccountFrom().getAccountId());
            long receiverBalance= accountRepository.findBalanceByAccountId(t.getAccountTo().getAccountId());

            long senderNewAmount = senderBalance - (t.getAmount());
            long receiverNewAmount = receiverBalance + (t.getAmount());

            accountRepository.changeAmount( senderNewAmount,t.getAccountFrom().getAccountId());
            accountRepository.changeAmount( receiverNewAmount, t.getAccountTo().getAccountId());

            t.setTransferStatus(statusRepo.findByTransferStatusId(2)); //status = approved
            //t.setTransferType(typeRepo.findByTransferTypeId(2)); //type = send
            t=transferRepository.save(t);

        return new ResponseEntity<>(t, HttpStatus.CREATED);

    }
}