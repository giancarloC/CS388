package com.example.duckdating.ui.match;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String EMAIL = "email";
    public static final String MATCH_EMAIL = "matchEmail";
    public static final String BODY_KEY = "body";

    public String getEmail() {
        return getString(EMAIL);
    }

    public String getMatchEmail(){
        return getString(MATCH_EMAIL);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public void setMatchEmail(String email){
        put(MATCH_EMAIL, email);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}
