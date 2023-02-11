package com.albertukrida.a412020031_projectuas;

import java.util.Date;

public class ClassTransaction {
    private String TransactionID;
    private String UserID;
    private String Type;
    private String Category;
    private String Amount;
    private String Description;
    private Date date;
    private String Month;
    private String Year;

    public ClassTransaction(){

    }

    public ClassTransaction(String TransactionID, String UserID, String Type, String Category, String Amount,
                            String Description, Date date, String Month, String Year){
        this.TransactionID = TransactionID;
        this.UserID = UserID;
        this.Type = Type;
        this.Category = Category;
        this.Amount = Amount;
        this.Description = Description;
        this.date = date;
        this.Month = Month;
        this.Year = Year;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
