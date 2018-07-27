package com.eventyay.organizer.core.orders.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.orders.detail.adapter.OrderAttendeesAdapter;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private final OrderAttendeesAdapter orderAttendeesAdapter;
    private final OrderDetailViewModel orderDetailViewModel;
    private final Paint paintGreen = new Paint();
    private final Paint paintRed = new Paint();
    private final Bitmap closeIcon;
    private final Bitmap doneIcon;

    public SwipeController(OrderDetailViewModel orderDetailViewModel, OrderAttendeesAdapter orderAttendeesAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.orderDetailViewModel = orderDetailViewModel;
        this.orderAttendeesAdapter = orderAttendeesAdapter;

        closeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.close);
        doneIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.done);

        paintGreen.setColor(context.getResources().getColor(R.color.light_green_500));
        paintRed.setColor(context.getResources().getColor(R.color.red_500));
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;

        if (orderDetailViewModel.getCheckedInStatus(viewHolder.getAdapterPosition()) == null)
            makeMovementFlags(dragFlags, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        if (orderDetailViewModel.getCheckedInStatus(viewHolder.getAdapterPosition())) {
            return makeMovementFlags(dragFlags, ItemTouchHelper.LEFT);
        } else {
            return makeMovementFlags(dragFlags, ItemTouchHelper.RIGHT);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        orderDetailViewModel.toggleCheckIn(position);
        orderAttendeesAdapter.notifyItemChanged(position);
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            RectF background;
            Paint paint;
            Bitmap icon;
            RectF iconDest;

            if (dX > 0) {
                background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                    (float) itemView.getBottom());
                paint = paintGreen;
                icon = doneIcon;
                iconDest = new RectF((float) itemView.getLeft() + width,
                    (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width,
                    (float) itemView.getBottom() - width);
            } else {
                background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom());
                paint = paintRed;
                icon = closeIcon;
                iconDest = new RectF((float) itemView.getRight() - 2 * width,
                    (float) itemView.getTop() + width, (float) itemView.getRight() - width,
                    (float) itemView.getBottom() - width);
            }

            canvas.drawRect(background, paint);
            canvas.drawBitmap(icon, null, iconDest, paint);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
