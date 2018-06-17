package org.fossasia.openevent.app.core.presenter;

import org.fossasia.openevent.app.core.event.create.CreateEventPresenter;
import org.fossasia.openevent.app.core.event.create.CreateEventView;
import org.fossasia.openevent.app.data.Preferences;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.event.EventRepository;
import org.fossasia.openevent.app.utils.CurrencyUtils;
import org.fossasia.openevent.app.utils.DateUtils;
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
    private EventRepository eventRepository;
    @Mock
    private CreateEventView createEventView;
    @Mock
    private CurrencyUtils currencyUtils;
    @Mock
    private Preferences preferences;

    private CreateEventPresenter createEventPresenter;

    @Before
    public void setUp() {
        createEventPresenter = new CreateEventPresenter(eventRepository, currencyUtils, preferences);
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
        event.setStartsAt(isoDate);
        event.setEndsAt(isoDate);

        createEventPresenter.createEvent();

        verify(createEventView).showError("End time should be after start time");
        verify(eventRepository, never()).createEvent(any());
    }

    @Test
    public void shouldRejectWrongFormatDates() {
        createEventPresenter.createEvent();
        Event event = createEventPresenter.getEvent();

        event.setStartsAt("2011/12/03");
        event.setEndsAt("2011/03/03");
        createEventPresenter.createEvent();

        verify(createEventView).showError("Please enter date in correct format");
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
        event.setStartsAt(isoDateNow);
        event.setEndsAt(isoDateMax);

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
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.setStartsAt(isoDateNow);
        event.setEndsAt(isoDateMax);

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
        event.setStartsAt(isoDateNow);
        event.setEndsAt(isoDateMax);

        createEventPresenter.createEvent();

        verify(createEventView).showError("Error");
    }

    @Test
    public void shouldShowSuccessOnCreated() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.just(event));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.setStartsAt(isoDateNow);
        event.setEndsAt(isoDateMax);

        createEventPresenter.createEvent();

        verify(createEventView).onSuccess("Event Created Successfully");
    }

    @Test
    public void shouldCloseOnCreated() {
        Event event = createEventPresenter.getEvent();

        when(eventRepository.createEvent(event)).thenReturn(Observable.just(event));

        String isoDateNow = DateUtils.formatDateToIso(LocalDateTime.now());
        String isoDateMax = DateUtils.formatDateToIso(LocalDateTime.MAX);
        event.setStartsAt(isoDateNow);
        event.setEndsAt(isoDateMax);

        createEventPresenter.createEvent();

        verify(createEventView).close();
    }
}

