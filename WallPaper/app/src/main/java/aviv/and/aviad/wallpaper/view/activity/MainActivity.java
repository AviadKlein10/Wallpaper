package aviv.and.aviad.wallpaper.view.activity;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v4.util.Pair;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.model.AdapterGrid;
import aviv.and.aviad.wallpaper.model.AnimUtility;
import aviv.and.aviad.wallpaper.model.WallpaperObject;
import aviv.and.aviad.wallpaper.presenter.BaseView;
import aviv.and.aviad.wallpaper.presenter.MainActivityPresenter;
import aviv.and.aviad.wallpaper.presenter.Presenter;


public class MainActivity extends BaseActivity implements BaseView,  AdapterGrid.ItemClickListener {
    private RecyclerView recyclerView;
    private AdapterGrid adapter;
    private int numberOfColumns = 3;
    private static final int GENERAL_DURATION = 600;
    private RelativeLayout relativeLayout;
    private final String EXTRA_IMG_URL = "EXTRA_IMG_URL";
    private String EXTRA_IMAGE_TRANSITION_NAME = "EXTRA_IMAGE_TRANSITION_NAME";
    private ImageView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        recyclerView =findViewById(R.id.wallpaper_grid);
        relativeLayout =findViewById(R.id.grid_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        ((MainActivityPresenter) mPresenter).onRetrieveList();

    }






    @Override
    protected Presenter getPresenter() {
        return new MainActivityPresenter();
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

    public void onListReady(List<WallpaperObject> wallpaperObjectList) {
        findViewById(R.id.pb_main).setVisibility(View.GONE);
        adapter = new AdapterGrid(this, wallpaperObjectList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onItemClick(View view, int position, WallpaperObject imageUrl, ImageView sharedImageView) {
        AnimatorSet viewToCenterAnim = new AnimUtility.AnimUtilsBuilder().moveViewToScreensCenter(view, GENERAL_DURATION, this, 0).build();
        AnimatorSet viewScaleUp = new AnimUtility.AnimUtilsBuilder().scaleExactViewAnimation(GENERAL_DURATION,sharedImageView,recyclerView).build();
        new AnimUtility.AnimUtilsBuilder().syncBindSets(viewScaleUp).start();
        /*Intent intent = new Intent(this, WallpaperActivity.class);
        intent.putExtra(EXTRA_IMG_URL, imageUrl.getUrl());
        intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedImageView,
                ViewCompat.getTransitionName(sharedImageView));

        startActivity(intent, options.toBundle());*/
    }

    @Override
    public void onItemClick(View view, int i, WallpaperObject item) {
       /* AnimatorSet viewScaleUp = new AnimUtility.AnimUtilsBuilder().scaleExactViewAnimation(GENERAL_DURATION,view,relativeLayout).build();
        new AnimUtility.AnimUtilsBuilder().syncBindSets(viewScaleUp).start();*/

        Intent intent = new Intent(this, WallpaperActivity.class);
        intent.putExtra("url", item.getUrl());
        intent.putExtra("trans_name", ViewCompat.getTransitionName(view));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view,
                ViewCompat.getTransitionName(view));
        startActivity(intent, options.toBundle());
    }
}

