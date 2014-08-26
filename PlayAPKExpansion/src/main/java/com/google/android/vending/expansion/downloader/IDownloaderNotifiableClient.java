package com.google.android.vending.expansion.downloader;

import android.app.PendingIntent;
import android.os.Messenger;

public interface IDownloaderNotifiableClient extends IDownloaderClient {

  void resendState();

  void setMessenger(Messenger mClientMessenger);

  void setClientIntent(PendingIntent mPendingIntent);
}
