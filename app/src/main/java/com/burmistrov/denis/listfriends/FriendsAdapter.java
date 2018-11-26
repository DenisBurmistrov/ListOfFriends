package com.burmistrov.denis.listfriends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.burmistrov.denis.listfriends.FriendsAdapter.cache;


public class FriendsAdapter extends BaseAdapter {
    private ArrayList<Friend> listData;
    private LayoutInflater layoutInflater;
    public static Map<String, Object> cache = new HashMap<>();
    public static final String URL_OF_PHOTO = "ulr of photo";


    public FriendsAdapter(Context context, ArrayList<Friend> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position).getLink();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_layout, null);
            holder = new ViewHolder();
            holder.photoOfFriend = convertView.findViewById(R.id.ib_row_photo_of_friend);
            holder.nameOfFriend = convertView.findViewById(R.id.tv_row_name_of_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameOfFriend.setText(listData.get(position).getName());

        if (holder.photoOfFriend != null) {
            if (cache.containsKey(listData.get(position).getLink())){
                holder.photoOfFriend.setImageBitmap((Bitmap) cache.get(listData.get(position).getLink()));
            }
            new BitmapWorkerTask(holder.photoOfFriend).execute(listData.get(position).getLink());
        }

        if (holder.photoOfFriend != null) {
            holder.photoOfFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), FullscreenActivity.class);
                    intent.putExtra(URL_OF_PHOTO, listData.get(position).getLink());
                    v.getContext().startActivity(intent);

                }
            });
        }


        return convertView;
    }

    class ViewHolder {
        ImageView photoOfFriend;
        TextView nameOfFriend;
    }

}


class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String imageUrl;

    public BitmapWorkerTask(ImageView imageView) {

        imageViewReference = new WeakReference<>(imageView);
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        imageUrl = params[0];
        return LoadImage(imageUrl);
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }



    private Bitmap LoadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            cache.put(URL, bitmap);
            in.close();
        } catch (IOException e1) {
        }
        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL)
            throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }
}



