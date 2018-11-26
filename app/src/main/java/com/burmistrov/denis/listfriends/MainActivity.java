package com.burmistrov.denis.listfriends;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.burmistrov.denis.listfriends.FriendsAdapter.cache;
import static com.burmistrov.denis.listfriends.NetworkUtils.generateURL;
import static com.burmistrov.denis.listfriends.NetworkUtils.getResponseFromURL;

public class MainActivity extends Activity {

    private URL generatedURL;
    private ListView listOfFriends;
    public final static int REQUEST_AUTH_VK = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivityForResult(new Intent(this, VkLoginActivity.class), REQUEST_AUTH_VK);



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_AUTH_VK:
                if (resultCode == Activity.RESULT_OK) {
                    //получение токена и айди после авторизации
                    String uid = data.getStringExtra("uid");
                    String token = data.getStringExtra("token");

                    generatedURL = generateURL(token, uid);

                    new VkQueryTask().execute(generatedURL);
                }
                break;
        }
    }

    public class VkQueryTask extends AsyncTask<URL, Void, String> {


        @Override
        protected String doInBackground(URL... urls) {
            String response = null;

            try {
                response = getResponseFromURL(urls[0]);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }


        @Override
        protected void onPostExecute(String response) {
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<Friend> friends = new ArrayList<>();
            listOfFriends = findViewById(R.id.lv_main_activity);

            try {
                JSONObject object = new JSONObject(response);
                JSONObject jsonResponse = object.getJSONObject("response");
                JSONArray jsonArray = jsonResponse.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++) {

                    stringBuilder.setLength(0);
                    stringBuilder.append(jsonArray.getJSONObject(i).getString("first_name"))
                            .append(" ")
                            .append(jsonArray.getJSONObject(i).getString("last_name"));

                    Friend friend = new Friend(stringBuilder.toString(), jsonArray.getJSONObject(i).getString("photo_200_orig"));
                    friends.add(friend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FriendsAdapter adapter = new FriendsAdapter(MainActivity.this, friends);
            listOfFriends.setAdapter(adapter);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_relogin:
                cache.clear();
                CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                MainActivity.this.recreate();

        }
        return super.onOptionsItemSelected(item);
    }
}
