package aviv.and.aviad.wallpaper.presenter;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.view.activity.WallpaperActivity;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * Created by Aviad on 17/04/2018.
 */

public class WallpaperPresenter extends Presenter<WallpaperActivity> implements WallpaperCallBack {


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onSharePressed() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "www");
        //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(gifUri));
        mView.startActivity(Intent.createChooser(shareIntent, "Share"));
    }


    @Override
    public void onSetAsBackgroundPressed(final String imgUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setBitmapAsWallpaper(getBitmapFromURL(imgUrl));
            }
        }).start();

    }

    @Override
    public void onDownloadPressed(Context context, String imgUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imgUrl));
        request.setDescription("Download wallpaper");
        request.setTitle(context.getResources().getString(R.string.app_name));
// in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String imageName = context.getResources().getString(R.string.app_name)+ "." +imgUrl.substring(imgUrl.lastIndexOf(".") + 1).toLowerCase();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imageName);

// get download service and enqueue file
        DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
        }
    }


    private Bitmap getBitmapFromURL(String src) {
        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) mView.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
*/
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
           /* myBitmap.setHeight(screenHeight);
            myBitmap.setWidth(screenWidth);*/
            return myBitmap;
        } catch (IOException e) {
            Log.e("errorwall",e.getMessage());
            return null;
        }
    }

    private void setBitmapAsWallpaper(Bitmap resource) {
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(resource);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}




