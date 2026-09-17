package com.icklist.model;

public class Transaction {
    private String userId;
    private String transactionId;
    private String date;
    private Double amount;
    private String merchant;
    private String category;
    private String uploadBatchId;

    public Transaction() {
    }

    public Transaction(String userId, String transactionId, String date, Double amount, String merchant, String category, String uploadBatchId) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.merchant = merchant;
        this.category = category;
        this.uploadBatchId = uploadBatchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }
}
