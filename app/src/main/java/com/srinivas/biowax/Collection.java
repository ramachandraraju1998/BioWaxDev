package com.srinivas.biowax;

public class Collection {

    String receipt_number,receipt_date;

    public Collection(String receipt_number, String receipt_date) {
        this.receipt_number = receipt_number;
        this.receipt_date = receipt_date;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }

    public void setReceipt_date(String receipt_date) {
        this.receipt_date = receipt_date;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public String getReceipt_date() {
        return receipt_date;
    }
}
