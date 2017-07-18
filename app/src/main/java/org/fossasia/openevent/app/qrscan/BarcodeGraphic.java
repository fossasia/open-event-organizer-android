package org.fossasia.openevent.app.qrscan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.barcode.Barcode;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.qrscan.widget.GraphicOverlay;

public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private int id;

    private Paint rectPaint;
    private volatile Barcode barcode;

    private Bitmap frameTopLeft;
    private Bitmap frameTopRight;
    private Bitmap frameBottomRight;
    private Bitmap frameBottomLeft;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);

        final int selectedColor = Color.CYAN;

        rectPaint = new Paint();
        rectPaint.setColor(selectedColor);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        frameTopLeft = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.frame_top_left);
        frameTopRight = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.frame_top_right);
        frameBottomLeft = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.frame_bottom_left);
        frameBottomRight = BitmapFactory.decodeResource(overlay.getResources(), R.drawable.frame_bottom_right);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateItem(Barcode barcode) {
        this.barcode = barcode;
        postInvalidate();
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (barcode == null) {
            return;
        }

        // Draws the bounding box around the barcode.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);

        int width = (int) ((rect.right - rect.left)/3);
        int height = (int) ((rect.top - rect.bottom)/3);

        canvas.drawBitmap(Bitmap.createScaledBitmap(frameBottomLeft, width, height, false), rect.left, rect.top, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(frameBottomRight, width, height, false), rect.right - width, rect.top, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(frameTopLeft, width, height, false), rect.left, rect.bottom + height, null);
        canvas.drawBitmap(Bitmap.createScaledBitmap(frameTopRight, width, height, false), rect.right - width, rect.bottom + height, null);

        canvas.drawRect(rect, rectPaint);

    }
}
