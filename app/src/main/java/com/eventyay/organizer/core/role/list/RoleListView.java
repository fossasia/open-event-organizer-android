package com.eventyay.organizer.core.role.list;

import com.eventyay.organizer.data.role.RoleInvite;

import java.util.List;

public interface RoleListView {

    void showError(String error);

    void showProgress(boolean show);

    void onRefreshComplete(boolean success);

    void showResults(List<RoleInvite> items);

    void showEmptyView(boolean show);
}
