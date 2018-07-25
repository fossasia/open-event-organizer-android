package org.fossasia.openevent.app.core.settings;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AcknowledgementDecider {

    public boolean shouldShowAcknowledgement() {
        return true;
    }

    public void openAcknowledgementsSection(Context context) {
        Intent intent = new Intent(context, OssLicensesMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
