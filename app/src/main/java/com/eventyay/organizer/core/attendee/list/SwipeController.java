package com.eventyay.organizer.core.attendee.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.eventyay.organizer.R;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.utils.Utils;
import com.mikepenz.fastadapter.FastAdapter;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private final AttendeesPresenter attendeesPresenter;
    private final Paint paintGreen = new Paint();
    private final Paint paintRed = new Paint();
    private final Bitmap checkinIcon;
    private final Bitmap checkoutIcon;

    public SwipeController(AttendeesPresenter attendeesPresenter, Context context) {
        super(0, ItemTouchHelper.RIGHT);
        this.attendeesPresenter = attendeesPresenter;

        checkinIcon = Utils.drawableToBitmap(context.getResources().getDrawable(R.drawable.ic_checkin));
        checkoutIcon = Utils.drawableToBitmap(context.getResources().getDrawable(R.drawable.ic_checkout));

        paintGreen.setColor(context.getResources().getColor(R.color.light_green_500));
        paintRed.setColor(context.getResources().getColor(R.color.red_500));
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int swipedPosition = viewHolder.getAdapterPosition();
        attendeesPresenter.toggleCheckInState(swipedPosition);
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        float newDx = dX;
        if (newDx >= 200f) {
            newDx = 200f;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            RectF background;
            Paint paint;
            Bitmap icon;
            RectF iconDest;

            if (!attendeesPresenter.getAttendees().get(viewHolder.getAdapterPosition()).isCheckedIn) {
                background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), newDx,
                    (float) itemView.getBottom());
                paint = paintGreen;
                icon = checkinIcon;
                iconDest = new RectF((float) itemView.getLeft() + width,
                    (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width,
                    (float) itemView.getBottom() - width);
            } else {
                background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), newDx,
                    (float) itemView.getBottom());
                paint = paintRed;
                icon = checkoutIcon;
                iconDest = new RectF((float) itemView.getLeft() + width,
                    (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width,
                    (float) itemView.getBottom() - width);
            }

            canvas.drawRect(background, paint);
            canvas.drawBitmap(icon, null, iconDest, paint);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);
    }
}
