package org.fossasia.openevent.app.common.app.lifecycle.contract.view;

import java.util.List;

public interface Emptiable<T> {

    void showResults(List<T> items);

    void showEmptyView(boolean show);

}
