package org.fossasia.openevent.app.core.orders.list;

import org.fossasia.openevent.app.common.mvp.view.Emptiable;
import org.fossasia.openevent.app.common.mvp.view.Erroneous;
import org.fossasia.openevent.app.common.mvp.view.Progressive;
import org.fossasia.openevent.app.common.mvp.view.Refreshable;
import org.fossasia.openevent.app.data.order.Order;

public interface OrdersView extends Progressive, Erroneous, Refreshable, Emptiable<Order> {

    void showMessage(String message);
}
