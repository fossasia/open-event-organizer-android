package com.eventyay.organizer.utils.misc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.eventyay.organizer.common.mvp.view.Emptiable;
import com.eventyay.organizer.common.mvp.view.Erroneous;
import com.eventyay.organizer.common.mvp.view.ItemResult;
import com.eventyay.organizer.common.mvp.view.Progressive;
import com.eventyay.organizer.common.mvp.view.Refreshable;
import com.eventyay.organizer.common.mvp.view.Successful;
import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.common.rx.ViewTransformers;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class ViewTransformersTest {

    private static final String TAG = "Test";

    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Mock private CombinedView view;

    private interface CombinedView
            extends Progressive,
                    Erroneous,
                    ItemResult<String>,
                    Refreshable,
                    Successful,
                    Emptiable<String> {}

    private static void testProgessive(CombinedView view) {
        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).showProgress(true);
        inOrder.verify(view).showProgress(false);
    }

    private static void testResult(CombinedView view, String result) {
        verify(view).showResult(result);
    }

    private static void testError(CombinedView view, String message) {
        verify(view).showError(message);
    }

    @Test
    public void testProgressiveErroneousResultSuccess() {
        Observable.just(TAG)
                .compose(ViewTransformers.progressiveErroneousResult(view))
                .subscribe(Logger::logSuccess, Logger::logError);

        testProgessive(view);
        testResult(view, TAG);
    }

    @Test
    public void testProgressiveErroneousResultError() {
        String[] io = new String[0];

        Observable.fromCallable(() -> io[2])
                .compose(ViewTransformers.progressiveErroneousResult(view))
                .subscribe(Logger::logSuccess, Logger::logError);

        testProgessive(view);
        testError(view, "2");
    }

    @Test
    public void testProgressiveErroneousRefreshableNoRefresh() {
        Observable.just(TAG)
                .compose(ViewTransformers.progressiveErroneousRefresh(view, false))
                .subscribe(Logger::logSuccess, Logger::logError);

        verify(view, never()).onRefreshComplete(anyBoolean());
    }

    @Test
    public void testProgressiveErroneousRefreshableRefresh() {
        Observable.just(TAG)
                .compose(ViewTransformers.progressiveErroneousRefresh(view, true))
                .subscribe(Logger::logSuccess, Logger::logError);

        verify(view).onRefreshComplete(true);
    }

    @Test
    public void testEmptiableNonEmpty() {
        Observable.fromArray(TAG)
                .toList()
                .compose(ViewTransformers.emptiable(view, Collections.emptyList()))
                .subscribe(Logger::logSuccess, Logger::logError);

        verify(view).showEmptyView(false);
        verify(view).showEmptyView(false);
    }

    @Test
    public void testEmptiableEmpty() {
        Observable.fromIterable(new ArrayList<String>())
                .toList()
                .compose(ViewTransformers.emptiable(view, Collections.emptyList()))
                .subscribe(Logger::logSuccess, Logger::logError);

        verify(view).showEmptyView(false);
        verify(view).showEmptyView(true);
    }

    @Test
    public void testEmptiableList() {
        List<String> list = spy(new ArrayList<>());

        Observable.fromArray(TAG)
                .toList()
                .compose(ViewTransformers.emptiable(view, list))
                .subscribe(Logger::logSuccess, Logger::logError);

        verify(list).clear();
        verify(list).addAll(any());
        verify(view).showResults(list);
    }
}
