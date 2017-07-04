package org.fossasia.openevent.app.main.listeners;


import org.fossasia.openevent.app.data.models.Event;

public interface OnEventLoadedListener {

    void onEventLoaded(Event event, boolean loadFragment);

}
