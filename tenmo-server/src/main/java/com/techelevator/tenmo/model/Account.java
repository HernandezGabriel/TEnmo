package com.techelevator.tenmo.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @NotNull(message = "AccountId Cannot be empty")
    int accountId;

    @NotNull
    int userId;

    @NotNull(message = "balance cannot be negative or empty")
    long balance;

    public Account() {
    }

    public Account(int accountId, int userId, long balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
