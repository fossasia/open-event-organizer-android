package org.fossasia.openevent.app.robo.rule;

import org.fossasia.openevent.app.OrgaApplication;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.robolectric.RuntimeEnvironment;

public final class DatabaseTestRule implements TestRule {

    private DatabaseTestRule() {
        // Never Called
    }

    public static DatabaseTestRule create() {
        return new DatabaseTestRule();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                OrgaApplication.initializeDatabase(RuntimeEnvironment.application);
                try {
                    base.evaluate();
                } finally {
                    OrgaApplication.destroyDatabase();
                }
            }
        };
    }
}
