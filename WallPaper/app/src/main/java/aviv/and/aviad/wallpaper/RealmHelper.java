package aviv.and.aviad.wallpaper;

import java.util.List;

import aviv.and.aviad.wallpaper.model.WallpaperObject;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Aviad on 16/04/2018.
 */

public class RealmHelper {

    private static final String WALLPAPER_EVENT_TAG = "mWallpaperTag";

    public static List<WallpaperObject> getList(Realm mRealm) {
        return mRealm.where(WallpaperObject.class).findAll();
    }

    public static void saveList(Realm mRealm, final List<WallpaperObject> list) {
        mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmList<WallpaperObject> _newsList = new RealmList<>();
                    _newsList.addAll(list);
                    realm.insertOrUpdate(_newsList); // <-- insert unmanaged to Realm
                }
            });
        }
    }

