package nl.ehi2vsd5.hboict.creazapp.model;

/**
 * @author Youri Tomassen
 */
public class Page {

    public static final String DIY_PAGES = "pages";

    private String description;
    private String photoUrl;
    private int rating;
    private int seqNo;

    public Page() {
        //required empty constructor
    }

    public Page(String description, String photoUrl, int seqNo) {
        this.description = description;
        this.photoUrl = photoUrl;
        this.seqNo = seqNo;
    }

    public Page(String description, String photoUrl, int rating, int seqNo) {
        this.description = description;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.seqNo = seqNo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }
}
