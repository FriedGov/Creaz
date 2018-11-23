package nl.ehi2vsd5.hboict.creazapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Koen Walterbos
 * @author Youri Tomassen
 * @author Creaz
 */

public class User {

    public static final String CHILD = "user_data";
    public static final String FAVORITES = "favorites";

    private String displayName;
    private long createdAt;
    private long birthDate;
    private int rank;
    private String photoUrl;

    public User() {

    }

    public User(String displayName, long createdAt, long birthDate) {
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.birthDate = birthDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
