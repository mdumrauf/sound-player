package com.zauberlabs.soundplayer.app;

import android.content.Context;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;

public final class ExpansionPackUtils {
  private final static String TAG = ExpansionPackUtils.class.getSimpleName();
  private static final String OBB_FILE_PATH = "%s/Android/obb/%s/main.%d.%s.obb";
  private static final String KEY = null;

  private ExpansionPackUtils() {
    throw new IllegalStateException("It is an utility class!");
  }

  public static boolean existExpansionFile(Context context, int version) {
    return getExpansionFile(context, version).exists();
  }

  private static File getExpansionFile(Context context, int version) {
    final String packageName = context.getPackageName();
    final String obbFilePath = String.format(OBB_FILE_PATH, Environment.getExternalStorageDirectory(), packageName, version, packageName);
    final File mainFile = new File(obbFilePath);
    return mainFile;
  }

  public static void deleteOldVersions(Context context, int version) {
    for (int i = 0; i < version; i++) {
      try {
        File mainFile = getExpansionFile(context, i);
        if (mainFile.exists()) {
          Log.d(TAG, "Deleting Expansion pack, file: " + mainFile.getPath());
          boolean deleted = mainFile.delete();
          if (deleted) {
            Log.d(TAG, "Expansion pack, file: " + mainFile.getPath() + " deleted");
          } else {
            Log.d(TAG, "Expansion pack, file: " + mainFile.getPath() + "cant be deleted");
          }
        }
      } catch(Exception e) {
        Log.d(TAG, "Error while delete old Expansion pack files, Version: " + version);
      }
    }
  }

  public static boolean isObbMounted(Context context, StorageManager storageManager, int version) {
    File mainFile = getExpansionFile(context, version);
    return storageManager.isObbMounted(mainFile.getAbsolutePath());
  }

  public static void mount(final Context context, final StorageManager storageManager, final int version, final StringCommand onObbMounted) {
    File mainFile = getExpansionFile(context, version);
    Log.d(TAG, "Mounting Expansion pack, file: " + mainFile.getPath());
    if (!mainFile.exists()) {
      throw new RuntimeException("File: " + mainFile.getPath() + " doesn't exist");
    }
    if (storageManager.isObbMounted(mainFile.getAbsolutePath())) {
      onObbMounted.execute(context, storageManager.getMountedObbPath(mainFile.getAbsolutePath()));
      return;
    }

    final boolean wasQueued = storageManager.mountObb(mainFile.getPath(), KEY, new OnObbStateChangeListener() {
      @Override
      public void onObbStateChange(String path, int state) {
        super.onObbStateChange(path, state);
        if (state != OnObbStateChangeListener.MOUNTED) {
          throw new RuntimeException("Obb was not mounted: " + state);
        }
        onObbMounted.execute(context, storageManager.getMountedObbPath(path));
      }
    });
    if (!wasQueued) {
      throw new RuntimeException("Mount process was not queued");
    }
  }
}