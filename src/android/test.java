package com.example.javatestapp2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.javatestapp2.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<SubscriptionInfo> subInfoList;
    public static ArrayList<String> Numbers;
    private SubscriptionManager mSubscriptionManager;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView textView = (TextView) findViewById(R.id.textview_first);

        // 권한이 허용되어있지않다면 권한요청
        if (!hasPermissions(this, PERMISSIONS)) {
            getPermission(PERMISSIONS);
            textView.setText("Please re run");
        }else {
            Numbers = new ArrayList<String>();
            mSubscriptionManager = SubscriptionManager.from(this);
            GetCarriorsInformation();
            textView.setText(Numbers.toString());
        }

    }

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_PHONE_NUMBERS
    };

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void getPermission(String... permissions){
        ActivityCompat.requestPermissions(this,
                permissions,1000);
    }
    private void GetCarriorsInformation() {
        subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
        Log.d("Test",subInfoList.toString());
        int subId;
        // https://developer.android.com/reference/android/telephony/SubscriptionInfo#getNumber()
        // This method was deprecated in API level 33.
        // use SubscriptionManager#getPhoneNumber(int) instead.
        for (SubscriptionInfo subscriptionInfo : subInfoList) {
            subId = subscriptionInfo.getSubscriptionId();
            Log.d("Test","SubscriptionInfo:"+subId);
            Log.d("Test","SubscriptionInfo:"+subscriptionInfo.getNumber());
            Numbers.add("SubscriptionInfo/"+subId+"/"+subscriptionInfo.getNumber()+"\n");
        }
        // https://developer.android.com/reference/android/telephony/TelephonyManager#getLine1Number()
        // This method was deprecated in API level 33.
        // use SubscriptionManager#getPhoneNumber(int) instead.
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String myNumber = mgr.getLine1Number();
        Log.d("Test","getLine1Number:"+myNumber);
        Numbers.add("getLine1Number/"+myNumber+"\n");

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            // https://developer.android.com/reference/android/telephony/SubscriptionManager#getPhoneNumber(int)
            for (SubscriptionInfo subscriptionInfo : subInfoList) {
                subId = subscriptionInfo.getSubscriptionId();
                Log.d("Test", "SubscriptionManager:" + subId);
                Log.d("Test", "SubscriptionManager:" + mSubscriptionManager.getPhoneNumber(subId));
                Numbers.add("SubscriptionManager/" + subId + "/" + mSubscriptionManager.getPhoneNumber(subId) + "\n");
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
