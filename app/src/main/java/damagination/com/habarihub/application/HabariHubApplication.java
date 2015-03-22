package damagination.com.habarihub.application;

import android.app.Application;

/**
 * Created by Damas on 3/21/15.
 */
public class HabariHubApplication extends Application{

    private String APPNAME = "HabariHub";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public boolean isFirstRun(){
        return false;
    }

    public void setHasFirstRun(){

    }

    public void setTheme(){

    }

    public void getThemeName(){

    }


}
