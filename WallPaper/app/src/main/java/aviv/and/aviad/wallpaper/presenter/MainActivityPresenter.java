package aviv.and.aviad.wallpaper.presenter;

import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import aviv.and.aviad.wallpaper.RealmHelper;
import aviv.and.aviad.wallpaper.model.WallpaperObject;
import aviv.and.aviad.wallpaper.view.activity.MainActivity;
import aviv.and.aviad.wallpaper.view.activity.WallpaperActivity;

/**
 * Created by Aviad on 16/04/2018.
 */

public class MainActivityPresenter extends Presenter<MainActivity> implements MainActivityCallBack {
    private List<WallpaperObject> wallpaperObjects;
    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRetrieveList() {
        if(RealmHelper.getList(mRealm).size()==0) {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    int counter = 0;
                    wallpaperObjects = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        wallpaperObjects.add(new WallpaperObject(counter, child.getValue() + ""));
                        counter++;
                    }
                    RealmHelper.saveList(mRealm, wallpaperObjects);
                    mView.onListReady(wallpaperObjects);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            mView.onListReady(RealmHelper.getList(mRealm));
        }
    }

    @Override
    public void onImageClicked(String imageUrl) {
        Intent intent = new Intent(mView.getApplicationContext(),WallpaperActivity.class);
        intent.putExtra("url", imageUrl);
        mView.startActivity(intent);
        mView.overridePendingTransition(0, 0); // 0 for no animation

    }


}
