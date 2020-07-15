package com.mervynm.nom.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_HOMEMADE = "homemade";
    public static final String KEY_PRICE = "price";
    public static final String KEY_LIKE_COUNT = "likeCount";
    public static final String KEY_LOCATION = "location";

    public Post() {}

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser author) {
        put(KEY_AUTHOR, author);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public JSONArray getTags() {
        return getJSONArray(KEY_TAGS);
    }

    public void setTags(JSONArray tags) {
        put(KEY_TAGS, tags);
    }

    public Boolean getHomemade() {
        return getBoolean(KEY_HOMEMADE);
    }

    public void setHomemade(Boolean homemade) {
        put(KEY_HOMEMADE, homemade);
    }

    public double getPrice() {
        return getDouble(KEY_PRICE);
    }

    public void setPrice(double price) {
        put(KEY_PRICE, price);
    }

    public int getLikeCount() {
        return getInt(KEY_LIKE_COUNT);
    }

    public void setLikeCount(int likeCount) {
        put(KEY_LIKE_COUNT, likeCount);
    }

    public ParseObject getLocation() {
        return getParseObject(KEY_LOCATION);
    }

    public void setLocation(ParseObject location) {
        put(KEY_LOCATION, location);
    }
}
