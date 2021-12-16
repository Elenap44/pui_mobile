package es.upm.hcid.pui.assignment;

import java.util.List;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class GetArticle implements Runnable {
    MainActivity activity;

    GetArticle(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            List<Article> res = MainActivity.modelManager.getArticles();
            activity.runOnUiThread(() -> {
                activity.receiveData(res);
            });

        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
    }
}
