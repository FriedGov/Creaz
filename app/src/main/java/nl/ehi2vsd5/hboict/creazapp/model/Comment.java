package nl.ehi2vsd5.hboict.creazapp.model;

/**
 * Created by Lexar on 10/17/2017.
 */

public class Comment {
    private String uid, comment;
    private long createdAt;

    public Comment(){

    }

    public Comment(String uid, String comment, long createdAt) {
        this.uid = uid;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String userId) {
        this.uid = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
