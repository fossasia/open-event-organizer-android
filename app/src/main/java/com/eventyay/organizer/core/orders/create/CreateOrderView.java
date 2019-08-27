package com.eventyay.organizer.core.orders.create;

import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.common.mvp.view.Successful;

public interface CreateOrderView extends Progressive, Erroneous, Refreshable, Successful {}
