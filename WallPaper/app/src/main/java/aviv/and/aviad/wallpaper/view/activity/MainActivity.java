package aviv.and.aviad.wallpaper.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.List;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.model.AdapterGrid;
import aviv.and.aviad.wallpaper.model.AnimUtility;
import aviv.and.aviad.wallpaper.model.WallpaperObject;
import aviv.and.aviad.wallpaper.presenter.BaseView;
import aviv.and.aviad.wallpaper.presenter.MainActivityPresenter;
import aviv.and.aviad.wallpaper.presenter.Presenter;
import io.saeid.fabloading.LoadingView;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;


public class MainActivity extends BaseActivity implements BaseView, AdapterGrid.ItemClickListener {
    private RecyclerView recyclerView;
    private AdapterGrid adapter;
    private int numberOfColumns = 2;
    private RelativeLayout relativeLayout;
    private LoadingView mLoadingView;
    private int animCounter = 0;
    private boolean isListReady = false;
    private ProgressBar progressBar;
    private int width = 350;
    private int halfOfWidthScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        ((MainActivityPresenter) mPresenter).onRetrieveList();
    }


    private void initViews() {
        mLoadingView = findViewById(R.id.pb_loading);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initLoading();
            }
        }, 100);
        recyclerView = findViewById(R.id.wallpaper_grid);
        relativeLayout = findViewById(R.id.grid_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        progressBar = findViewById(R.id.pb_main);
        progressBar.setVisibility(View.INVISIBLE);

        //   recyclerView.setItemAnimator(new FadeInAnimator());


    }

    private void initLoading() {
        mLoadingView.addAnimation(Color.parseColor("#BC1F28"), R.drawable.marvel_5_lollipop, LoadingView.FROM_TOP);
        mLoadingView.addAnimation(Color.parseColor("#FFD200"), R.drawable.marvel_1_lollipop, LoadingView.FROM_BOTTOM);
        mLoadingView.addAnimation(Color.parseColor("#2F5DA9"), R.drawable.marvel_2_lollipop, LoadingView.FROM_LEFT);
        mLoadingView.addAnimation(Color.parseColor("#C7E7FB"), R.drawable.marvel_4_lollipop, LoadingView.FROM_RIGHT);
        mLoadingView.addAnimation(Color.parseColor("#BC1F28"), R.drawable.marvel_5_lollipop, LoadingView.FROM_TOP);

        mLoadingView.addListener(new LoadingView.LoadingListener() {
            @Override
            public void onAnimationStart(int currentItemPosition) {
            }

            @Override
            public void onAnimationRepeat(int nextItemPosition) {
                animCounter++;
                if (animCounter == 3) {
                    finishLoadingAndShowList();
                }
            }

            @Override
            public void onAnimationEnd(int nextItemPosition) {
            }
        });
        mLoadingView.startAnimation();
    }

    private void finishLoadingAndShowList() {
        AnimatorSet fadeOutAnim = new AnimUtility.AnimUtilsBuilder().animateViewToFadeOut(mLoadingView, 400, 400).build();
        fadeOutAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isListReady) {
                    recyclerView.setAdapter(adapter);
                    super.onAnimationEnd(animation);
                } else {
                    animCounter = 0;
                    mLoadingView.startAnimation();
                }
            }
        });
        new AnimUtility.AnimUtilsBuilder().syncBindSets(fadeOutAnim).start();
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(500);

        recyclerView.startAnimation(fadeIn);
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
        width = getHalfOfWidthScreen();
       /* LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_list);
        recyclerView.setLayoutAnimation(animation);*/
        adapter = new AdapterGrid(this, wallpaperObjectList, width);
        adapter.setClickListener(this);
        isListReady = true;
    }


    @Override
    public void onItemClick(View view, String imgUrl) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, WallpaperActivity.class);
        intent.putExtra("url", imgUrl);
        intent.putExtra("trans_name", ViewCompat.getTransitionName(view));
        intent.putExtra("width", width+"");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view,
                ViewCompat.getTransitionName(view));
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
       progressBar.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    public int getHalfOfWidthScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels / 2;    }
}

