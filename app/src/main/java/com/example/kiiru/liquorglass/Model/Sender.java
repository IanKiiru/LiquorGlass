package com.example.kiiru.liquorglass.Model;

/**
 * Created by Kiiru on 11/13/2017.
 */

public class Sender {
    public String to;
    Notification notification;

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }
}
