package com.srinivas.biowax;

public class Collection {

    String receipt_number,receipt_date;
    int id;




    public Collection(int id,String receipt_number, String receipt_date) {
        this.receipt_number = receipt_number;
        this.receipt_date = receipt_date;
        this.id=id;
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
