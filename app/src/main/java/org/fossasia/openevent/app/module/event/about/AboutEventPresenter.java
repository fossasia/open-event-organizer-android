package org.fossasia.openevent.app.module.event.about;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventPresenter;
import org.fossasia.openevent.app.module.event.about.contract.IAboutEventVew;

import javax.inject.Inject;

import io.reactivex.Observable;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.result;

public class AboutEventPresenter extends BaseDetailPresenter<Long, IAboutEventVew> implements IAboutEventPresenter {

    private final IEventRepository eventRepository;
    private Event event;

    @Inject
    public AboutEventPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void start() {
        getEventSource()
            .compose(dispose(getDisposable()))
            .compose(result(getView()))
            .subscribe(loadedEvent -> this.event = loadedEvent, Logger::logError);
    }

    private Observable<Event> getEventSource() {
        if (event != null && isRotated()) {
            return Observable.just(event);
        } else {
            return eventRepository.getEvent(getId(), false);
        }
    }
}
