package aviv.and.aviad.wallpaper.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.model.AnimUtility;
import aviv.and.aviad.wallpaper.model.DownloadService;
import aviv.and.aviad.wallpaper.presenter.BaseView;
import aviv.and.aviad.wallpaper.presenter.Presenter;
import aviv.and.aviad.wallpaper.presenter.WallpaperPresenter;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by Aviad on 17/04/2018.
 */

public class WallpaperActivity extends BaseActivity implements BaseView,View.OnClickListener {

    private String imgUrl;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
            .READ_EXTERNAL_STORAGE, Manifest.permission.SET_WALLPAPER};
    private BroadcastReceiver mDLCompleteReceiver;
    private FabSpeedDial fabSpeedDial;
    private ImageView backBtn,largeImg;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        supportPostponeEnterTransition();
        Bundle extras = getIntent().getExtras();
        initViews();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.transition.Transition transition = getWindow().getSharedElementEnterTransition();
            transition.addListener(new android.transition.Transition.TransitionListener() {
                @Override
                public void onTransitionStart(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionEnd(android.transition.Transition transition) {
                    initFabView();
                }

                @Override
                public void onTransitionCancel(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionPause(android.transition.Transition transition) {

                }

                @Override
                public void onTransitionResume(android.transition.Transition transition) {

                }
            });
        }

        if (extras != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String imageTransitionName = extras.getString("trans_name");
                largeImg.setTransitionName(imageTransitionName);
            }

            imgUrl = extras.getString("url");
            supportPostponeEnterTransition();
            int width = Integer.parseInt(extras.getString("width"));

            Picasso.get()
                    .load(imgUrl)
                    .noFade()
                    .centerCrop()
                    .resize(width,0)
                    .into(largeImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {
                            supportStartPostponedEnterTransition();

                        }

                    });

            initProgressView();
            /*Glide.with(this)
                    .load(imgUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(largeImg);*/
        }
    }

    private void initProgressView() {


// instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(WallpaperActivity.this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

    }

    private void initViews() {
        largeImg = findViewById(R.id.image_wallpaper);
        fabSpeedDial = findViewById(R.id.fab_menu);
        fabSpeedDial.setAlpha(0f);
        backBtn = findViewById(R.id.action_bar_back_wall);
        backBtn.setOnClickListener(this);
        initFabMenu();
    }

    private void initFabMenu() {
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }

            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_share:
                        ((WallpaperPresenter) mPresenter).onSharePressed();
                        break;
                    case R.id.action_download:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (onPermissionCheck()) {
                                /*((WallpaperPresenter) mPresenter).onDownloadPressed(imgUrl);*/
                               // downloadImage();
                                downloadImageUsingService();
                            }
                        } else {
                           // downloadImage();
                            downloadImageUsingService();
                        }
                        /*String imageUrl = "http://www.avajava.com/images/avajavalogo.jpg";
                        String destinationFile = "image.jpg";

                        try {
                            SaveImageFromUrl.saveImage(imageUrl,destinationFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        break;
                    case R.id.action_wallpaper:
                        ((WallpaperPresenter) mPresenter).onSetAsBackgroundPressed(imgUrl);

                        break;
                }
                //TODO: Start some activity
                return false;
            }
        });
    }

    private void initFabView() {
        final LinearLayout shadowLinearLayout = findViewById(R.id.shadow_layout_wallpaper);
        AnimatorSet fadeInAnimFab = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(fabSpeedDial,600).build();
        AnimatorSet fadeInAnimShadow = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(shadowLinearLayout,600).build();
        fadeInAnimFab.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fabSpeedDial.setAlpha(1f);
                shadowLinearLayout.setAlpha(1f);
            }
        });
        new AnimUtility.AnimUtilsBuilder().bindSets(fadeInAnimFab,fadeInAnimShadow).start();
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


    @Override
    protected Presenter getPresenter() {
        return new WallpaperPresenter();
    }

    @Override
    protected void bind() {
        mPresenter.bind(this);
    }

    @Override
    protected void unbind() {

    }

    @Override
    public void onError(String message) {

    }

    public boolean onPermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (arePermissionsEnabled()) {
                return true;
            } else {
                requestMultiplePermissions();
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled() {
        for (String permission : permissions) {
            if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void downloadImage() {
        findViewById(R.id.layout_download).setVisibility(View.VISIBLE);
        /*findViewById(R.id.pbImageLoading).setVisibility(View.GONE);
        findViewById(R.id.tvPercent).setVisibility(View.GONE);*/
        final TextView tvStatus = findViewById(R.id.tvDMWorking);
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setDuration(700L);
        tvStatus.startAnimation(anim);

        DownloadManager.Request request;

        try {
            request = new DownloadManager.Request(Uri.parse(imgUrl));
        } catch (IllegalArgumentException e) {
            tvStatus.setText("Error: " + e.getMessage());
            tvStatus.clearAnimation();
            return;
        }
                /* allow mobile and WiFi downloads */
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("DM Example");
        request.setDescription("Downloading file");

                /* we let the user see the download in a notification */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                /* Try to determine the file extension from the url. Only allow image types. You
                 * can skip this check if you only plan to handle the downloaded file manually and
                 * don't care about file managers not recognizing the file as a known type */
        String[] allowedTypes = {"png", "jpg", "jpeg", "gif", "webp"};
        String suffix = imgUrl.substring(imgUrl.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(allowedTypes).contains(suffix)) {
            tvStatus.clearAnimation();
            tvStatus.setText("Invalid file extension. Allowed types: \n");
            for (String s : allowedTypes) {
                tvStatus.append("\n" + "." + s);
            }
            return;
        }

                /* set the destination path for this download */
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS +
                File.separator + getResources().getString(R.string.app_name), "." + suffix);
                /* allow the MediaScanner to scan the downloaded file */
        request.allowScanningByMediaScanner();
        final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                /* this is our unique download id */
        final long DL_ID = dm.enqueue(request);

                /* get notified when the download is complete */
        mDLCompleteReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                        /* our download */
                if (DL_ID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)) {

                    tvStatus.clearAnimation();
                            /* get the path of the downloaded file */
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(DL_ID);
                    Cursor cursor = dm.query(query);
                    if (!cursor.moveToFirst()) {
                        tvStatus.setText("Download error: cursor is empty");
                        return;
                    }

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            != DownloadManager.STATUS_SUCCESSFUL) {
                        tvStatus.setText("Download failed: no success status");
                        return;
                    }

                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                     tvStatus.setText("File download complete. Location: \n" + path);
                   // tvStatus.setText("File download complete");
                    Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    anim2.setDuration(700L);
                    tvStatus.startAnimation(anim2);
                    findViewById(R.id.layout_download).setVisibility(View.INVISIBLE);

                }
            }
        };
                /* register receiver to listen for ACTION_DOWNLOAD_COMPLETE action */
        registerReceiver(mDLCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_bar_back_wall:
                super.onBackPressed();
                fabSpeedDial.setAlpha(0f);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        fabSpeedDial.setAlpha(0f);
        super.onBackPressed();
    }

    private void downloadImageUsingService(){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imgUrl));
        request.setDescription("Download wallpaper");
        request.setTitle(getResources().getString(R.string.app_name));
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

       /* mProgressDialog.show();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("url", imgUrl);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        startService(intent);*/
    }
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                mProgressDialog.setProgress(progress);
                if (progress == 100) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }
}
