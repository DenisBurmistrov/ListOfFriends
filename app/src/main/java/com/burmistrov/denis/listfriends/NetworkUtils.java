package com.burmistrov.denis.listfriends;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String VK_API_BASE_URL = "https://api.vk.com/";
    private static final String VK_FRIENDS_GET = "method/friends.get";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_FIELDS = "fields";
    private static final String PARAM_VERSION = "v";
    private static final String ACCESS_TOKEN = "access_token";




    public static URL generateURL(String token, String uid) {
        Uri builtUri = Uri.parse(VK_API_BASE_URL + VK_FRIENDS_GET)
                .buildUpon().appendQueryParameter(PARAM_USER_ID, uid)
                .appendQueryParameter(PARAM_FIELDS, "photo_200_orig")
                .appendQueryParameter(PARAM_VERSION, "5.92")
                .appendQueryParameter(ACCESS_TOKEN, token)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromURL(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            }
            else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }
}

