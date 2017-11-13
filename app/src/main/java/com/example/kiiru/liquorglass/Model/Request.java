package com.example.kiiru.liquorglass.Model;

import java.util.List;

/**
 * Created by Kiiru on 10/8/2017.
 */

public class Request {
    private String phone;
    private String fname;
    private String lname;
    private String status;
    private String total;
    private String address;
    private List<Order> Drinks; //list of drink orders

    public Request() {
    }

    public Request(String phone, String fname, String lname, String total, List<Order> drinks) {
        this.phone = phone;
        this.fname = fname;
        this.lname = lname;
        this.total = total;
        Drinks = drinks;
        this.status = "0"; // Default is 0, 0: Placed, 1: Shipping , 2: Shipped
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getDrinks() {
        return Drinks;
    }

    public void setDrinks(List<Order> drinks) {
        Drinks = drinks;
    }
}


