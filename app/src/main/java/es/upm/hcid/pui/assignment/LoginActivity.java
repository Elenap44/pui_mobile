package es.upm.hcid.pui.assignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity {


    private String userID;
    public static String KEY_USERNAME= "usernameKey";
    public static String KEY_PASSWORD = "passwordKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userID = "";
    }

    public void loginAttempt(View view) {
        EditText usernameField = findViewById(R.id.user_textEdit);
        EditText passwordField = findViewById(R.id.password_textEdit);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        Thread thread = new Thread(() -> {
            try {
                MainActivity.modelManager.login(username, password);

                MainActivity.loggedIn = true;

                this.userID = MainActivity.modelManager.getIdUser();
                Properties prop = new Properties();
                prop.setProperty(ModelManager.ATTR_LOGIN_USER, "DEV_TEAM_01");
                prop.setProperty(ModelManager.ATTR_LOGIN_PASS, "123401");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(KEY_USERNAME, username);
                ed.putString(KEY_PASSWORD, password);
                ed.apply();

                runOnUiThread(this::finish);
            } catch (AuthenticationError authenticationError) {
                authenticationError.printStackTrace();
                Toast.makeText(this, "Credentials are not correct!",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        thread.start();
    }


}