package com.eventyay.organizer.core.event.about;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AboutEventViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    @Mock
    private AboutEventView aboutEventView;
    @Mock private EventRepository eventRepository;
    @Mock private CopyrightRepository copyrightRepository;
    @Mock private DatabaseChangeListener<Copyright> copyrightChangeListener;

    @Mock
    private Event event;

    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<Event> success;
    @Mock
    Observer<Copyright> showCopyright;
    @Mock
    Observer<String> showCopyrightDeleted;
    @Mock
    Observer<Boolean> changeCopyrightMenuItem;
    @Mock
    Observer<String> error;

    private AboutEventViewModel aboutEventViewModel;
    private static final Event EVENT = new Event();
    private static final Copyright COPYRIGHT = new Copyright();
    private static final long ID = 10L;

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        ContextManager.setSelectedEvent(event);
        aboutEventViewModel = new AboutEventViewModel(eventRepository, copyrightRepository, copyrightChangeListener);
        ContextManager.setSelectedEvent(null);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadEventSuccessfully() {
        when(eventRepository.getEvent(anyLong(), anyBoolean())).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(eventRepository, progress, success);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getSuccess().observeForever(success);

        aboutEventViewModel.loadEvent(false);

        inOrder.verify(eventRepository).getEvent(anyLong(), anyBoolean());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(EVENT);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldLoadCopyrightSuccessfully() {
        when(copyrightRepository.getCopyright(anyLong(), anyBoolean())).thenReturn(Observable.just(COPYRIGHT));
        when(copyrightChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        InOrder inOrder = Mockito.inOrder(copyrightRepository, aboutEventView, progress, showCopyright);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getShowCopyright().observeForever(showCopyright);

        aboutEventViewModel.loadCopyright(false);

        inOrder.verify(copyrightRepository).getCopyright(anyLong(), anyBoolean());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(showCopyright).onChanged(COPYRIGHT);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldChangeCopyrightMenuTextOnSuccessfulLoad() {
        when(copyrightRepository.getCopyright(anyLong(), anyBoolean())).thenReturn(Observable.just(COPYRIGHT));
        when(copyrightChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        InOrder inOrder = Mockito.inOrder(eventRepository, changeCopyrightMenuItem);

        aboutEventViewModel.getChangeCopyrightMenuItem().observeForever(changeCopyrightMenuItem);

        aboutEventViewModel.loadCopyright(false);

        inOrder.verify(changeCopyrightMenuItem).onChanged(false);
    }

    @Test
    public void shouldShowErrorOnEventLoadFailure() {
        when(eventRepository.getEvent(anyLong(), anyBoolean())).thenReturn(Observable.error(new Throwable("Error")));

        InOrder inOrder = Mockito.inOrder(eventRepository, progress, error);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getError().observeForever(error);

        aboutEventViewModel.loadEvent(false);

        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged("Error");
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRefreshEventSuccessfully() {
        when(eventRepository.getEvent(anyLong(), anyBoolean())).thenReturn(Observable.just(EVENT));

        InOrder inOrder = Mockito.inOrder(eventRepository, aboutEventView, progress, success);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getSuccess().observeForever(success);

        aboutEventViewModel.loadEvent(true);

        inOrder.verify(eventRepository).getEvent(anyLong(), anyBoolean());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(EVENT);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRefreshCopyrightSuccessfully() {
        when(copyrightRepository.getCopyright(anyLong(), anyBoolean())).thenReturn(Observable.just(COPYRIGHT));
        when(copyrightChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        InOrder inOrder = Mockito.inOrder(copyrightRepository, progress, showCopyright);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getShowCopyright().observeForever(showCopyright);

        aboutEventViewModel.loadCopyright(true);

        inOrder.verify(copyrightRepository).getCopyright(anyLong(), anyBoolean());
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(showCopyright).onChanged(COPYRIGHT);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldDeleteCopyrightSuccessfully() {
        when(copyrightRepository.deleteCopyright(anyLong())).thenReturn(Completable.complete());
        when(copyrightRepository.getCopyright(anyLong(), anyBoolean())).thenReturn(Observable.just(COPYRIGHT));

        InOrder inOrder = Mockito.inOrder(copyrightRepository, aboutEventView, progress, showCopyrightDeleted);

        aboutEventViewModel.getProgress().observeForever(progress);
        aboutEventViewModel.getShowCopyrightDeleted().observeForever(showCopyrightDeleted);

        aboutEventViewModel.deleteCopyright(ID);

        inOrder.verify(copyrightRepository).deleteCopyright(ID);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(showCopyrightDeleted).onChanged("Copyright Deleted");
        inOrder.verify(progress).onChanged(false); // Delete copyright operation and load copyright operation
    }
}
