package com.hashmybag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This class is handling pogress dialogs for web services
 * like for wait
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-21
 */

public class WebActivity extends AppCompatActivity {
    public static final int DIALOG_LOADING = 1;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private WebView myWebView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        WebActivity.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.web_layout);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        myWebView = (WebView) findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        startWebView(url);
    }

    private void startWebView(String url) {
        //Create new webview Client to show progress dialog when opening a url or click on link
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showDialog(DIALOG_LOADING);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    dismissDialog(DIALOG_LOADING);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                Log.v("URl ", url);
                super.onPageCommitVisible(view, url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);

            }
        });

        // Javascript inabled on webview
        //webView.getSettings().setJavaScriptEnabled(true);
        //Load url in webview
        myWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    ;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING:
                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //here we set layout of progress dialog
                dialog.setContentView(R.layout.custom_progress_dialog);
                dialog.setCancelable(true);

                return dialog;

            default:
                return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReciever);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        if (!check()) {
                            buildAlertMessageNoGps();
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Check for location manager
     *
     * @return
     */


    private boolean check() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!check()) {
                buildAlertMessageNoGps();
            }
        }
    }
}
