package org.fossasia.openevent.app.unit.presenter;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.repository.contract.IEventRepository;
import org.fossasia.openevent.app.common.utils.core.DateUtils;
import org.fossasia.openevent.app.module.event.create.CreateEventPresenter;
import org.fossasia.openevent.app.module.event.create.contract.ICreateEventView;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

import io.reactivex.Observable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateEventPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private IEventRepository eventRepository;
    @Mock
    private ICreateEventView createEventView;

    private CreateEventPresenter createEventPresenter;

    @Before
    public void setUp() {
        createEventPresenter = new CreateEventPresenter(eventRepository);
        createEventPresenter.attach(createEventView);
    }

    @Test
    public void shouldInitializeEvent() {
        Event event = createEventPresenter.getEvent();

        assertNotNull(event.getStartsAt());
        assertNotNull(event.getEndsAt());
    }

    @Test
    public void shouldRejectEndAfterStartDates() {
        Event event = createEventPresenter.getEvent();

        String isoDate = DateUtils.formatDateToIso(LocalDateTime.now());
        event.getStartsAt().set(isoDate);
        event.getEndsAt().set(isoDate);

        createEventPresenter.createEvent();

        verify(createEventView).showError(createEventPresenter.ERROR_DATE_END_AFTER_START);
        verify(eventRepository, never()).createEvent(any());
    }

    @Test
    public void shouldRejectWrongFormatDates() {
        createEventPresenter.createEvent();
        Event event = createEventPresenter.getEvent();

        event.getStartsAt().set("2011/12/03");
        event.getEndsAt().set("2011/03/03");
        createEventPresenter.createEvent();

        verify(createEventView).showError(createEventPresenter.ERROR_DATE_WRONG_FORMAT);
        verify(eventRepository, never()).createEvent(any());
    }

    @Test
    public void shouldNullifyEmptyFields() {
        Event event = createEventPresenter.getEvent();
        when(eventRepository.createEvent(event)).thenReturn(Observable.just(event));

        event.setLogoUrl("");
        event.setTicketUrl("");
        event.setOriginalImageUrl("");
        event.setExternalEventUrl("");
        event.setPaypalEmail("");

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.getStartsAt().set(isoDateNow);
        event.getEndsAt().set(isoDateMax);

        createEventPresenter.createEvent();
        assertNull(event.getLogoUrl());
        assertNull(event.getTicketUrl());
        assertNull(event.getOriginalImageUrl());
        assertNull(event.getExternalEventUrl());
        assertNull(event.getPaypalEmail());
    }

    @Test
    public void shouldAcceptCorrectEventDates() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.empty());

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.getStartsAt().set(isoDateNow);
        event.getEndsAt().set(isoDateThen);

        createEventPresenter.createEvent();

        verify(createEventView, never()).showError(anyString());
        verify(eventRepository).createEvent(event);
    }

    @Test
    public void shouldShowErrorOnFailure() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.error(new Throwable("Error")));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.getStartsAt().set(isoDateNow);
        event.getEndsAt().set(isoDateMax);

        createEventPresenter.createEvent();

        verify(createEventView).showError("Error");
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.just(event));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.getStartsAt().set(isoDateNow);
        event.getEndsAt().set(isoDateThen);

        createEventPresenter.createEvent();

        verify(createEventView).onSuccess(anyString());
    }

    @Test
    public void shouldCloseOnCreated() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.just(event));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateThen = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.getStartsAt().set(isoDateNow);
        event.getEndsAt().set(isoDateThen);

        createEventPresenter.createEvent();

        verify(createEventView).close();
    }
}

