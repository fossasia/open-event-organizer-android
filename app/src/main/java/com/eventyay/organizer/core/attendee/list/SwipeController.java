package com.eventyay.organizer.core.attendee.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.attendee.Attendee;

import java.util.List;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private final AttendeesViewModel attendeesViewModel;
    private List<Attendee> attendeeList;
    private final Context context;
    private final Paint paintGreen = new Paint();
    private final Paint paintRed = new Paint();
    private final Bitmap closeIcon;
    private final Bitmap doneIcon;
    private final boolean playSound;

    public SwipeController(AttendeesViewModel attendeesViewModel, List<Attendee> attendeeList, Context context) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.attendeesViewModel = attendeesViewModel;
        this.attendeeList = attendeeList;
        this.context = context;

        closeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.close);
        doneIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.done);

        paintGreen.setColor(context.getResources().getColor(R.color.light_green_500));
        paintRed.setColor(context.getResources().getColor(R.color.red_500));

        playSound = attendeesViewModel.getPreferences().getBoolean(Constants.PREF_PLAY_SOUNDS, false);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        makeMovementFlags(dragFlags, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        if (attendeeList.get(viewHolder.getAdapterPosition()).isCheckedIn) {
            return makeMovementFlags(dragFlags, ItemTouchHelper.LEFT);
        } else {
            return makeMovementFlags(dragFlags, ItemTouchHelper.RIGHT);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int swipedPosition = viewHolder.getAdapterPosition();
        attendeesViewModel.toggleCheckInState(attendeeList, swipedPosition);

        if (playSound) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.check_in_sound);
            mediaPlayer.start();
        }
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            RectF background;
            Paint paint;
            Bitmap icon;
            RectF iconDest;

            if (!attendeeList.get(viewHolder.getAdapterPosition()).isCheckedIn) {
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
                    paint = paintGreen;
                    icon = doneIcon;
                    iconDest = new RectF((float) itemView.getRight() - 2 * width,
                        (float) itemView.getTop() + width, (float) itemView.getRight() - width,
                        (float) itemView.getBottom() - width);
                }
            } else {
                if (dX > 0) {
                    background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom());
                    paint = paintRed;
                    icon = closeIcon;
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
            }

            canvas.drawRect(background, paint);
            canvas.drawBitmap(icon, null, iconDest, paint);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
