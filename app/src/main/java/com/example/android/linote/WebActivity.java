package com.example.android.linote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import static com.example.android.linote.R.id.webview;

public class WebActivity extends AppCompatActivity {

    private static WebView myWebView;
    private ProgressBar progressBar;
    private Uri mBackUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_line);

        Intent intent = getIntent();
        Uri mCurrentUri = getIntent().getData();
        if(intent.hasExtra("backUri")) {
            mBackUri = Uri.parse(intent.getStringExtra("backUri"));
        }

        setTitle(mCurrentUri.toString());


        myWebView = (WebView) findViewById(webview);
        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

        Bundle bundle = getIntent().getExtras();
        setTitle(bundle.getString("title"));

        loadProgress();

        myWebView.loadUrl(mCurrentUri.toString());
    }

    public void loadProgress(){
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress * 1);
                progressBar.setSecondaryProgress(progress * 2);
                if (progressBar.getProgress() == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.webactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mBackUri != null){
                    Intent backIntent = new Intent(this, WordDetails.class);
                    backIntent.setData(mBackUri);
                    startActivity(backIntent);
                    return true;
                }
                NavUtils.navigateUpFromSameTask(WebActivity.this);
                return true;
            case R.id.action_reload:
                myWebView.reload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
