package com.mervynm.nom.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_USERNAME = "username";

    public User() {}

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfileImage(ParseFile profilePicture) {
        put(KEY_PROFILE_PICTURE, profilePicture);
    }
}
