package aviv.and.aviad.wallpaper.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.model.AnimUtility;
import aviv.and.aviad.wallpaper.presenter.BaseView;
import aviv.and.aviad.wallpaper.presenter.Presenter;
import aviv.and.aviad.wallpaper.presenter.WallpaperPresenter;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by Aviad on 17/04/2018.
 */

public class WallpaperActivity extends BaseActivity implements BaseView, View.OnClickListener {

    private String imgUrl;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
            .READ_EXTERNAL_STORAGE, Manifest.permission.SET_WALLPAPER};
    private BroadcastReceiver mDLCompleteReceiver;
    private FabSpeedDial fabSpeedDial;
    private ImageView backBtn, largeImg;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        supportPostponeEnterTransition();
        Bundle extras = getIntent().getExtras();
        initViews();
        initSharedElements();

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
                    .resize(width, 0)
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
        }
    }

    private void initSharedElements() {
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
                                ((WallpaperPresenter) mPresenter).onDownloadPressed(getApplicationContext(), imgUrl);
                            }
                        } else {
                            ((WallpaperPresenter) mPresenter).onDownloadPressed(getApplicationContext(), imgUrl);
                        }
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
        AnimatorSet fadeInAnimFab = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(fabSpeedDial, 600).build();
        AnimatorSet fadeInAnimShadow = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(shadowLinearLayout, 600).build();
        fadeInAnimFab.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fabSpeedDial.setAlpha(1f);
                shadowLinearLayout.setAlpha(1f);
            }
        });
        new AnimUtility.AnimUtilsBuilder().bindSets(fadeInAnimFab, fadeInAnimShadow).start();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

}
