package com.example.brkic.assecoassignment.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.brkic.assecoassignment.R;
import com.example.brkic.assecoassignment.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment();
    }

    private void loadFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, MainFragment.newInstance()).commit();
    }
}
