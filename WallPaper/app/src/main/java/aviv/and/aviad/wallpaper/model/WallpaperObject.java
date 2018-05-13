package aviv.and.aviad.wallpaper.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Aviad on 16/04/2018.
 */

public class WallpaperObject extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String url;

    public WallpaperObject(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public WallpaperObject() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
