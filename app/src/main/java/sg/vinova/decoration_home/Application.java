package sg.vinova.decoration_home;

import android.os.Bundle;

import com.blankj.utilcode.util.Utils;

public class Application extends android.app.Application {
    private static Application instance;

    public static Application newInstance() {
        if(instance == null) instance = new Application();
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
