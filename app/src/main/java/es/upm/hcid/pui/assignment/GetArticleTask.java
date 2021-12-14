package es.upm.hcid.pui.assignment;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

import java.util.List;

public class GetArticleTask implements Runnable {
    MainActivity activity;

    GetArticleTask(MainActivity activity) {
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
