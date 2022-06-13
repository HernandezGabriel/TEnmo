package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.TransferRepository;
import com.techelevator.tenmo.dao.TransferStatusRepository;
import com.techelevator.tenmo.dao.TransferTypeRepository;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.security.Principal;

@Service
@Transactional
public class TransferService {
//TODO ADD TRY CATCH

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

    public Transfer handleTransfer(@Valid Transfer t, Principal p){
        principalAccount = accountRepository.findAccountByUsername(p.getName());            //set principle account
        if(t.getAmount()<=0){return t;}         //if amount less than 0 return

        if(t.getTransferType().getTransferTypeId()==2){ return handleSend(t); } //TYPE: SEND
        else if(t.getTransferType().getTransferTypeId()==1){ //TYPE.REQUEST

            if (t.getTransferStatus().getTransferStatusId()==1){ return handleRequestPending(t); } // pending
            else if(t.getTransferStatus().getTransferStatusId()==1){ return handleRequestApproved(t); } //approved
            else if(t.getTransferStatus().getTransferStatusId()==2){ return handleRequestRejected(t); } // rejected

            else return t;
        }
        else return t;
    }

    private Transfer handleSend(Transfer t){

        if(t.getTransferType().getTransferTypeId()!=2){ return t; } //type of 2
        if(t.getTransferId()!=0){ return t; } //id has to be 0 to start
        if(!t.getAccountFrom().equals(principalAccount)){ return t; } //accnt from = principal
        if(principalAccount.getBalance()<t.getAmount()){ return t; } //then check balance
        else { //transfer funds
            //GO TO
            return transferMoney(t);
        }

    }

    private Transfer handleRequestRejected(Transfer t) {

        // denied
        //if transfer has a request and denied then you are trying to deny a pending request so
        //very if history that there is a pending request with that ID!
        Transfer verificationTransfer;
        try{
            verificationTransfer = transferRepository.findByTransferId(t.getTransferId());
        }catch (Exception e){
            return t;
        }
        if(verificationTransfer.getTransferStatus()!=statusRepo.findByTransferStatusId(1)){
            System.out.println("TS NOT PENDING! ERROR");return t;} //HAS TO BE PENDING
        if(verificationTransfer.getAccountFrom()!=principalAccount){         //verify that account FROM is principal
            System.out.println("NOT YR ACCOUNT ERROR"); return t; }
        //post transaction?
        else{
            t.setTransferStatus(statusRepo.findByTransferStatusId(3)); //rejected
        }
        return         transferRepository.save(t);
    }

    private Transfer handleRequestApproved(Transfer t) { //2
        //gets transfer from db via id
        Transfer verifyTransfer;
        try{
            verifyTransfer = transferRepository.findByTransferId(t.getTransferId());
        }catch (Exception e){
            System.out.println("HANDLE REQUEST APPROVED ERRR");
            return t;
        }
        if(verifyTransfer.getTransferStatus()!=statusRepo.findByTransferStatusId(2)){
            System.out.println("ID MUST BE PENDING TO BE APPROVED"); return t;
        }
        if(!(verifyTransfer.getAccountFrom().equals(principalAccount))){  //accnt from must = principal accnt
            System.out.println("handleRequestApproveERROR account from must be principal acount");return t;
        }
        if(verifyTransfer.getAccountTo().equals(principalAccount)){ //accnt to cant be yours if youre approving a transfer
            System.out.println("account to cant be yours when approving transfer"); return t;
        }
        if(accountRepository.findBalanceByAccountId(principalAccount.getAccountId())<verifyTransfer.getAmount()){
            System.out.println("not enough funds");return t;
        }

        return transferMoney(t);

    }

    private Transfer handleRequestPending(Transfer t) { //1

        if(t.getTransferId()!=0){return t;} //has to be 0 to initiate
        if(!(t.getAccountTo().equals(principalAccount))) {
            System.out.println("handleResquestPending() ACCNT to has to be principal"); return t;}
        if(t.getAccountFrom().equals(principalAccount)){
            System.out.println("Accnt FROM CANT BE PRINCIPAL ACCNT/ cant request money from self");  return t;}

        t.setTransferStatus(statusRepo.findByTransferStatusId(1)); //1 for pending
        //note no funds being transferred
        return transferRepository.save(t);
    }

    private Transfer transferMoney(Transfer t) {
            long senderBalance= accountRepository.findBalanceByAccountId(t.getAccountFrom().getAccountId());
            long receiverBalance= accountRepository.findBalanceByAccountId(t.getAccountTo().getAccountId());

            long senderNewAmount = senderBalance - (t.getAmount());
            long receiverNewAmount = receiverBalance + (t.getAmount());

            accountRepository.changeAmount( senderNewAmount,t.getAccountFrom().getAccountId());
            accountRepository.changeAmount( receiverNewAmount, t.getAccountTo().getAccountId());
            t.setTransferStatus(statusRepo.findByTransferStatusId(2));
            t=transferRepository.save(t);

        return t;

    }
}