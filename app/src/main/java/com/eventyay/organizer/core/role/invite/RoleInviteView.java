package com.eventyay.organizer.core.role.invite;

public interface RoleInviteView {

    void showError(String error);

    void onSuccess(String message);

    void showProgress(boolean show);

    int getTitle();
}
