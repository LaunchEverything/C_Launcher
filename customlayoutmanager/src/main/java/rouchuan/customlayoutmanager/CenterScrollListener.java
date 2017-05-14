package rouchuan.customlayoutmanager;

import android.support.v7.widget.RecyclerView;


/**
 * Created by Dajavu on 16/8/18.
 */
public class CenterScrollListener extends RecyclerView.OnScrollListener{
    private boolean mAutoSet = false;
    CustomLayoutManager customLayoutManager;
    public CenterScrollListener(CustomLayoutManager layoutManager){
        customLayoutManager = layoutManager;
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(!(layoutManager instanceof CustomLayoutManager)){
            mAutoSet = true;
            return;
        }
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            customLayoutManager.setScrollCallback(true);
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int dx = ((CustomLayoutManager)layoutManager).getOffsetCenterView();
            if (mAutoSet || dx == 0) {
                customLayoutManager.setScrollCallback(false);
            }
        }

        if(!mAutoSet){
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                final int dx;
                dx = ((CustomLayoutManager)layoutManager).getOffsetCenterView();
                ((CustomLayoutManager) layoutManager).resetTargetPosition();
                recyclerView.smoothScrollBy(dx,0);
            }
            mAutoSet = true;
        }
        if(newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING){
            mAutoSet = false;
        }
    }
}
