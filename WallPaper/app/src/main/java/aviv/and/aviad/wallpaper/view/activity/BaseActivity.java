package aviv.and.aviad.wallpaper.view.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import aviv.and.aviad.wallpaper.R;
import aviv.and.aviad.wallpaper.presenter.Presenter;


/**
 * Created by Ofer Dan-On on 1/20/2017.
 */

public abstract class BaseActivity<P extends Presenter> extends AppCompatActivity {
    public P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        mPresenter = getPresenter();
        bind();

    }

    @Override
    protected void onResume() {
        super.onResume();
       // App.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // App.activityPaused();
    }





    @Override
    protected void onStart() {
        super.onStart();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called BEFORE the view is created and binds the presenter
     * to lifecycle events (onResume and onPause)
     *
     * @return The Presenter instance connected to this fragment
     */
    protected abstract P getPresenter();

    protected abstract void bind();

    protected abstract void unbind();
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color

            window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        }
    }
}
