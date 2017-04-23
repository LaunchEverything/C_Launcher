package com.example.carlauncher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.carlauncher.entity.CarItem;
import com.example.carlauncher.entity.CarView;
import com.example.carlauncher.helper.MyItemTouchCallback;

/**
 * Created by Administrator on 2016/4/12.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {

    private Context mContext;
    private int mLayoutId;
    private SparseArray<CarItem> mCarItems;

    public RecyclerAdapter(int layoutId, SparseArray<CarItem> carItems){
        this.mCarItems = carItems;
        this.mLayoutId = layoutId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mTitleView.setText(mCarItems.get(position).getTitle());
        holder.mIconView.setImageResource(mCarItems.get(position).getIconRes());
    }

    @Override
    public int getItemCount() {
        return mCarItems.size();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
//        if (fromPosition==results.size()-1 || toPosition==results.size()-1){
//            return;
//        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(results, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(results, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {
        mCarItems.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleView;
        public ImageView mIconView;

        public MyViewHolder(View itemView) {
            super(itemView);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
//            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
//            layoutParams.height = width / 4;
//            itemView.setLayoutParams(layoutParams);
            if (itemView instanceof CarView) {
                mTitleView = (TextView) ((CarView) itemView).getTitleView();
                mIconView = (ImageView) ((CarView) itemView).getIconView();
            }
        }
    }
}
