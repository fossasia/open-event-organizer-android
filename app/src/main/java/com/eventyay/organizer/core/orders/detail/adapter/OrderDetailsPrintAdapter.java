package com.eventyay.organizer.core.orders.detail.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import androidx.annotation.RequiresApi;
import com.eventyay.organizer.data.order.Order;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class OrderDetailsPrintAdapter extends PrintDocumentAdapter {

    private static final int TOTAL_PAGES = 1;
    private final Context context;
    private final Order order;
    private int pageHeight;
    private int pageWidth;
    private PdfDocument orderDocument;
    private final Paint paintHeaderText = new Paint();
    private final Paint paintOrderDescription = new Paint();

    public OrderDetailsPrintAdapter(Context context, Order order) {
        this.context = context;
        this.order = order;
    }

    @Override
    public void onLayout(
            PrintAttributes oldAttributes,
            PrintAttributes newAttributes,
            CancellationSignal cancellationSignal,
            LayoutResultCallback callback,
            Bundle extras) {
        orderDocument = new PrintedPdfDocument(context, newAttributes);
        pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder =
                new PrintDocumentInfo.Builder(
                                "Order_" + order.getIdentifier() + "_" + order.getId() + ".pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(TOTAL_PAGES);

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(
            PageRange[] pages,
            ParcelFileDescriptor destination,
            CancellationSignal cancellationSignal,
            WriteResultCallback callback) {

        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            orderDocument.close();
            return;
        }

        PdfDocument.PageInfo newPage =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, TOTAL_PAGES).create();
        PdfDocument.Page page = orderDocument.startPage(newPage);

        drawPage(page);
        orderDocument.finishPage(page);

        try {
            orderDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            orderDocument.close();
        }

        callback.onWriteFinished(pages);
    }

    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();
        int titleBaseLine = 72;
        int leftMargin = 54;

        paintHeaderText.setColor(Color.BLACK);
        paintOrderDescription.setTextSize(40);

        canvas.drawText("Order Details", leftMargin, titleBaseLine, paintOrderDescription);

        paintOrderDescription.setTextSize(14);
        canvas.drawText(
                "Order Identitifer: " + order.getIdentifier(),
                leftMargin,
                titleBaseLine + 55,
                paintOrderDescription);
        canvas.drawText(
                "Order Status: " + order.getStatus(),
                leftMargin,
                titleBaseLine + 85,
                paintOrderDescription);
        canvas.drawText(
                "Order Amount: " + order.getAmount(),
                leftMargin,
                titleBaseLine + 115,
                paintOrderDescription);
        canvas.drawText(
                "Payment Mode: " + order.getPaymentMode(),
                leftMargin,
                titleBaseLine + 145,
                paintOrderDescription);
    }
}
