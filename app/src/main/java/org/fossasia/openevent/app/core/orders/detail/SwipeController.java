package org.fossasia.openevent.app.core.orders.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.fossasia.openevent.app.R;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private final OrderAttendeesAdapter orderAttendeesAdapter;
    private final OrderDetailViewModel orderDetailViewModel;
    private final Context context;

    public SwipeController(OrderDetailViewModel orderDetailViewModel, OrderAttendeesAdapter orderAttendeesAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.orderDetailViewModel = orderDetailViewModel;
        this.orderAttendeesAdapter = orderAttendeesAdapter;
        this.context = context;
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
            Paint paintGreen = new Paint();
            Paint paintRed = new Paint();

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            RectF doneIconDest = new RectF((float) itemView.getLeft() + width,
                (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width,
                (float) itemView.getBottom() - width);
            RectF closeIconDest = new RectF((float) itemView.getRight() - 2 * width,
                (float) itemView.getTop() + width, (float) itemView.getRight() - width,
                (float) itemView.getBottom() - width);

            paintGreen.setColor(context.getResources().getColor(R.color.light_green_500));
            paintRed.setColor(context.getResources().getColor(R.color.red_500));

            Bitmap doneIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.done);
            Bitmap closeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.close);

            if (dX > 0) {
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                    (float) itemView.getBottom());

                canvas.drawRect(background, paintGreen);

                canvas.drawBitmap(doneIcon, null, doneIconDest, paintGreen);
            } else {
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom());
                canvas.drawRect(background, paintRed);

                canvas.drawBitmap(closeIcon, null, closeIconDest, paintRed);
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
