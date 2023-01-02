package np.com.yourname.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import np.com.yourname.babybuy.dashboard.DashboardActivity;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.user.User;
import np.com.yourname.babybuy.db.user.UserDao;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private TextView tvDisplay1, tvDisplay2, tvDisplay3;
    private ImageView ivDisplay1;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "onCreate: ");
        tvDisplay1 = findViewById(R.id.tv_display_1);
        tvDisplay2 = findViewById(R.id.tv_display_2);
        tvDisplay3 = findViewById(R.id.tv_display_3);
        ivDisplay1 = findViewById(R.id.iv_display_1);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        tvDisplay1.setText("This is Android...");
        tvDisplay1.setTextColor(getColor(R.color.purple_200));
        tvDisplay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        LoginActivity.this,
                        "Display 1 is clicked",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Email is empty",
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Email is not in correct format",
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(
                            LoginActivity.this,
                            "Password is empty",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Login success",
                            Toast.LENGTH_SHORT
                    ).show();

                    //Writing data to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            "login_pref",
                            Context.MODE_PRIVATE
                    );
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email_data", email);
                    editor.putString("password_data", password);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    //Writing data to SQLiteDatabase
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            insertUserData(email, password);
                        }
                    }).start();



                    Intent intent = new Intent(
                            LoginActivity.this,
                            DashboardActivity.class
                    );
//                    UserCredentials userCredentials = new UserCredentials(email, password);
//                    intent.putExtra("user_credentials", userCredentials);
//                    intent.putExtra(DashboardActivity.KEY_USER_EMAIL, email);
//                    intent.putExtra(DashboardActivity.KEY_USER_PASSWORD, password);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    private void insertUserData(String email, String password) {
        BabyBuyDatabase database = BabyBuyDatabase.getInstance(getApplicationContext());

        UserDao userDao = database.getUserDao();

        User user1 = new User();
        user1.email = email;
        user1.password = password;

        userDao.insertUser(user1);
    }
}