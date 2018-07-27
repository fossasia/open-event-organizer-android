package com.eventyay.organizer.core.orders.list;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.data.order.Order;

public interface OrdersView extends Progressive, Erroneous, Refreshable, Emptiable<Order> {

    void showMessage(String message);
}
