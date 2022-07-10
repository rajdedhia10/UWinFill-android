package com.uwindsor.uwinfill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this,R.id.navHostFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mapsFragment, R.id.statsFragment
        ).build();
    }
}