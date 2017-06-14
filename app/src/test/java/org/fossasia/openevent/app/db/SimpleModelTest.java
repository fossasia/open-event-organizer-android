package org.fossasia.openevent.app.db;

import org.fossasia.openevent.app.data.db.DatabaseRepository;
import org.fossasia.openevent.app.data.models.SimpleModel;
import org.fossasia.openevent.app.data.models.SimpleModel_Table;
import org.fossasia.openevent.app.data.models.User;
import org.fossasia.openevent.app.data.models.UserDetail;
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

        databaseRepository.getItems(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertSubscribed()
            .assertValue(savedModel -> savedModel.equals(simpleModel));
    }

    @Test
    public void shouldSaveAndDelete() {
        // Currently only checking no exception
        // TODO: Add correct loading and deleting checks

        User user = new User();
        UserDetail userDetail = new UserDetail();
        user.setUserDetail(userDetail);
        user.setId(2);
        user.setEmail("master.chief@nova.com");
        user.setLastAccessTime("2017-06-06T13:23:45.234");
        user.setSignupTime("2017-06-01T22:48:12.986");
        userDetail.setFirstName("Master");
        userDetail.setLastName("Chief");
        userDetail.setDetails("A detailed description about Master Chief");

        databaseRepository.save(user).subscribe();

        databaseRepository.deleteAll(User.class, UserDetail.class).subscribe();
    }

}
