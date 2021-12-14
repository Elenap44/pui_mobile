package es.upm.hcid.pui.assignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class DownloadArticleThread implements Runnable {

    private ShowArticleActivity activity;
    private int id;

    public DownloadArticleThread(ShowArticleActivity activity, int id){
        this.activity = activity;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            Article article = MainActivity.modelManager.getArticle(id);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        activity.initialize(article);
                    } catch (ServerCommunicationError serverCommunicationError) {
                        serverCommunicationError.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
