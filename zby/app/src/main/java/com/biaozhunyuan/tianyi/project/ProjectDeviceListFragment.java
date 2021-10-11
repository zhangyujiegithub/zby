package com.biaozhunyuan.tianyi.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.helper.WebviewNormalActivity;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.utils.StrUtils;
import com.biaozhunyuan.tianyi.common.view.PullToRefreshAndLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

import static com.biaozhunyuan.tianyi.helper.WebviewNormalActivity.EXTRA_TITLE;
import static com.biaozhunyuan.tianyi.helper.WebviewNormalActivity.EXTRA_URL;
import static com.biaozhunyuan.tianyi.project.ProjectInfoActivity.PRSTR;

/**
 * 项目详情: 设备列表
 */

public class ProjectDeviceListFragment extends Fragment{


    private PullToRefreshAndLoadMoreListView lv;
    private Demand<Project> demand;
    private int pageIndex = 1;
    private CommanAdapter<Project> adapter;
    private Project mProject;
    private DictionaryHelper helper;
    private Context mContext;
    public static boolean isReasume = false;
    private TextView nullData;
    private List<Project> projectList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_project_info, null);
        getIntentData();
        initView(v);
        initData();
        getList();
        setOnTouch();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isReasume) {
            projectList.clear();
            pageIndex = 1;
            getList();
        }
    }

    private void setOnTouch() {
        lv.setOnLoadMore(new PullToRefreshAndLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getList();
            }
        });
        lv.setOnRefreshListener(new PullToRefreshAndLoadMoreListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                projectList.clear();
                getList();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Project item = adapter.getItem(position - 1);
                    Intent intent = new Intent(mContext, WebviewNormalActivity.class);
                    String url = "http://crm.tysoft.com/" + GlobalMethord.项目设备列表详情+ item.getUuid() + "&projectId=" + mProject.getUuid();
                    intent.putExtra(EXTRA_URL,url);
                    intent.putExtra(EXTRA_TITLE,"设备详情");
                    startActivity(intent);
                }
            }
        });
    }

    private void getIntentData() {
        if (getActivity().getIntent().getSerializableExtra("Project") != null) {
            mProject = (Project) getActivity().getIntent().getSerializableExtra("Project");
        }
    }

    private void initData() {
        mContext = getActivity();
        helper = new DictionaryHelper(mContext);

        String url = Global.BASE_JAVA_URL + GlobalMethord.项目设备列表 + mProject.getUuid();
        demand = new Demand<>(Project.class);
        demand.pageSize = 10;
        demand.sortField = "turnOnDate desc";
        demand.src = url;
    }

    private void getList() {
        demand.pageIndex = pageIndex;
        demand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                List<Project> data = demand.data;
                if(data.size()>0){
                    for(Project p : data){
                        projectList.add(p);
                    }

                    lv.onRefreshComplete();
                    if (pageIndex == 1) {
                        adapter = getAdapter(data);
                        lv.setAdapter(adapter);
                    } else {
                        adapter.addBottom(data, false);
                        lv.loadCompleted();
                    }
                    pageIndex += 1;
                    showOrHiddenList(true);
                }else {
                    if(projectList.size()>0){
                        showOrHiddenList(true);
                    }else {
                        showOrHiddenList(false);
                    }
                }

            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {

            }
        });

    }


    private void showOrHiddenList(boolean isShowList){
        lv.loadCompleted();
        if(isShowList){
            nullData.setVisibility(View.GONE);
        }else {
            nullData.setVisibility(View.VISIBLE);
        }
    }

    private void initView(View v) {
        lv = v.findViewById(R.id.lv);
        nullData = v.findViewById(R.id.tv_null_data);
        nullData.setText(PRSTR + "设备");
    }


    private CommanAdapter<Project> getAdapter(List<Project> gridItems) {
        return new CommanAdapter<Project>(gridItems, getActivity(), R.layout.item_project_device_list) {
            public void convert(int position, final Project item, BoeryunViewHolder viewHolder) {
                viewHolder.setTextValue(R.id.tv_name_contact_item,item.getProductCode()); //设备序列号
                viewHolder.setTextValue(R.id.tv_status_item_contact,
                        "设备型号: " + StrUtils.pareseNull(item.getProductModel())); //设备型号
                viewHolder.setTextValue(R.id.tv_time_contact_item,
                        ViewHelper.convertStrToFormatDateStr(item.getTurnOnDate(),"yyyy-MM-dd")); //开机时间
                TextView remark = viewHolder.getView(R.id.tv_remark);
                if(!TextUtils.isEmpty(item.getRemark())){
                    remark.setVisibility(View.VISIBLE);
                    viewHolder.setTextValue(R.id.tv_remark,item.getRemark()); //备注
                } else {
                    remark.setVisibility(View.GONE);
                }
            }
        };
    }

}
