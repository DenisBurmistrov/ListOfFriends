package com.burmistrov.denis.listfriends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VkLoginActivity extends Activity {


        private WebView webview;
        static final String VK_API_ID ="6761488";
        static  final String VK_REDIRECT_URL = "http://api.vkontakte.ru/blank.html";


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_vk_login);



            //Получаем элементы
            webview = findViewById(R.id.web);



            webview.getSettings().setJavaScriptEnabled(true);
            webview.setVerticalScrollBarEnabled(false);
            webview.setHorizontalScrollBarEnabled(false);
            webview.clearCache(true);

            //Чтобы получать уведомления об окончании загрузки страницы
            webview.setWebViewClient(new VkWebViewClient());

            String url = "http://oauth.vk.com/authorize?client_id=" + VK_API_ID + "&display=mobile" +
                    "&scope=friends&redirect_uri=" + VK_REDIRECT_URL + "&response_type=token";
            webview.loadUrl(url);
            webview.setVisibility(View.VISIBLE);


        }

        class VkWebViewClient extends WebViewClient {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                parseUrl(url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith("http://oauth.vk.com/authorize") || url.startsWith("http://oauth.vk.com/oauth/authorize")) {
                }
            }

            private void parseUrl(String url) {
                try {
                    if (url == null) {
                        return;
                    }
                    if (url.startsWith(VK_REDIRECT_URL)) {
                        if (!url.contains("error")) {
                            String[] auth = VkUtil.parseRedirectUrl(url);
                            webview.setVisibility(View.GONE);


                            //Строим данные
                            Intent intent = new Intent();
                            intent.putExtra("token", auth[0]);
                            intent.putExtra("uid", auth[1]);

                            //Возвращаем данные
                            setResult(Activity.RESULT_OK, intent);
                            finish();

                        } else {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    } else if (url.contains("error?err")) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }


    }

