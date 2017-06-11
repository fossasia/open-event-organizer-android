package org.fossasia.openevent.app.db;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.models.SimpleModel;
import org.fossasia.openevent.app.data.models.SimpleModel_Table;
import org.fossasia.openevent.app.db.config.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class SimpleModelTest extends BaseUnitTest {

    private DatabaseRepository databaseRepository;

    @Before
    public void setUp() {
        databaseRepository = new DatabaseRepository();
    }

    @Test
    public void testBasicRead() {
        SimpleModel simpleModel = new SimpleModel(1, "Title", "Greta Jones");
        databaseRepository
            .save(simpleModel)
            .subscribe();

        databaseRepository.getItem(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertSubscribed()
            .assertValue(savedModel -> savedModel.equals(simpleModel));
    }

    @Test
    public void oneToMany() {
        // TODO: To be implemented later
    }

}
