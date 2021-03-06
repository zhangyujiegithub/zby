package com.biaozhunyuan.tianyi.cnis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.cnis.inforfragment.InformationFragment;
import com.biaozhunyuan.tianyi.cnis.inforfragment.NoticeFragment;
import com.biaozhunyuan.tianyi.common.base.BaseActivity;
import com.biaozhunyuan.tianyi.common.view.BoeryunHeaderView;
import com.biaozhunyuan.tianyi.view.SimpleIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知公告
 * @author GaoB
 * @description:
 * @date : 2020/11/23 9:41
 */
public class InformationActivity extends BaseActivity {

    private BoeryunHeaderView headerView;
    private SimpleIndicator indicator;
    private ViewPager pager;
    private String[] titles = new String[]{"通知", "公告"};
    private List<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initView();
        setEvent();
    }


    private void initView() {
        indicator = findViewById(R.id.incator_title_information_list);
        headerView = findViewById(R.id.header_information_list);
        pager = findViewById(R.id.pager_information_list);
        initFragment();
    }


    private void initFragment() {

        indicator.setTabItemTitles(titles);
        fragments = new ArrayList<>();
        NoticeFragment noticeFragment = new NoticeFragment();
        InformationFragment informationFragment =  new InformationFragment();
        fragments.add(noticeFragment);
        fragments.add(informationFragment);

        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Fragment fragment = ((Fragment) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == ((Fragment) obj).getView();
            }
        });
        indicator.setViewPager(pager, 0);

        pager.setOffscreenPageLimit(0);
    }


    private void setEvent() {

        headerView.setOnButtonClickListener(new BoeryunHeaderView.OnButtonClickListener() {
            @Override
            public void onClickBack() {
                finish();
            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickSaveOrAdd() {

            }
        });

    }


}
