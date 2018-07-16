package me.opia.note.noteopia.Utils.Utils.RealmConf;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmConf extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("data.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);



    }
}
