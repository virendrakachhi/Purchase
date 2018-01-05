package com.hashmybag.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.beans.InboxBean;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for showing all streams for HMB user *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

public class TabScreen extends Fragment {

    public static TextView chatFrag, totalcount, streamFrag, storeFrag;
    TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int temp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout, container, false);
        viewPager = new ViewPager(getActivity());
        tabLayout(view);
        return view;
    }

    /*ViewPager arrangement*/

    public void tabLayout(View view) {

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        View chattab = LayoutInflater.from(getContext()).inflate(R.layout.chattab, null);
        View storetab = LayoutInflater.from(getContext()).inflate(R.layout.storetab, null);
        View streamtab = LayoutInflater.from(getContext()).inflate(R.layout.streamtab, null);
        chatFrag = (TextView) chattab.findViewById(R.id.chatFrag);
        streamFrag = (TextView) streamtab.findViewById(R.id.streamFrag);
        storeFrag = (TextView) storetab.findViewById(R.id.storeFrag);
        totalcount = (TextView) chattab.findViewById(R.id.totalcount);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setCustomView(storetab);
        tabLayout.getTabAt(1).setCustomView(streamtab);
        tabLayout.getTabAt(2).setCustomView(chattab);

        /*Pager listener to listen o page change and scrolling*/

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    storeFrag.setTextColor(getResources().getColor(R.color.colorBagRed));
                    streamFrag.setTextColor(Color.BLACK);
                    chatFrag.setTextColor(Color.BLACK);
                } else if (position == 1) {
                    streamFrag.setTextColor(getResources().getColor(R.color.colorBagRed));
                    storeFrag.setTextColor(Color.BLACK);
                    chatFrag.setTextColor(Color.BLACK);
                } else if (position == 2) {
                    chatFrag.setTextColor(getResources().getColor(R.color.colorBagRed));
                    storeFrag.setTextColor(Color.BLACK);
                    streamFrag.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * This method is used to setting up ViewPager
     */
    public void setCount(Context activity) {
        int num = getCountSms(activity);
        if (num > 0) {
            totalcount.setText(String.valueOf(num));
            totalcount.setVisibility(View.VISIBLE);
        } else {
            totalcount.setVisibility(View.GONE);
        }
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new StoresFragment(), "STORES");
        adapter.addFragment(new StreamFragment(), "STREAMS");
        adapter.addFragment(new ChatsFragment(), "CHATS");
        viewPager.setAdapter(adapter);
    }

    public int getCountSms(Context context) {
        int count = 0;
        // List<InboxBean> inboxBeanList = new DatabaseHandler(context).getInbox();
        List<InboxBean> inboxBeanList = ChatsFragment.inboxList;
        for (int i = 0; i < inboxBeanList.size(); i++) {
            if (inboxBeanList.get(i).getSmsCount() > 0) {
                count++;
            }
        }
        return count;
    }

    private void setTabOpen() {

        /*Intent intent = getActivity().getIntent();
        String from = intent.getStringExtra("from");
        String action = intent.getAction();
        Uri data = intent.getData();*/
        if (temp < 1) {
            viewPager.setCurrentItem(1);
            temp++;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setCount(getContext());
        setTabOpen();

    }


    /*This inner class is used for setting adapter to the ViewPager*/

    class ViewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}

