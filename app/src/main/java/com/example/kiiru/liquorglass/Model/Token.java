package com.example.kiiru.liquorglass.Model;

/**
 * Created by Kiiru on 11/13/2017.
 */

public class Token {
    private String token;
    private boolean isMerchantToken;

    public Token() {
    }

    public Token(String token, boolean isMerchantToken) {
        this.token = token;
        this.isMerchantToken = isMerchantToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMerchantToken() {
        return isMerchantToken;
    }

    public void setMerchantToken(boolean merchantToken) {
        isMerchantToken = merchantToken;
    }
}
