package com.example.heshan.threadexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private EditText editText;
    private Button button;
    private ListView listView;
    private LinearLayout linearLayout;
    private String []imageArray;
    private String mUrl = null;

    //init
    private void init() {
        editText = (EditText) findViewById(R.id.edit_text_url);
        button = (Button) findViewById(R.id.button_download);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        imageArray = getResources().getStringArray(R.array.image_urls);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrl = editText.getText().toString();
                downloadRunnable();
            }
        });
    }

    //download Asynchronously
    private void downloadAsynchronously() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                downloadImage();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("Asyncronous Download", "Completed");
            }
        }.execute();
    }

    //download through Runnable
    private void downloadRunnable() {
        Thread thread = new Thread(new DownloadThread());
        thread.start();
    }

    //Thread creation : Runnable
    private class DownloadThread implements Runnable {

        public DownloadThread() {

        }

        @Override
        public void run() {
            //show the progress wheel while download. this will run on UI thread
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
            downloadImage();
        }
    }

    //download image
    private void downloadImage() {
        int count;
        URL uri = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            uri = new URL(mUrl);
            httpURLConnection = (HttpURLConnection) uri.openConnection();
            httpURLConnection.connect();
            int read = -1;

            int fileLength = httpURLConnection.getContentLength();
            inputStream = new BufferedInputStream(uri.openStream());
            byte data[] = new byte[1024];
            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                Log.d( "Downloading ---- " , String.valueOf((int) ((total * 100) / fileLength)));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(imageArray[position]);
    }
}
