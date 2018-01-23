package org.fossasia.openevent.app.module.event.create;

import org.fossasia.openevent.app.common.app.lifecycle.presenter.BasePresenter;
import org.fossasia.openevent.app.common.app.rx.Logger;
import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventPresenter;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventView;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.dispose;
import static org.fossasia.openevent.app.common.app.rx.ViewTransformers.erroneous;


public class CreateEventPresenter extends BasePresenter<ICreateEventView> implements ICreateEventPresenter {

    private final IEventRepository eventRepository;
    private final Event event = new Event();

    @Inject
    public CreateEventPresenter(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        event.getStartsAt().set(isoDate);
        event.getEndsAt().set(isoDate);
        event.setDefaults();
        event.setId(null);
    }

    @Override
    public void start() {
        // Nothing to do
    }

    @Override
    public Event getEvent() {
        return event;
    }

    private boolean verify() {
        ZonedDateTime start = DateUtils.getDate(event.getStartsAt().get());
        ZonedDateTime end = DateUtils.getDate(event.getEndsAt().get());

        if (!end.isAfter(start)) {
            getView().showError("End time should be after start time");
            return false;
        }

        return true;
    }

    @Override
    public void createEvent() {
        if (!verify())
            return;

        eventRepository
            .createEvent(event)
            .compose(dispose(getDisposable()))
            .compose(erroneous(getView()))
            .doOnSubscribe(disposable -> event.creating.set(true))
            .doFinally(() -> event.creating.set(false))
            .subscribe(createdEvent -> {
                getView().onSuccess("Event Created");
                getView().dismiss();
            }, Logger::logError);
    }
}
