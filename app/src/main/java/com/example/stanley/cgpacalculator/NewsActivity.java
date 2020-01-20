package com.example.stanley.cgpacalculator;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class NewsActivity extends AppCompatActivity {

    String address = "https://www.funai.edu.ng/news";
    WebView webView;
    ProgressBar progressBar;
    private ProgressDialog npd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        progressBar = findViewById(R.id.progressBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News");
        npd = new ProgressDialog(this);

        webView = findViewById(R.id.webView);
        progressBar.setMax(100);
        progressBar.setProgress(20);
        npd.setMessage("Loading Please wait...");
        npd.show();
        npd.setCanceledOnTouchOutside(false);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChangeed(WebView view, int progress) {
                progressBar.setProgress(progress);
                setTitle("Loading");
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, progress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                npd.dismiss();
                progressBar.setProgress(50);
                setTitle("Loading");
//                if (progress == 100) {
//                    frameLayout.setVisibility(View.GONE);
//                    setTitle(view.getTitle());
//                }
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setProgress(100);
                progressBar.setVisibility(View.GONE);
                setTitle(view.getTitle());

            }

            @Override
            public void onReceivedSslError(WebView v, SslErrorHandler handler, SslError er) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.setVisibility(View.VISIBLE);
                return true;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(address);
        progressBar.setProgress(0);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
            }

            public boolean onOptionsItemSelected (MenuItem item){
                int id = item.getItemId();
                if (id == android.R.id.home) {
            finish();
                }
                return super.onOptionsItemSelected(item);
            }
        }
