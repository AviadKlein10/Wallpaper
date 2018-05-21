package aviv.and.aviad.wallpaper.model;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import aviv.and.aviad.wallpaper.R;


/**
 * Created by Aviad on 25/09/2017.
 */

public class AdapterGrid extends RecyclerView.Adapter<AdapterGrid.ViewHolder> {

    private List<WallpaperObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int desiredImageWidth;
    private int lastPosition = -1;
    // private ItemLongClickListener mLongClickListener;
    // private List<Integer> selectedIds = new ArrayList<>();


    public AdapterGrid(Context context, List<WallpaperObject> data, int desiredImageWidth) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.desiredImageWidth = desiredImageWidth;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallpaper, parent, false);

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


        final String imgUrl = mData.get(position).getUrl();
        /*holder.imgGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet viewToCenterAnim = new AnimUtility.AnimUtilsBuilder().moveViewToScreensCenter(recyclerView, GENERAL_DURATION, this, 0).build();
                AnimatorSet viewScaleUp = new AnimUtility.AnimUtilsBuilder().scaleExactViewAnimation(600, v, ).build();
                new AnimUtility.AnimUtilsBuilder().syncBindSets(viewToCenterAnim, viewScaleUp).start();
            }
        });*/
        holder.progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar = holder.progressBar;
        Picasso.get().load(imgUrl).resize(desiredImageWidth, 0).centerCrop().into(holder.imgGif, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        //holder.imgGif.setImageResource(R.drawable.placeholder);
        //Glide.with(mInflater.getContext()).load(mData.get(position).getUrl()).into(holder.imgGif);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            holder.imgGif.setTransitionName("img_trans");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(holder.imgGif,imgUrl);
            }

        });

       startItemAnimation(holder, position);
    }

    private void startItemAnimation(ViewHolder holder, int position) {
        if(position>lastPosition){
            Log.d("position", position+"");
            AnimatorSet fadeInAnim = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(holder.imgGif, 350*position).build();
            new AnimUtility.AnimUtilsBuilder().syncBindSets(fadeInAnim).start();
           /* Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(),
                    R.anim.item_animation_list);
            holder.itemView.startAnimation(animation);*/

            lastPosition = position;
        }
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
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            // txtTag = (TextView) itemView.findViewById(R.id.gif_tag);
            imgGif = itemView.findViewById(R.id.img_wallpaper);
            progressBar = itemView.findViewById(R.id.pb_item);
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
        void onItemClick(View view, String imgUrl);
    }


}