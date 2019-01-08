package com.patrickgrady.todayslists.Managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.patrickgrady.todayslists.Utilities.HttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class QuoteManager implements UpdateManager.TimeSensitive {
    private static String filename = "quote.txt";

    private static QuoteManager instance;
    Context context;
    String quote, img;

    ImageView quoteImage;
    TextView quoteView;
    Drawable imageDrawable;

    // constructor/set-up

    private QuoteManager(Context c, TextView qv, ImageView qi) {
        context = c;
        quoteView = qv;
        quoteImage = qi;
        imageDrawable = null;
    }

    public static QuoteManager getInstance(Context c, TextView qv, ImageView qi) {
        init(c, qv, qi);

        return instance;
    }

    public static QuoteManager getInstance() throws Error {
        if (instance == null) {
            throw new Error();
        }

        return instance;
    }

    public static void init(Context c, TextView qv, ImageView qi) {
        if (instance == null) {
            instance = new QuoteManager(c, qv, qi);
        }
        else {
            instance.quoteView = qv;
            instance.quoteImage = qi;
        }
        instance.load();

    }


    // private helper/convenience methods

    // convenience method
    @Override
    public void update() {

        final String[] quote = {"",""};
        HttpUtils.get("qod.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONObject contents = (JSONObject)((JSONArray) ((JSONObject) response.get("contents")).get("quotes")).get(0);
                    quote[0] = contents.getString("quote");
                    quote[1] = contents.getString("background");
                    writeToFile(quote[0], quote[1]);
                    updateView(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Do something with the response
            }
        });
    }

    @Override
    public void refresh() {
        load();
    }


    // low level file stuff
    private void writeToFile(String q, String i) {
        quote = q;
        img = i;
        try {
            PrintWriter writer = new PrintWriter(getFile());
            writer.println(quote);
            writer.println(img);
            writer.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void load() {

        boolean findOnline = false;
        try {
            Scanner scanner = new Scanner(getFile());
            quote = scanner.nextLine();
            img = scanner.nextLine();
        } catch(FileNotFoundException e) {
            quote = "";
            img = "";

        }

        try {
            FileInputStream is = new FileInputStream(getDrawableFile());
            Bitmap x = BitmapFactory.decodeStream(is);
            System.out.println("got bitmap");
            imageDrawable = new BitmapDrawable(null, x);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            imageDrawable = null;
            findOnline = true;
        }


        updateView(findOnline);
    }

    private void updateView(boolean findOnline) {
        quoteView.setText(quote);
        if(!img.equals("") && findOnline) {
            System.out.println("needed to get it online");
            new GetDrawable().execute(img);
        }
        if(imageDrawable != null) {
            System.out.println("setting the image here");
            System.out.println(quote);
            quoteImage.setImageDrawable(imageDrawable);
        }
    }

    class GetDrawable extends AsyncTask<String, Void, Drawable> {

        protected Drawable doInBackground(String... urls) {
            try {
                Bitmap x;

                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();

                x = BitmapFactory.decodeStream(input);
                saveBitmapToFile(x, Bitmap.CompressFormat.PNG,100);
                return new BitmapDrawable(null, x);
            } catch (Exception e) {
                return null;
            }

        }

        protected void onPostExecute(Drawable result) {
            if(result != null) {
                imageDrawable = result;
            }
        }
    }



    private File getDrawableFile() { return new File(getDirectory(), "drawable.png"); }
    private File getFile() {
        return new File(getDirectory(), filename);
    }

    private File getDirectory() {
        File directory = new File(context.getFilesDir(), "quotes");

        // creates the directory if not present yet
        directory.mkdir();

        return directory;
    }

    boolean saveBitmapToFile(Bitmap bm, Bitmap.CompressFormat format, int quality) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getDrawableFile());

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
}
