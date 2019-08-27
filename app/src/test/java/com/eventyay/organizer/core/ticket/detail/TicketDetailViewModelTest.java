package com.eventyay.organizer.core.ticket.detail;

import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.ticket.Ticket;
import com.eventyay.organizer.data.ticket.TicketRepositoryImpl;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TicketDetailViewModelTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private TicketRepositoryImpl ticketRepository;
    @Rule public TestRule rule = new InstantTaskExecutorRule();

    private TicketDetailViewModel ticketDetailViewModel;

    private static final long ID = 5L;

    private static final Ticket TICKET = new Ticket();

    static {
        TICKET.setId(ID);
        TICKET.setEvent(Event.builder().id(ID).build());
    }

    @Mock Observer<String> error;
    @Mock Observer<Ticket> ticket;

    @Before
    public void setUp() {
        ticketDetailViewModel = new TicketDetailViewModel(ticketRepository);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadTicketSuccessfully() {
        when(ticketRepository.getTicket(ID, false)).thenReturn(Observable.just(TICKET));
        InOrder inOrder = Mockito.inOrder(ticketRepository, ticket);

        ticketDetailViewModel.getTicket().observeForever(ticket);

        ticketDetailViewModel.loadTicket(ID);

        inOrder.verify(ticketRepository).getTicket(ID, false);
        inOrder.verify(ticket).onChanged(TICKET);
    }

    @Test
    public void shouldShowErrorOnTicketLoadFailure() {
        when(ticketRepository.getTicket(ID, false))
                .thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(ticketRepository, error);

        ticketDetailViewModel.getError().observeForever(error);

        ticketDetailViewModel.loadTicket(ID);

        inOrder.verify(ticketRepository).getTicket(ID, false);
        inOrder.verify(error).onChanged("Error");
    }
}
