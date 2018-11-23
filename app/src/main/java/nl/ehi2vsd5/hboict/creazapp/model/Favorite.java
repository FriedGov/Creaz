package nl.ehi2vsd5.hboict.creazapp.model;

/**
 * Created by Govert on 25-10-2017.
 */

public class Favorite {

    public static final String FAVORITES = "favorites";
    public static final String DIYID = "diyId";
    private String diyId;

    public Favorite() {
    }

    public Favorite(String diyId) {
        this.diyId = diyId;
    }

    public String getDiyId() {
        return diyId;
    }


    public void setDiyId(String diyid) {
        this.diyId = diyid;
    }
}
