package com.eventyay.organizer.core.speakerscall.detail;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;

public interface SpeakersCallView extends Progressive, Erroneous, Refreshable, ItemResult<SpeakersCall> {
}
