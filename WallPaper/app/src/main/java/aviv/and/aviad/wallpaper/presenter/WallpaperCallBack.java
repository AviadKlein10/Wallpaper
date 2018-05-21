package aviv.and.aviad.wallpaper.presenter;

import android.content.Context;

/**
 * Created by Aviad on 22/04/2018.
 */

interface WallpaperCallBack {
    void onSharePressed();
    void onSetAsBackgroundPressed(String imgUrl);
    void onDownloadPressed(Context context, String imgUrl);
}
