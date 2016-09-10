package com.app.tesis.eduardo.tesis.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eduardo on 14/07/2016.
 */
public class Citizen implements Parcelable {

    private Integer id;
    private String facebookId;
    private String fullname;
    private String email;
    private String sessionToken;
    private Boolean postAsAnonymous;
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
    public Boolean getPostAsAnonymous() {
        return postAsAnonymous;
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

    public void setPostAsAnonymous(Boolean postAsAnonymous) {
        this.postAsAnonymous = postAsAnonymous;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
