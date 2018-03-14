package org.fossasia.openticket.app.unit.presenter;

import org.fossasia.openevent.app.common.data.models.Event;
import org.fossasia.openevent.app.common.data.models.Ticket;
import org.fossasia.openevent.app.common.data.repository.TicketRepository;
import org.fossasia.openevent.app.module.ticket.detail.TicketDetailPresenter;
import org.fossasia.openevent.app.module.ticket.detail.contract.ITicketDetailView;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ITicketDetailView ticketDetailView;
    @Mock
    private TicketRepository ticketRepository;
    private TicketDetailPresenter ticketDetailPresenter;

    private static final long ID = 5L;

    private static final Ticket TICKET = new Ticket();

    static {
        TICKET.setId(ID);
        TICKET.setEvent(Event.builder().id(ID).build());
    }

    @Before
    public void setUp() {
        ticketDetailPresenter = new TicketDetailPresenter(ticketRepository);
        ticketDetailPresenter.attach(ID, ticketDetailView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadTicketSuccessfully() {
        when(ticketRepository.getTicket(ID, false)).thenReturn(Observable.just(TICKET));

        ticketDetailPresenter.start();

        verify(ticketDetailView).showResult(TICKET);
    }

    @Test
    public void shouldShowErrorOnTicketLoadFailure() {
        when(ticketRepository.getTicket(ID, false)).thenReturn(Observable.error(new Throwable("Error")));

        ticketDetailPresenter.start();

        verify(ticketDetailView).showError("Error");
    }
}
