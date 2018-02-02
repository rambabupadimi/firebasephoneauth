package c.com.phoneauthfirebase;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ramu on 14-12-2017.
 */

public class FirebaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
