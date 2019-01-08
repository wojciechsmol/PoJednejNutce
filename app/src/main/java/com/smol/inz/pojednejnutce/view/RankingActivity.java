package com.smol.inz.pojednejnutce.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.utils.Category;
import com.smol.inz.pojednejnutce.utils.FragmentChangeListener;

public class RankingActivity extends AppCompatActivity implements FragmentChangeListener {

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 Fragment selectedFragment = null;

                switch(item.getItemId()) {

                    case R.id.action_ranking_overall:
                        selectedFragment = RankingOverallFragment.newInstance();
                        break;
                    case R.id.action_ranking_categories:
                        selectedFragment = RankingCategoriesFragment.newInstance();
                        break;
                    case R.id.action_ranking_songs:
                        selectedFragment = RankingSongsFragment.newInstance();
                        break;

                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, RankingOverallFragment.newInstance());
        transaction.commit();
    }


    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }


}
