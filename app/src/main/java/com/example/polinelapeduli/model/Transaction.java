package com.example.polinelapeduli.model;

import org.jetbrains.annotations.NotNull;

public class Transaction {
    private int transactionId;
    private int userId;
    private int donationId;
    private int amount;
    private String createdAt;

    public Transaction() {}

    public Transaction(int transactionId, int userId, int donationId, int amount, String createdAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.donationId = donationId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    @NotNull
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", donationId=" + donationId +
                ", amount=" + amount +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

