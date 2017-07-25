package org.fossasia.openevent.app.robo.db;

import org.fossasia.openevent.app.common.data.db.DatabaseRepository;
import org.fossasia.openevent.app.common.data.models.dto.SimpleModel;
import org.fossasia.openevent.app.common.data.models.dto.SimpleModel_Table;
import org.fossasia.openevent.app.robo.db.config.BaseUnitTest;
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
            .save(SimpleModel.class, simpleModel)
            .subscribe();

        databaseRepository.getItems(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertSubscribed()
            .assertValue(savedModel -> savedModel.equals(simpleModel));
    }

}
