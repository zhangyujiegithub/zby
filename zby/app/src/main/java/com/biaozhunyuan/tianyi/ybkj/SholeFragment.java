package com.biaozhunyuan.tianyi.ybkj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.apply.FormInfoActivity;
import com.biaozhunyuan.tianyi.cnis.model.AppliedSeal;
import com.biaozhunyuan.tianyi.cnis.model.PlAllBean;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.utils.ImageUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.view.PullToRefreshAndLoadMoreListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class SholeFragment extends Fragment {

    private PullToRefreshAndLoadMoreListView lv;


    private Context context;
    private boolean isAllData = false;
    private String titleName;
    private PlAllBean plAllBean;
    private int pageIndex = 1;
    private DictionaryHelper dictionaryHelper;
    private RelativeLayout rl_nodata;
    private List<AppliedSeal> list;
    private CommanAdapter<AppliedSeal> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_shouwen, null);
        context = getActivity();
        ProgressDialogHelper.show(context);
        dictionaryHelper = new DictionaryHelper(context);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initViews(view);
        getList();
        setOnEvent();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        isAllData = getArguments().getBoolean("isAllData", false);
        String json = getArguments().getString("json", "");
        titleName = getArguments().getString("titleName", "");
        plAllBean = JSON.parseObject(json,PlAllBean.class);
        showWorkFlowId();
    }

    private void showWorkFlowId() {
        if(!TextUtils.isEmpty(titleName)){
            if(titleName.equals(Global.HX[1]+"??????")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("??????????????????");
                plAllBean.setWorkflowTemplateId("e86b272ca37844b3516722fd812481e3");
            }else if(titleName.equals(Global.HX[2]+"??????")) {
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("??????????????????");
                plAllBean.setWorkflowTemplateId("e86b872ca37844b3914325fd89790ioa");
            }else if(titleName.equals(Global.Y[1]+"???")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89papccc");
            }else if(titleName.equals(Global.Y[2]+"???")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89397ew0");
            }else if(titleName.equals(Global.Y[3]+"???")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89245uio");
            }else if(titleName.equals(Global.Y[4]+"???")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89papaac");
            }else if(titleName.equals(Global.Y[5]+"???")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd892481e3");
            }else if(titleName.equals(Global.Y[6]+"???")){
                plAllBean.setType("1");
            }else if(titleName.equals(Global.Y[7]+"???")){
                plAllBean.setType("3");
            }else if(titleName.equals(Global.Y[8]+"???")){
                plAllBean.setType("2");
            }else if(titleName.equals(Global.KYWT[2]+"????????????")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89390qrt");
            }else if(titleName.equals(Global.KYWT[3]+"????????????")){
                plAllBean.setTitle("????????????");
                plAllBean.setFormName("????????????????????????");
                plAllBean.setWorkflowTemplateId("n23p422cd67451b3914325fd89399opc");
            }
        }
    }

    public static SholeFragment newInstance(boolean isAllData,String sb,String titleName) {
        SholeFragment fragment = new SholeFragment();
        Bundle args = new Bundle();
        args.putBoolean("isAllData", isAllData);
        args.putString("json",sb);
        args.putString("titleName",titleName);
        fragment.setArguments(args);
        return fragment;
    }

    private void initViews(View view) {
        lv = view.findViewById(R.id.lv_common_shouwen);
        rl_nodata = view.findViewById(R.id.rl_nodata);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Event(String message) {
        if ("?????????????????????".equals(message)) {
            pageIndex = 1;
            getList();
        }
    }


    private void setOnEvent() {

        lv.setOnRefreshListener(() -> {
            pageIndex = 1;
            getList();
        });

        lv.setOnLoadMore(this::getList);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (position > 0) {
                AppliedSeal template = adapter.getDataList().get(position - 1);
                Intent intent = new Intent(context, FormInfoActivity.class);
                intent.putExtra("formName", plAllBean.getTitle());
                if (isAllData) {
                    intent.putExtra("formDataId", template.getUuid());
                    intent.putExtra("createrId", template.getCreatorId());
                } else {
                    intent.putExtra("formDataId", template.getFormDataId());
                    intent.putExtra("createrId", template.getWorkflowTemplate());
                }
                intent.putExtra("workflowTemplateId", template.getWorkflowTemplate());
                startActivity(intent);
            }
        });
    }

    private void getList() {
        String url ;
        Map<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "10");
        if (isAllData) {
            if(Global.KYYB.equals(titleName)) {
                url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????????;
                map.put("title", plAllBean.getTitle());
                map.put("workflowTemplateId", plAllBean.getWorkflowTemplateId());
                map.put("formName", plAllBean.getTitle());
                map.put("status", plAllBean.getStatus());
                map.put("type", plAllBean.getType());
                map.put("hideStatus", plAllBean.getShowDelete());
                map.put("showCancel", plAllBean.getShowCancel());
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            }else if ((Global.HX[0]+"??????").equals(titleName)) {
                url = Global.BASE_JAVA_URL + GlobalMethord.XMSB;
                map.put("status", plAllBean.getStatus());
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            }else if (Global.CG_XXHCG.equals(titleName)) {
                url = Global.BASE_JAVA_URL + GlobalMethord.URL_XXHCG;
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            }else if ((Global.Y[6]+"???").equals(titleName)
                    || (Global.Y[7]+"???").equals(titleName)
                    || (Global.Y[8]+"???").equals(titleName)) {
                url = Global.BASE_JAVA_URL + GlobalMethord.Y_WTS;
                map.put("type",plAllBean.getType());
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            }else if ((Global.KYWT[1]+"????????????").equals(titleName)) {
                url = Global.BASE_JAVA_URL + GlobalMethord.KY_HTSP;
                map.put("tableName",plAllBean.getTableName());
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            } else {
                url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????????;
                map.put("title", plAllBean.getTitle());
                map.put("workflowTemplateId", plAllBean.getWorkflowTemplateId());
                map.put("formName", plAllBean.getTitle());
                map.put("status", plAllBean.getStatus());
                map.put("showDelete", plAllBean.getShowDelete());
                map.put("showCancel", plAllBean.getShowCancel());
                map.put("jsFileId", plAllBean.getJsFileId());
                map.put("sort", plAllBean.getSort());
                map.put("sortField", plAllBean.getSortField());
            }
        } else {
            url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????????;
            map.put("category", "33fb40edb10948e6a5442dd4141d1607");
            map.put("searchField_string_workflowTemplate", "1|" + plAllBean.getWorkflowTemplateId());
        }

        StringRequest.postAsyn(url, map, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    list = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(JsonUtils.getStringValue(response, "Data"), "data"), AppliedSeal.class);
                    lv.onRefreshComplete();
                    if (list != null) {
                        if (pageIndex == 1) {
                            adapter = getAdapter(list);
                            lv.setAdapter(adapter);
                            if (list.size() > 0) {
                                rl_nodata.setVisibility(View.GONE);
                                lv.setVisibility(View.VISIBLE);
                            } else {
                                rl_nodata.setVisibility(View.VISIBLE);
                                lv.setVisibility(View.GONE);
                            }
                        } else {
                            adapter.addBottom(list, false);
                            if (list != null && list.size() == 0) {
                                lv.loadAllData();
                            }
                            lv.loadCompleted();
                        }
                    }else{
                        rl_nodata.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    }

                    pageIndex += 1;
                } catch (JSONException e) {
                    e.printStackTrace();
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


    private CommanAdapter<AppliedSeal> getAdapter(List<AppliedSeal> list) {
        return new CommanAdapter<AppliedSeal>(list, context, R.layout.item_seal_list) {
            @Override
            public void convert(int position, AppliedSeal item, BoeryunViewHolder viewHolder) {

                if (item != null) {
                    viewHolder.setTextValue(R.id.tv_creator_seal_item, dictionaryHelper.getUserNameById(item.getCreatorId())); //?????????

//                    viewHolder.setTextValue(R.id.tv_item_seal_type, item.getDocType()); //??????
                    viewHolder.getView(R.id.tv_item_seal_type).setVisibility(View.GONE);


                    viewHolder.setTextValue(R.id.tv_creatTime_seal_text_item, "????????????"); //??????????????????
                    viewHolder.setTextValue(R.id.tv_creatTime_seal_item, item.getCreateTime()); //??????

                    viewHolder.getView(R.id.ll_handleTime).setVisibility(View.GONE);//????????????


                    viewHolder.setTextValue(R.id.tv_no_seal_item, item.get??????()); //??????
                    String strTemp = TextUtils.isEmpty(item.get??????())?item.get????????????():item.get??????();
                    viewHolder.setTextValue(R.id.content_seal_list, TextUtils.isEmpty(strTemp)?item.get????????????():strTemp); //??????

                    viewHolder.setTextValue(R.id.tv_status_item_seal, item.getCurrentState()); //????????????

                    if (TextUtils.isEmpty(item.get??????())) {
                        viewHolder.getView(R.id.tv_no_seal_item).setVisibility(View.GONE);//??????
                    }

                    ImageUtils.displyUserPhotoById(context, item.getCreatorId(),
                            viewHolder.getView(R.id.head_item_seal_list), Color.parseColor("#3366CC"));

                }


            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
