package com.automationanywhere.botcommand.samples.Utils;

public class SNOWServer {


    public String getToken() {
        return Token;
    }

    public String getURL() {
        return Url;
    }


    String Token;
    String Url;


    public SNOWServer(String Url, String Token){
        this.Url = Url;
        this.Token = Token;
    }
}