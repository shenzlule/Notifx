package com.tonni.notifx.Utils;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tonni.notifx.adapter.ForexCurrencyAdapter;
import com.tonni.notifx.frags.ForexFragment;
import com.tonni.notifx.models.ForexCurrency;

public class SwipeToRevealCallback extends ItemTouchHelper.SimpleCallback {

    private ForexCurrencyAdapter adapter;
    private RecyclerView.ViewHolder currentSwipedViewHolder = null;
    private ForexFragment forexFragment;

    public SwipeToRevealCallback(ForexCurrencyAdapter adapter, ForexFragment forexFragment) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.forexFragment = forexFragment;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Reset the current swiped view holder
        if (currentSwipedViewHolder != null) {
            View itemView = currentSwipedViewHolder.itemView;
//            ImageView plusIcon = itemView.findViewById(R.id.plus_icon);
//            plusIcon.setVisibility(View.INVISIBLE);
        }

        currentSwipedViewHolder = viewHolder;

        switch (direction){
            case ItemTouchHelper.LEFT:
                forexFragment.showInputDialog(currentSwipedViewHolder.getAdapterPosition(),null);

                break;
            case ItemTouchHelper.RIGHT:
                break;
        }



    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (currentSwipedViewHolder != null && currentSwipedViewHolder != viewHolder) {
                onChildDraw(c, recyclerView, currentSwipedViewHolder, 0, 0, ItemTouchHelper.ACTION_STATE_IDLE, false);
            }
            currentSwipedViewHolder = viewHolder;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX / 7, dY, actionState, isCurrentlyActive);
    }

    public void resetSwipedView() {
        if (currentSwipedViewHolder != null) {
            int position = currentSwipedViewHolder.getAdapterPosition();
            adapter.notifyItemChanged(position);
            currentSwipedViewHolder = null;
        }
    }
}
