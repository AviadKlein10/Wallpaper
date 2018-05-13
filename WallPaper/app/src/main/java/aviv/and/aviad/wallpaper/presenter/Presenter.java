package aviv.and.aviad.wallpaper.presenter;


import io.realm.Realm;

/**
 * Created by Aviv on 01-Mar-17.
 */

public abstract class Presenter<V extends BaseView> {
    public final String TAG = getClass().getSimpleName();
    protected V mView;
    protected Realm mRealm;

    public void bind(V view) {
        this.mView = view;
        this.mRealm = Realm.getDefaultInstance();
    }

    public void unbind() {
        this.mView = null;
        mRealm.close();
    }

    public abstract void onResume();

    public abstract void onPause();




    public interface PresenterCallback extends BaseView {
       void onOnGifReady();
    }


}