package com.example.tss.cryptinfo.views;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetRemoteService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new WidgetRemoteViewFactory(this);
    }
}
