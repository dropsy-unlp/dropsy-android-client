package com.fuentesfernandez.dropsy;

import android.app.Application;

import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.PersistenceConfig;
import com.fuentesfernandez.dropsy.Model.Project;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseSpec database = PersistenceConfig.registerSpec(/**db version**/1);
        database.match(Project.class);
    }

}
