package br.com.lavicameta;

import android.app.Application;
import br.com.lavicameta.util.NotificationHelper;

public class LavicametaApp extends Application {

    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer(this);
        NotificationHelper.criarCanal(this);
    }

    public AppContainer container() {
        return container;
    }
}
