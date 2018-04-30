package com.example.balmaz.saildatamanagerclient;

import android.Manifest;
import android.app.Application;

import com.holidaycheck.permissify.DialogText;
import com.holidaycheck.permissify.PermissifyConfig;

import java.util.HashMap;

/**
 * Created by balmaz on 2018. 04. 16..
 */

public class SailDataManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PermissifyConfig permissifyConfig = new PermissifyConfig.Builder()
                .withDefaultTextForPermissions(new HashMap<String, DialogText>() {{
                    put(Manifest.permission_group.LOCATION, new DialogText(R.string.permission_rat, R.string.permission_deny));
                }})
                .build();

        PermissifyConfig.initDefault(permissifyConfig);
    }
}
