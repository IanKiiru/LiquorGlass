package com.example.kiiru.liquorglass.Model;

/**
 * Created by Kiiru on 11/13/2017.
 */

class Result {
    public String message_id;

    public Result() {
    }

    public Result(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
