package aviv.and.aviad.wallpaper.model;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import aviv.and.aviad.wallpaper.R;


/**
 * Created by Aviad on 25/09/2017.
 */

public class AdapterGrid extends RecyclerView.Adapter<AdapterGrid.ViewHolder> {

    private List<WallpaperObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
   // private ItemLongClickListener mLongClickListener;
   // private List<Integer> selectedIds = new ArrayList<>();


    public AdapterGrid(Context context, List<WallpaperObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(view,2, getItem(2));
            }
        });*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        String tag = mData.get(position).getId()+"";
        final WallpaperObject wallpaperObject = mData.get(position);
        /*holder.imgGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet viewToCenterAnim = new AnimUtility.AnimUtilsBuilder().moveViewToScreensCenter(recyclerView, GENERAL_DURATION, this, 0).build();
                AnimatorSet viewScaleUp = new AnimUtility.AnimUtilsBuilder().scaleExactViewAnimation(600, v, ).build();
                new AnimUtility.AnimUtilsBuilder().syncBindSets(viewToCenterAnim, viewScaleUp).start();
            }
        });*/
        Glide.with(mInflater.getContext()).load(mData.get(position).getUrl()).into(holder.imgGif);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            holder.imgGif.setTransitionName("img_trans");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mClickListener.onItemClick(holder.imgGif, position,wallpaperObject);
            }

        });
       /* holder.imgGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,holder.getAdapterPosition(), wallpaperObject, holder.imgGif);
            }
        });*/

        startItemAnimation(holder,position);
    }

    private void startItemAnimation(ViewHolder holder, int position) {
        AnimatorSet fadeInAnim = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(holder.imgGif,position*600).build();
        AnimatorSet scaleAnim = new AnimUtility.AnimUtilsBuilder().scaleViewAnimation(holder.imgGif,position*650,holder.imgGif.getScaleX(),holder.imgGif.getScaleY()).build();
        new AnimUtility.AnimUtilsBuilder().syncBindSets(fadeInAnim,scaleAnim).start();
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setdata(List<WallpaperObject> list) {
        mData = list;
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
       // TextView txtTag;
        SquareImageView imgGif;
        FrameLayout rootView;

        ViewHolder(View itemView) {
            super(itemView);
           // txtTag = (TextView) itemView.findViewById(R.id.gif_tag);
            imgGif = itemView.findViewById(R.id.img_wallpaper);

            // Apply the new width for ImageView programmatically

            // Set the scale type for ImageView image scaling
        }




    }

    // convenience method for getting data at click position
    public WallpaperObject getItem(int position) {
        if (mData == null || mData.get(position) == null) {
            return null;
        }
        return mData.get(position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View v, int view, WallpaperObject imageUrl, ImageView imageView);

        void onItemClick(View view, int i, WallpaperObject item);
    }


}