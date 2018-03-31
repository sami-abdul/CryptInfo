package com.example.tss.cryptinfo.views.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CoinWidgetRemoteService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new CoinWidgetRemoteViewFactory(this);
    }
}
