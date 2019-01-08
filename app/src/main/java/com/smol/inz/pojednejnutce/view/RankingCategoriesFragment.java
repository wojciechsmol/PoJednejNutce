package com.smol.inz.pojednejnutce.view;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smol.inz.pojednejnutce.R;
import com.smol.inz.pojednejnutce.utils.FragmentChangeListener;

public class RankingCategoriesFragment extends Fragment {

    View myFragment;
    private Button mPopButton;
    private Button mRockButton;

    public static RankingCategoriesFragment newInstance() {
        RankingCategoriesFragment rankingCategoriesFragment = new RankingCategoriesFragment();
        return rankingCategoriesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking_categories, container, false);
        initialize();
        return myFragment;
    }

    private void initialize() {
        mPopButton = myFragment.findViewById(R.id.button_pop_category);
        mPopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = RankingSpecificCategoryFragment.newInstance();
                Bundle args = new Bundle();
                args.putString("Category", "POP");
                fr.setArguments(args);
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(fr);
            }
        });

        mRockButton = myFragment.findViewById(R.id.button_rock_category);
        mRockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = RankingSpecificCategoryFragment.newInstance();
                Bundle args = new Bundle();
                args.putString("Category", "ROCK");
                fr.setArguments(args);
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(fr);
            }
        });
    }

}
