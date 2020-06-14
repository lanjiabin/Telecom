package com.ct.client.telecom;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText mCityET;
    private Button mRunBtn, mLocationBtn, mSystemInfoBtn, mResultsBtn;
    private WebView mWebView;
    TextView mDcSystemSizeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadWebView();
        onClick();
    }

    public void initView() {
        mCityET = findViewById(R.id.cityET);
        mRunBtn = findViewById(R.id.runBtn);
        mWebView = findViewById(R.id.webView);
        mLocationBtn = findViewById(R.id.locationBtn);
        mSystemInfoBtn = findViewById(R.id.systemInfoBtn);
        mResultsBtn = findViewById(R.id.resultsBtn);
        mDcSystemSizeTV = findViewById(R.id.dc_system_size);


    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadWebView() {
        mWebView.loadUrl("https://pvwatts.nrel.gov/index.php");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        String code = mCityET.getText().toString();
        code = "Beijing";
        final String strJS = String.format("javascript:document.getElementById('myloc').value='%s';", code);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.v("lanjiabin", "loadUrl =" + url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebView.evaluateJavascript(strJS, null);
                    }
                }
            }
        });
    }

    public void onClick() {
        mRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:document.getElementById('go').click();");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);
                }
            }
        });

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:appNav('location', 'left');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);
                }
            }
        });

        mSystemInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:appNav('systeminfo', 'right');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);

                    //加载完成后才会执行这个方法
                    mWebView.loadUrl("javascript:window.java_obj.showDescription("
                            + "document.getElementById('module_type').value"
                            + ");");
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            Log.v("lanjiabin", "url=" + url);
                            //获得一个input的值方法
                            mWebView.loadUrl("javascript:window.java_obj.showDescription("
                                    + "document.querySelector('input[name=\"system_capacity\"]').getAttribute('value')"
                                    + ",\'system_capacity\'" +
                                    ");");

                            //获得一个select的值的方法
                            mWebView.loadUrl("javascript:window.java_obj.showDescription("
                                    + "document.getElementById('module_type').value"
                                    + ",\'module_type\'" +
                                    ");");
                        }
                    });
//                    //new ValueCallback<String> 中的范型只可以传 String型 也就是说只能回调String当作数据
//                    mWebView.evaluateJavascript("document.querySelector('input[name=\"system_capacity\"]').getAttribute('value');"
//                            , new ValueCallback<String>() {
//                                @Override
//                                public void onReceiveValue(String value) {
//                                    if (value!=null){
//                                        mDcSystemSizeTV.setText(value);
//                                    }
//                                }
//                            });
                }
            }
        });

        mResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:appNav('results', 'right');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);
                }
            }
        });

    }


    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showDescription(String str,String tag) {
            Log.v("lanjiabin", "value=" + str+"  tag= "+tag);
            if (str != null) {
                mDcSystemSizeTV.setText(str);
            }
        }
    }


}
