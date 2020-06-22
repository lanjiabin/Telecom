package com.ct.client.telecom;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText mCityET;
    private Button mRunBtn, mLocationBtn, mSystemInfoBtn, mResultsBtn, mCheckBtn, mInputBtn;
    private WebView mWebView;
    TextView mDcSystemSizeTV;
    private boolean mIsFinish = false;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadWebView();
        onClick();
    }

    public void initView() {
        mContext = getApplicationContext();
        mCityET = findViewById(R.id.cityET);
        mRunBtn = findViewById(R.id.runBtn);
        mWebView = findViewById(R.id.webView);
        mLocationBtn = findViewById(R.id.locationBtn);
        mSystemInfoBtn = findViewById(R.id.systemInfoBtn);
        mResultsBtn = findViewById(R.id.resultsBtn);
        mDcSystemSizeTV = findViewById(R.id.dc_system_size);
        mCheckBtn = findViewById(R.id.checkBtn);
        mInputBtn = findViewById(R.id.inputBtn);

        mRunBtn.setBackgroundColor(Color.RED);

    }

    //加载主界面
    @SuppressLint("SetJavaScriptEnabled")
    public void loadWebView() {
        mWebView.loadUrl("https://pvwatts.nrel.gov/index.php");
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mIsFinish = true;
                mRunBtn.setBackgroundColor(Color.BLUE);
                Log.v("lanjiabin", "loadUrl =" + url);
            }
        });
    }

    public void selectCity() {
        String code = mCityET.getText().toString();
        final String strJS = String.format("javascript:document.getElementById('myloc').value='%s';", code);
        final String js = String.format("javascript:document.getElementById('go').click();");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(strJS, null); //填入城市
            mWebView.evaluateJavascript(js, null);  //填入城市后执行
        }
    }

    public void onClick() {
        //选择城市
        mRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFinish) {
                    selectCity();//填写城市并执行
                    Toast.makeText(mContext, "起飞中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "正在加载，请稍等哟", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //地图
        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:appNav('location', 'left');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);
                }
            }
        });

        //计算数据
        mSystemInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String js = String.format("javascript:appNav('systeminfo', 'right');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js, null);

                    //加载完成后才会执行这个方法
//                    mWebView.loadUrl("javascript:window.java_obj.showDescription("
//                            + "document.getElementById('module_type').value"
//                            + ");");
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


//                    mWebView.setWebViewClient(new WebViewClient() {
//                        @Override
//                        public void onPageFinished(WebView view, String url) {
//                            Log.v("lanjiabin", "url=" + url);
//                            //获得一个input的值方法
//                            mWebView.loadUrl("javascript:window.java_obj.showDescription("
//                                    + "document.querySelector('input[name=\"system_capacity\"]').getAttribute('value')"
//                                    + ",\'system_capacity\'" +
//                                    ");");
//
//                            //获得一个select的值的方法
//                            mWebView.loadUrl("javascript:window.java_obj.showDescription("
//                                    + "document.getElementById('module_type').value"
//                                    + ",\'module_type\'" +
//                                    ");");
//                        }
//                    });
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
        //输入数据
        mInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.requestFocus();
                //填入数据 输入11的结果是 11562
                final String js11 = String.format("javascript:document.getElementById('system_capacity').focus();"); //获得焦点
                final String js1 = String.format("javascript:document.getElementById('system_capacity').value='%s';", "11");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js1, null);
                    mWebView.evaluateJavascript(js11, null);
                }
            }
        });

        //验证数据
        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //验证输入框
                final String js2 = String.format("javascript:document.getElementById('system_capacity').blur();");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js2, null);
                }
            }
        });

        //得到计算结果
        mResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到结果
                final String js3 = String.format("javascript:appNav('results', 'right');");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(js3, null);
                }
            }
        });

    }


    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showDescription(String str, String tag) {
            Log.v("lanjiabin", "value=" + str + "  tag= " + tag);
            if (str != null) {
                mDcSystemSizeTV.setText(str);
            }
        }
    }


}
