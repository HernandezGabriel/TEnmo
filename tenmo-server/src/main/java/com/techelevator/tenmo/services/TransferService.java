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
import org.springframework.web.client.ResourceAccessException;

import javax.validation.Valid;
import java.security.Principal;


//Called via Transfer Controller to handle all transfers
//Starts in the only public method: handleTransfer
//uses transfer repo to change account amounts and to fetch existing transfers for validation
//uses account repo for validation
@Service
@Transactional
public class TransferService {

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


    //accepts a transfer object
    //sends transfer to corresponding method using TYPE & STATUS
    //Returns a Response Entity
    //Uses accountRepo to set principalAccount class member
    public ResponseEntity<Transfer> handleTransfer(@Valid Transfer t, Principal p) {
        try {
            principalAccount = accountRepository.findAccountByUsername(p.getName());  //set principle account
            //if amount less than 0 return
            if (t.getAmount() <= 0)
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

            //if accounts are the same
            if (t.getAccountFrom().getAccountId() == t.getAccountTo().getAccountId())
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

            //TYPE: SEND
            if (t.getTransferType().getTransferTypeId() == 2)
                return handleSend(t);

            else if (t.getTransferType().getTransferTypeId() == 1) { //TYPE.REQUEST

                if (t.getTransferStatus().getTransferStatusId() == 1) {// pending
                    return handleRequestPending(t);
                } else if (t.getTransferStatus().getTransferStatusId() == 2) { //approved
                    return handleRequestApproved(t);
                } else if (t.getTransferStatus().getTransferStatusId() == 3) {// rejected
                    return handleRequestRejected(t);
                }
            }
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

        } catch (ResourceAccessException e) {
            return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //if type is send,
    //id must be 0 to begin, account from must be equal to principal account, and must have enough balance
    //calls on transferMoney() to handle transferring of funds and writing to repo
    //also uses typeRepo to get a complete type object from the DB
    //note that status is ignored for type send
    private ResponseEntity<Transfer> handleSend(Transfer t) {

        try {

            if (t.getTransferId() != 0)
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

            if (!t.getAccountFrom().equals(principalAccount))
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

            if (principalAccount.getBalance() < t.getAmount()) //then check balance
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST); //transfer funds

            t.setTransferType(typeRepo.findByTransferTypeId(2)); //type: send

            return transferMoney(t);

        }catch (ResourceAccessException e){
            return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Transfer type = request and Status = denied
    //Uses transferRepo to fetch the Transfer that the user is attempting reject (verificationTransfer)
    //meaning only the id from the client side transfer is ever used
    //Checks status on server side transfer to make sure it is currently pending
    //checks account from belongs to user.
    //finally, uses transfer repo to save the transfer with new status
    private ResponseEntity<Transfer> handleRequestRejected(Transfer t) {
        Transfer verificationTransfer;
        try {
            verificationTransfer = transferRepository.findByTransferId(t.getTransferId());
        } catch (Exception e) {
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }

        try {
        if (verificationTransfer.getTransferStatus() != statusRepo.findByTransferStatusId(1)) { //Status has to be pending
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        }

        if (verificationTransfer.getAccountFrom() != principalAccount) { //verify that account FROM is principal
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
        } else {
            t.setTransferStatus(statusRepo.findByTransferStatusId(3)); //rejected
            t.setTransferType(typeRepo.findByTransferTypeId(1)); //request
        }

            return new ResponseEntity<>(transferRepository.save(t), HttpStatus.CREATED);

        } catch (ResourceAccessException e) {
            return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Transfer type = request and Status = approved
    //Uses transferRepo to fetch the Transfer that the user is attempting to approve (verificationTransfer)
    //meaning only the id from the client side transfer is ever used
    //Checks status on server side transfer to make sure it is currently pending
    //checks account from belongs to user.
    //checks account to isn't yours
    //finally, uses transferMoney() to handle moving funds
    private ResponseEntity<Transfer> handleRequestApproved(Transfer t) {
        Transfer verifyTransfer;
        try {
            verifyTransfer = transferRepository.findByTransferId(t.getTransferId());
        }catch (ResourceAccessException e ) {
            return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);

        }
        try {
            if (verifyTransfer.getTransferStatus() != statusRepo.findByTransferStatusId(1)) {
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
            }

            if (!(verifyTransfer.getAccountFrom().equals(principalAccount))) {  //account from must = principal account
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
            }

            if (verifyTransfer.getAccountTo().equals(principalAccount)) { //account to can't be yours if you're approving a transfer
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
            }

            if (accountRepository.findBalanceByAccountId(principalAccount.getAccountId()) < verifyTransfer.getAmount()) {
                return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
            }

            t.setTransferType(typeRepo.findByTransferTypeId(1)); //request

            return transferMoney(t);

        } catch (ResourceAccessException e) {
            return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        //Transfer type = request and Status = pending
        //ID has to be 0 because user is trying to insert into table not update
        //if acctTo doesn't belong to user -> bad request
        //if accFrom does belong to user -> bad request
        //checks accountTo to make sure it doesn't belong to user
        //finally, uses transfer repo to save the transfer with new status
        private ResponseEntity<Transfer> handleRequestPending (Transfer t){ //1
            try {
                if (t.getTransferId() != 0) { //ID has to be 0 to begin
                    return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
                }
                if (!(t.getAccountTo().equals(principalAccount))) {
                    return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
                }

                if (t.getAccountFrom().equals(principalAccount)) {
                    return new ResponseEntity<>(t, HttpStatus.BAD_REQUEST);
                }

                t.setTransferStatus(statusRepo.findByTransferStatusId(1)); //1 for pending
                t.setTransferType(typeRepo.findByTransferTypeId(1)); // request

                return new ResponseEntity<>(transferRepository.save(t), HttpStatus.CREATED);

            } catch (ResourceAccessException e) {
                return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        //uses account repo to retrieve both balances from both accounts
        //uses account repo to update both balances accordingly
        //sets status to approve and uses transfer repo to save transfer
        //returns response entity with updated transfer
        private ResponseEntity<Transfer> transferMoney (Transfer t){
            try {

                long senderBalance = accountRepository.findBalanceByAccountId(t.getAccountFrom().getAccountId());
                long receiverBalance = accountRepository.findBalanceByAccountId(t.getAccountTo().getAccountId());

                long senderNewAmount = senderBalance - (t.getAmount());
                long receiverNewAmount = receiverBalance + (t.getAmount());

                accountRepository.changeAmount(senderNewAmount, t.getAccountFrom().getAccountId());
                accountRepository.changeAmount(receiverNewAmount, t.getAccountTo().getAccountId());

                t.setTransferStatus(statusRepo.findByTransferStatusId(2)); //status = approved


                return new ResponseEntity<>(transferRepository.save(t), HttpStatus.CREATED);

            } catch (ResourceAccessException e) {
                return new ResponseEntity<>(t, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
