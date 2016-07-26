package com.app.tesis.eduardo.tesis.entities;

/**
 * Created by Eduardo on 14/07/2016.
 */
public class Citizen {

    private Integer id;
    private String facebookId;
    private String fullname;
    private String email;
    private String sessionToken;
    private String premiumUntil;

    public Integer getId() {
        return id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getPremiumUntil() {
        return premiumUntil;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setPremiumUntil(String premiumUntil) {
        this.premiumUntil = premiumUntil;
    }
}
