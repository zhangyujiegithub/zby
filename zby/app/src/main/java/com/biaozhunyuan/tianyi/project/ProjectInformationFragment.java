package com.biaozhunyuan.tianyi.project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.base.LazyFragment;
import com.biaozhunyuan.tianyi.common.helper.Logger;
import com.biaozhunyuan.tianyi.common.base.ParseException;
import com.biaozhunyuan.tianyi.client.ChClientTabFragment;
import com.biaozhunyuan.tianyi.client.动态表单ViewModel;
import com.biaozhunyuan.tianyi.client.动态表单分类;
import com.biaozhunyuan.tianyi.common.model.form.表单字段;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.view.IndicatorTabView;
import com.biaozhunyuan.tianyi.widget.BoeryunViewpager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 项目信息
 */

public class ProjectInformationFragment extends LazyFragment {

    private Project mProject;
    private HashMap<String, ArrayList<表单字段>> mFormDataMap;
    public List<ChClientTabFragment> mFragments;
    private IndicatorTabView simpleindicator;
    private Context mContext;
    //    private ChClientBiz mClientBiz;

    private BoeryunViewpager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_project_information, null);
        getIntentData();
        initView(v);
        initData();
        getList();
        return v;
    }


    private void getIntentData() {
        if(getActivity().getIntent().getSerializableExtra("Project")!=null){
            mProject = (Project) getActivity().getIntent().getSerializableExtra("Project");
        }
    }

    private void initData() {
        mContext = getActivity();
        mFragments = new ArrayList<ChClientTabFragment>();
        mFormDataMap = new HashMap<String, ArrayList<表单字段>>();
    }

    private void getList() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.CRM动态字段;

        JSONObject object = new JSONObject();
        try {
            object.put("type", "crm_project");
            object.put("id", mProject.getUuid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest.postAsyn(url, object, new StringResponseCallBack() {
            @Override
            public void onResponse(String result) {
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponseCodeErro(String response) {
                ProgressDialogHelper.dismiss();
                动态表单ViewModel formViewModel = null;
                try {
                    formViewModel = JsonUtils.jsonToEntity(
                            response, 动态表单ViewModel.class);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (formViewModel != null) {
                    List<String> titles = new ArrayList<String>();
                    for (动态表单分类 categray : formViewModel.动态表单分类s) {
                        titles.add(categray.分类名称);
                        Logger.i("CategrayTag::" + categray.分类名称 + "");
                        mFormDataMap.put(categray.分类名称, new ArrayList<表单字段>());
                    }
                    if (formViewModel.表单字段s != null
                            && formViewModel.表单字段s.size() > 0) {
                        // 根据分类Tab
                        for (表单字段 form : formViewModel.表单字段s) {
                            if (form.Name.equals("uuid")) {
                                form.TypeName = "主要信息";
                                mFormDataMap.put(form.TypeName,
                                        new ArrayList<表单字段>());
                                mFormDataMap.get(form.TypeName).add(form);
                            }
                        }
                    }

                    if (formViewModel.表单字段s != null
                            && formViewModel.表单字段s.size() > 0) {
                        // 根据分类Tab
                        for (表单字段 form : formViewModel.表单字段s) {
                            if (!mFormDataMap.containsKey(form.TypeName)) {
                                mFormDataMap.put(form.TypeName,
                                        new ArrayList<表单字段>());
                            }
//							if(!"编号".equals(form.Name))
//							{   //完成分类，编号字段不显示
//								mFormDataMap.get(form.TypeName).add(form);
//							}
                            if (!form.Name.equals("uuid")) {
                                mFormDataMap.get(form.TypeName).add(form);
                            }
                        }


                        表单字段 tempIdForm = null;
                        String firstKey = "";
                        Iterator<Map.Entry<String, ArrayList<表单字段>>> item = mFormDataMap.entrySet().iterator();
                        while (item.hasNext()) {
                            Map.Entry<String, ArrayList<表单字段>> entry = item.next();
                            List<表单字段> formList = entry.getValue();
                            String formName = entry.getKey();

                            //特殊处理，当分类下没有内容 或只有一个编号字段则去除tab
                            if (formList == null || formList.size() == 0) {
//                                ||
                                titles.remove(formName);
                            } else if ((formList.size() == 1 && "编号".equals(formList.get(0).Name))) {
                                titles.remove(formName);
                                tempIdForm = formList.get(0);
                            }

                            if (TextUtils.isEmpty(firstKey)) {
                                firstKey = formName;
                            }
                        }


                        if (tempIdForm != null) {
                            //特殊处理id字段到第一分类
                            mFormDataMap.get(firstKey).add(tempIdForm);
                        }
                        for (int i = 0; i < titles.size(); i++) {
                            String s = titles.get(i);
                            if (TextUtils.isEmpty(s)) {
//                                titles.set(titles.indexOf(s), "其他");
                                titles.remove(i);
                                titles.add("其他");
                            }
                        }

//                        mIndicator.setTabItemTitles(titles);

                        for (String title : titles) {
                            if (title.equals("其他")) {
                                title = "";
                            }
                            Iterator<Map.Entry<String, ArrayList<表单字段>>> it = mFormDataMap
                                    .entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<String, ArrayList<表单字段>> entry = it
                                        .next();
                                String keyStr = entry.getKey();
                                Logger.i("EQU" + keyStr + "---" + title);
                                if (keyStr.equals(title)) {
                                    ArrayList<表单字段> formList = entry.getValue();
                                    ChClientTabFragment fragment = ChClientTabFragment
                                            .newInstance(formList,true,"crm_project",mProject.getUuid());
                                    mFragments.add(fragment);
                                }
                            }
                        }

                        for (int i = 0; i < titles.size(); i++) {
                            String s = titles.get(i);
                            if ("其他".equals(s)) {
//                                titles.set(titles.indexOf(s), "其他");
                                titles.remove(s);
                            }
                        }
//                        mIndicator.setTabItemTitles(titles);
                        simpleindicator.setTabItemTitles(titles);
                        if (titles == null || titles.size() == 0 || (titles.size() == 1 && TextUtils.isEmpty(titles.get(0)))) {
//                            mIndicator.setVisibility(View.GONE);
                            simpleindicator.setVisibility(View.GONE);
                        }
                        mViewPager.setOffscreenPageLimit(mFragments.size());
                        mViewPager.setAdapter(new FragmentPagerAdapter(
                                getActivity().getSupportFragmentManager()) {

                            @Override
                            public int getCount() {
                                // TODO Auto-generated method stub
                                return mFragments.size();
                            }

                            @Override
                            public Fragment getItem(int position) {
                                return mFragments.get(position);
                            }
                        });
                        mViewPager.setEnabled(true);
                        simpleindicator.setRelateViewPager(mViewPager);

                    } else {
//                        mIndicator.setVisibility(View.GONE);
                        simpleindicator.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
            }
        });


    }

    private void initView(View v) {
        simpleindicator = v.findViewById(R.id.simpleindicator_ch_client_info);
        mViewPager =  v.findViewById(R.id.vp_ch_client_info);
    }

}
