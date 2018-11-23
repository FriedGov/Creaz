package nl.ehi2vsd5.hboict.creazapp.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Koen Walterbos
 * @author Youri Tomassen
 */

public class DoItYourself {

    private static final String TAG = DoItYourself.class.getSimpleName();

    public static final int CATEGORY_ALL = -1;
    public static final int CATEGORY_HOME = 10;
    public static final int CATEGORY_BEAUTY = 20;
    public static final int CATEGORY_HOME_GARDEN_KITCHEN = 30;
    public static final int CATEGORY_SCHOOL = 40;
    public static final int CATEGORY_FAVORITES = 50;
    public static final int CATEGORY_WEEKLY_CHALLENGE = 60;

    private String title;
    private String uid, id;
    private int category;
    private long createdAt;
    private List<Page> pages = new ArrayList<>();
    private Map<String, Float> ratings = new HashMap<>();


    public static final String CHILD = "diys"; //should match the name of database table
    public static final String RATINGS = "ratings";
    public static final String COMMENTS = "comments";

    /**
     * required empty constructor for Firebase serialization
     */
    public DoItYourself() {
    }

    /**
     * @param title of the diy
     * @param uid   of the creator
     */
    public DoItYourself(String title, String uid, int categoryId, long createdAt) {
        this.title = title;
        this.uid = uid;
        this.createdAt = createdAt;
        if (!validCategory(categoryId))
            throw new RuntimeException(
                    "Invalid categoryId, use constants defined in DoItYourself class.");

        this.category = categoryId;
    }


    /**
     * @param title of the diy
     * @param uid   of the creator
     */
    public DoItYourself(String id, String title, String uid, int categoryId, long createdAt) {
        this.id = id;
        this.title = title;
        this.uid = uid;
        this.createdAt = createdAt;
        if (!validCategory(categoryId))
            throw new RuntimeException(
                    "Invalid categoryId, use constants defined in DoItYourself class.");

        this.category = categoryId;
    }

    private boolean validCategory(int categoryId) {
        return categoryId == CATEGORY_HOME
                || categoryId == CATEGORY_BEAUTY
                || categoryId == CATEGORY_HOME_GARDEN_KITCHEN
                || categoryId == CATEGORY_SCHOOL
                || categoryId == CATEGORY_WEEKLY_CHALLENGE;
    }

    /**
     * diy
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * pages
     */
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public List<Page> getPages() {
        return pages;
    }

    public Page getPage(int position) {
        return pages.get(position);
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public void addPage(Page page, int position) {
        pages.add(position, page);
    }

    public void removePage(int position) {
        pages.remove(position);
    }

    public void removePage(Page page) {
        pages.remove(page);
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    public int getCountPages() {
        return pages.size();
    }

    /**
     * Ratings
     */

    public Map<String, Float> getRatings() {
        return ratings;
    }

    public float averageRating() {
        float total = 0;
        for (Object a : ratings.keySet()) {
            total = total + ratings.get(a);
        }
        return total / ratings.size();
    }

    public int totalRating(){

        return ratings.size();
    }

    public void setRatings(Map<String, Float> ratings) {
        this.ratings = ratings;
    }

    /**
     * Categories
     */

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * createdAt
     */

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
