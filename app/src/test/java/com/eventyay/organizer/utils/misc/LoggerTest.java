package com.eventyay.organizer.utils.misc;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.common.model.SimpleModel;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import timber.log.Timber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("PMD.JUnit4TestShouldUseBeforeAnnotation")
public class LoggerTest {

    @Rule public final MockitoRule rule = MockitoJUnit.rule();
    private static Timber.Tree tree;

    @BeforeClass
    public static void setUp() {
        tree = mock(Timber.Tree.class);
        Timber.plant(tree);
    }

    @Test
    public void shouldLogError() {
        Throwable testThrowable = new Throwable("Severe Error");
        Logger.logError(testThrowable);
        verify(tree).e(eq(testThrowable), any(), eq(testThrowable.getMessage()));
    }

    @Test
    public void shouldLogItem() {
        SimpleModel simpleModel = new SimpleModel(2, "Title", "Description");
        Logger.logSuccess(simpleModel);
        verify(tree).i(contains(simpleModel.toString()));
    }

    @Test
    public void shouldLogList() {
        List simpleModel = Arrays.asList(new SimpleModel[]{
            new SimpleModel(2, "Title", "Description"),
            new SimpleModel(3, "New Title", "Good description")
        });
        Logger.logSuccess(simpleModel);
        verify(tree, atLeastOnce()).i(matches(".*\\b(List|count:|" + simpleModel.size() + ")\\b.*"));
    }

}
