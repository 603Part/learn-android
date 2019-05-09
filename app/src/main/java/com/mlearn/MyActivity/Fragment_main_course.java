package com.mlearn.MyActivity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_main_course extends Fragment {
    private static final String TAG = "Fragment_main_course";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<Fragment> fragmentList;
    private MyAdapter adapter;
    private String[] titles = {"我的课程", "所有课程"};

    public Fragment_main_course() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_main_course, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tabLayout = view.findViewById(R.id.course_tab);
        viewPager = view.findViewById(R.id.course_view_pager);
        fragmentList = new ArrayList<>();

        Fragment fragment_my_course = new Fragment_my_course();
        Fragment fragment_all_course = new Fragment_all_course();
        fragmentList.add(fragment_my_course);
        fragmentList.add(fragment_all_course);

        adapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void refreshPage() {
        ((Fragment_my_course) fragmentList.get(0)).refreshPage();
        ((Fragment_all_course) fragmentList.get(1)).refreshPage();
    }

    /*********************************************************************************************/
    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
