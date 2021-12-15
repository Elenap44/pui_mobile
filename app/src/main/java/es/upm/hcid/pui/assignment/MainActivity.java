package es.upm.hcid.pui.assignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {

    public static String PARAM_ARTICLE = "article";

    public static ModelManager modelManager;
    ArticleAdapter adapter;
    List<Article> data;
    public static Boolean loggedIn = false;
    private String username = "";
    private String apiKey = "";

    List<String> tabs = Arrays.asList("All", "National", "International", "Sport", "Economy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupArticleData();

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabs);
        for (String tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String currentTab = tabs.get(tab.getPosition());
                adapter.setFilter(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    void setupArticleData() {
        Properties props = new Properties();
        props.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pmd-task/");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(LoginActivity.KEY_BOOLEAN, false)) {
            if (prefs.contains(LoginActivity.KEY_API) && prefs.contains(LoginActivity.KEY_USERNAME)) {
                loggedIn = true;
                this.username = prefs.getString(LoginActivity.KEY_USERNAME, "");
                this.apiKey = prefs.getString(LoginActivity.KEY_API, "");
                String password = prefs.getString(LoginActivity.KEY_PASSWORD, "");
                props.setProperty(ModelManager.ATTR_LOGIN_USER, this.username);
                props.setProperty(ModelManager.ATTR_LOGIN_PASS, password);
            }
        }

        new Thread(() -> {
            try {
                modelManager = new ModelManager(props);
                runOnUiThread(this::getArticles);
            } catch (AuthenticationError authenticationError) {
                authenticationError.printStackTrace();
                try {
                    props.setProperty(ModelManager.ATTR_LOGIN_USER, null);
                    props.setProperty(ModelManager.ATTR_LOGIN_PASS, null);
                    modelManager = new ModelManager(props);
                } catch (AuthenticationError error) {
                    error.printStackTrace();
                }
            }

            runOnUiThread(this::updateLabelsRegardingLoginStatus);
        }).start();


        adapter = new ArticleAdapter(this);
        ((ListView) findViewById(R.id.articleListView)).setAdapter(adapter);
    }

    public void getArticles() {
        if (modelManager != null) {
            adapter.setData(new ArrayList<>());
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            GetArticleTask task = new GetArticleTask(this);
            new Thread(task).start();
        }
    }

    public void receiveData(List<Article> data) {
        this.data = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void routeToArticle(Article article) throws ServerCommunicationError {
        //Intent intent = new Intent(getApplicationContext(), EditArticleActivity.class);
        Intent intent = new Intent(getApplicationContext(), ShowArticleActivity.class);
        intent.putExtra(PARAM_ARTICLE, article.getId());
        startActivity(intent);
    }


    // LOGIN FUNCTIONALITY

    public void login(View view) {
        if (!loggedIn) {
            // allow user to log in
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            // log out user
            loggedIn = false;
            updateLabelsRegardingLoginStatus();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = prefs.edit();
            ed.remove(LoginActivity.KEY_USERNAME);
            ed.remove(LoginActivity.KEY_PASSWORD);
            ed.remove(LoginActivity.KEY_API);
            ed.putBoolean(LoginActivity.KEY_BOOLEAN, false);
            ed.apply();

            modelManager.logout();
        }
        updateLabelsRegardingLoginStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLabelsRegardingLoginStatus();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelsRegardingLoginStatus() {
        if (loggedIn) {
            // change button label
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setText("Log out");
            // change status
            TextView loginStatus = findViewById(R.id.loginStatus);
            loginStatus.setText("You are logged");
        } else {
            // change button label
            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setText("Log in");
            // change status
            TextView loginStatus = findViewById(R.id.loginStatus);
            loginStatus.setText("You are not logged");
        }

        getArticles();
    }
}