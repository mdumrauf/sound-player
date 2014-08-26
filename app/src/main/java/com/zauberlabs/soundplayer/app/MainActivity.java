package com.zauberlabs.soundplayer.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends Activity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int VERSION = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    return id == R.id.action_settings || super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  public void onClickFromResRaw(View view) {
    playIfNotNull(MediaPlayer.create(this, R.raw.welcome_explanation));
  }

  public void onClickFromAssets(View view) {
    try {
      AssetFileDescriptor afd = getAssets().openFd("welcome_explanation.mp3");
      MediaPlayer mediaPlayer = new MediaPlayer();
      mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
      mediaPlayer.prepare();
      mediaPlayer.start();
    }
    catch (Exception e) {
      Toast.makeText(this, "Could not create a MediaPlayer", Toast.LENGTH_SHORT).show();
    }
  }

  public void onClickFromInternalStorage1(View view) {
    copyFileFromAssetsToInternalStorage(Context.MODE_PRIVATE);
    playFromInternalStorage(getFilesDir());
  }

  public void onClickFromInternalStorage2(View view) {
    copyFileFromAssetsToInternalStorage(Context.MODE_WORLD_READABLE);
    playFromInternalStorage(getFilesDir());
  }

  public void onClickFromExpansionPack(View view) {
    if (!ExpansionPackUtils.existExpansionFile(getBaseContext(), VERSION)) {
      Toast.makeText(this, "Error: Expansion Pack does not exist", Toast.LENGTH_SHORT).show();
      return;
    }
    final StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
    ExpansionPackUtils.mount(this, storageManager, VERSION, new StringCommand() {
      @Override
      public void execute(Context context, String externalStoragePath) {
        Log.d(TAG, "Expansion pack: " + externalStoragePath);
        playFromExpansionPack(externalStoragePath);
      }
    });
  }

  private void playFromExpansionPack(String externalStoragePath) {
    playFromInternalStorage(new File(externalStoragePath));
  }

  private void copyFileFromAssetsToInternalStorage(int fileMode) {
    try {
      InputStream inputStream = getAssets().open("welcome_explanation.mp3");
      FileOutputStream outputStream = openFileOutput("welcome_explanation.mp3", fileMode);
      // Copy data
      byte[] buffer = new byte[8192];
      int length;
      while ( (length=inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
      // Close the streams
      inputStream.close();
      outputStream.flush();
      outputStream.close();
    } catch (Exception e) {
      Toast.makeText(this, "Could not move file to internal storage", Toast.LENGTH_SHORT).show();
    }
  }

  private void playIfNotNull(MediaPlayer mediaPlayer) {
    if (mediaPlayer != null) {
      mediaPlayer.start();
    } else {
      Toast.makeText(this, "Could not create a MediaPlayer", Toast.LENGTH_SHORT).show();
    }
  }

  private void playFromInternalStorage(File filesDir) {
    File file = new File(filesDir, "welcome_explanation.mp3");
    try {
      MediaPlayer mediaPlayer = new MediaPlayer();
      if (!file.exists()) {
        throw new IOException("File does not exist");
      }
      mediaPlayer.setDataSource(this, Uri.fromFile(file));
      mediaPlayer.prepare();
      mediaPlayer.start();
    }
    catch (Exception e) {
      Toast.makeText(this, "Could not create a MediaPlayer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    } finally {
      file.delete();
    }
  }
}
