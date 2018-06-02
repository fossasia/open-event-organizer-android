package org.fossasia.openevent.app.core.ticket.detail;

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
import android.support.annotation.RequiresApi;

import org.fossasia.openevent.app.data.ticket.Ticket;
import org.fossasia.openevent.app.utils.DateUtils;

import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    Context context;
    private int pageHeight;
    private int pageWidth;
    private PdfDocument myPdfDocument;
    private final int TOTAL_PAGES = 1;
    private Ticket ticket;

    public MyPrintDocumentAdapter(Context context, Ticket ticket) {
        this.context = context;
        this.ticket = ticket;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {
        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight =
            newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
            newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintDocumentInfo.Builder builder = new PrintDocumentInfo
            .Builder("print_output.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(TOTAL_PAGES);

        PrintDocumentInfo info = builder.build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {

        PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
            pageHeight, 1).create();

        PdfDocument.Page page =
            myPdfDocument.startPage(newPage);

        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            myPdfDocument.close();
            myPdfDocument = null;
            return;
        }

        drawPage(page);
        myPdfDocument.finishPage(page);

        try {
            myPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pages);
    }

    private void drawPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(
            "Ticket Details",
            leftMargin,
            titleBaseLine,
            paint);

        paint.setTextSize(14);
        canvas.drawText(
            "Ticket Name: " + ticket.getName()
            , leftMargin, titleBaseLine + 55, paint);
        canvas.drawText(
            "Ticket Type: " + ticket.getType()
            , leftMargin, titleBaseLine + 85, paint);
        canvas.drawText(
            "Ticket Sales Start At: " + DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, ticket.getSalesStartsAt())
            , leftMargin, titleBaseLine + 115, paint);
        canvas.drawText(
            "Ticket Min Order: " + ticket.getMinOrder()
            , leftMargin, titleBaseLine + 145, paint);
        canvas.drawText(
            "Ticket Max Order: " + ticket.getMaxOrder()
            , leftMargin, titleBaseLine + 175, paint);
        if (ticket.getPrice() != 0)
            canvas.drawText(
                "Ticket Price: " + ticket.getPrice()
                , leftMargin, titleBaseLine + 205, paint);
    }
}
