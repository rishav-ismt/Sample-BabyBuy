package np.com.yourname.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import np.com.yourname.babybuy.R;

public class DashboardActivity extends AppCompatActivity {
    public static String KEY_USER_EMAIL = "user_email";
    public static String KEY_USER_PASSWORD = "user_password";
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private PurchasedFragment purchasedFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        homeFragment = HomeFragment.newInstance();
        purchasedFragment = PurchasedFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(
                    @NonNull MenuItem item
            ) {
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragmentInContainer(homeFragment);
                        break;

                    case R.id.purchased:
                        loadFragmentInContainer(purchasedFragment);
                        break;

                    case R.id.profile:
                        loadFragmentInContainer(profileFragment);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });




        //Reading data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(
                "login_pref",
                MODE_PRIVATE
        );
        String defaultEmail = "";
        String defaultPassword = "";
        String email = sharedPreferences.getString("email_data", defaultEmail);
        String password = sharedPreferences.getString(
                "password_data",
                defaultPassword
        );
        Toast.makeText(this,
                "Email ::: " + email + "\nPassword ::: " + password
                ,
                Toast.LENGTH_SHORT
        ).show();







//        Intent intent = getIntent();
//        String email = intent.getStringExtra(KEY_USER_EMAIL);
//        String password = intent.getStringExtra(KEY_USER_PASSWORD);

//        UserCredentials userCredentials = (UserCredentials) intent.getSerializableExtra("user_credentials");
//        String userData = "Email ::: " +
//                userCredentials.getUserEmail() +
//                ", Password ::: " +
//                userCredentials.getUserPassword();
//        Toast.makeText(
//                DashboardActivity.this,
//                userData,
//                Toast.LENGTH_LONG
//        ).show();
    }

    /*
     * Loading Fragments in container
     */
    private void loadFragmentInContainer(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        fragmentTransaction.commit();
    }
}