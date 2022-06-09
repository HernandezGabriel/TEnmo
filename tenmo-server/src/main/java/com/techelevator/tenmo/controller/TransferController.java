package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountRepository;
import com.techelevator.tenmo.dao.TransferRepository;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private AccountRepository accountRepository;

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping("/InitTransfer")
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer){

        transferService.transferMoney(transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());
        transfer.setTransferStatusId(2); //2==approved

        return transferRepository.save(transfer);



        //needs to update both accounts using account repository
        //needs to update transfer status before posting

        //return transferRepository.save(transfer);



      //  return null;
    }

    //private





}
