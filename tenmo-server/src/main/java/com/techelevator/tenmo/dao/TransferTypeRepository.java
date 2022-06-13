package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTypeRepository extends JpaRepository<TransferType, Integer> {

    TransferType findByTransferTypeId(int id);
    String getTransferTypeDescByTransferTypeId(int id);

}
