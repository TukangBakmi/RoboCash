package com.albertukrida.a412020031_projectuas;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAbout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAbout extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentAbout() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAbout.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAbout newInstance(String param1, String param2) {
        FragmentAbout fragment = new FragmentAbout();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    AnimationDrawable idleAnimation1, idleAnimation2, idleAnimation3;
    Boolean gear1HasFall = false, gear2HasFall = false, gear3HasFall = false;
    ImageView gear1, gear2, gear3, ground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, null);

        ground = root.findViewById(R.id.ground);
        gear1 = root.findViewById(R.id.gear1);
        gear2 = root.findViewById(R.id.gear2);
        gear3 = root.findViewById(R.id.gear3);

        // Gear animation
        gear1.setImageResource(R.drawable.animation_gear_green);
        idleAnimation1 = (AnimationDrawable)gear1.getDrawable();
        idleAnimation1.start();

        gear2.setImageResource(R.drawable.animation_gear_reverse);
        idleAnimation2 = (AnimationDrawable)gear2.getDrawable();
        idleAnimation2.start();

        gear3.setImageResource(R.drawable.animation_gear_green);
        idleAnimation3 = (AnimationDrawable)gear3.getDrawable();
        idleAnimation3.start();

        gear1.setOnClickListener(this::GearFallAnimation);
        gear2.setOnClickListener(this::GearFallAnimation);
        gear3.setOnClickListener(this::GearFallAnimation);

        // Inflate the layout for this fragment
        return root;
    }

    public void GearFallAnimation(View view){
        if(!gear1HasFall){
            float newY = ground.getY() - ground.getHeight() - gear1.getHeight();
            ViewPropertyAnimator animator = gear1.animate()
                    .translationY(newY)
                    .setInterpolator(new AccelerateInterpolator())
                    .setInterpolator(new BounceInterpolator())
                    .setDuration(2000);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) gear1.getLayoutParams();
            params.topMargin = 0;
            gear1.setLayoutParams(params);
            idleAnimation1.stop();
            animator.start();
            gear1HasFall = true;
        }else if(!gear2HasFall){
            float newY = ground.getY() - ground.getHeight() - gear2.getHeight();
            ViewPropertyAnimator animator = gear2.animate()
                    .translationY(newY)
                    .setInterpolator(new AccelerateInterpolator())
                    .setInterpolator(new BounceInterpolator())
                    .setDuration(2000);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) gear2.getLayoutParams();
            params.topMargin = 0;
            gear2.setLayoutParams(params);
            idleAnimation2.stop();
            animator.start();
            gear2HasFall = true;
        }else if(!gear3HasFall){
            float newY = ground.getY() - ground.getHeight() - gear2.getHeight();
            ViewPropertyAnimator animator = gear3.animate()
                    .translationY(newY)
                    .setInterpolator(new AccelerateInterpolator())
                    .setInterpolator(new BounceInterpolator())
                    .setDuration(2000);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) gear3.getLayoutParams();
            params.topMargin = 0;
            gear3.setLayoutParams(params);
            idleAnimation3.stop();
            animator.start();
            gear3HasFall = true;
        }
    }
}