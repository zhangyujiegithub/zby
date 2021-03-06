package com.biaozhunyuan.tianyi.cnis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.apply.FormInfoActivity;
import com.biaozhunyuan.tianyi.client.ClientContactListFragment;
import com.biaozhunyuan.tianyi.cnis.model.YiQingModel;
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
import com.biaozhunyuan.tianyi.common.view.BoeryunHeaderView;
import com.biaozhunyuan.tianyi.common.view.PullToRefreshAndLoadMoreListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 疫情列表
 */
public class YiQingFragment extends Fragment {

    private PullToRefreshAndLoadMoreListView lv;


    private Context context;
    private boolean isAllData = false;
    private int pageIndex = 1;
    private DictionaryHelper dictionaryHelper;
    private RelativeLayout rl_nodata;
    private List<YiQingModel> list;
    private CommanAdapter<YiQingModel> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_shouwen, null);
        context = getActivity();
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
        isAllData = getArguments().getBoolean("isAllData", false);
    }

    public static YiQingFragment newInstance(boolean isAllData) {
        YiQingFragment fragment = new YiQingFragment();
        Bundle args = new Bundle();
        args.putBoolean("isAllData", isAllData);
        fragment.setArguments(args);
        return fragment;
    }

    private void initViews(View view) {
        lv = view.findViewById(R.id.lv_common_shouwen);
        rl_nodata = view.findViewById(R.id.rl_nodata);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Event(String message) {
        if ("表单提交成功！".equals(message)) {
            pageIndex = 1;
            getList();
        }
    }


    private void setOnEvent() {

        lv.setOnRefreshListener(new PullToRefreshAndLoadMoreListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getList();
            }
        });

        lv.setOnLoadMore(new PullToRefreshAndLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getList();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    YiQingModel template = adapter.getDataList().get(position - 1);
                    Intent intent = new Intent(context, FormInfoActivity.class);
                    intent.putExtra("formName", "中国标准化研究院职工疫情防控情况报备表");
                    if (isAllData) {
                        intent.putExtra("formDataId", template.getUuid());
                        intent.putExtra("workflowTemplateId", template.getWorkflowTemplateId());
                    } else {
                        intent.putExtra("formDataId", template.getFormDataId());
                        intent.putExtra("workflowTemplateId", template.getWorkflowTemplate());
                    }
                    startActivity(intent);
                }
            }
        });
    }

    private void getList() {
        String url = "";
        if (isAllData) {
            url = Global.BASE_JAVA_URL + GlobalMethord.疫情报备列表;
        } else {
            url = Global.BASE_JAVA_URL + GlobalMethord.疫情报备已办列表;
        }

        Map<String, String> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "10");
        map.put("sort", "desc");
        map.put("sortField", "createTime");

        StringRequest.postAsyn(url, map, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    list = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(JsonUtils.getStringValue(response, "Data"), "data"), YiQingModel.class);
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


    private CommanAdapter<YiQingModel> getAdapter(List<YiQingModel> list) {
        return new CommanAdapter<YiQingModel>(list, context, R.layout.item_seal_list) {
            @Override
            public void convert(int position, YiQingModel item, BoeryunViewHolder viewHolder) {

                if (item != null) {
                    viewHolder.setTextValue(R.id.tv_creator_seal_item, dictionaryHelper.getUserNameById(item.getCreatorId())); //创建人

//                    viewHolder.setTextValue(R.id.tv_item_seal_type, item.getDocType()); //类型
                    viewHolder.getView(R.id.tv_item_seal_type).setVisibility(View.GONE);


                    viewHolder.setTextValue(R.id.tv_creatTime_seal_text_item, "申请时间"); //申请时间标题
                    viewHolder.setTextValue(R.id.tv_creatTime_seal_item, item.getCreateTime()); //时间

                    viewHolder.getView(R.id.ll_handleTime).setVisibility(View.GONE);//办理时间


//                    viewHolder.setTextValue(R.id.tv_no_seal_item, item.get文号()); //文号

                    viewHolder.getView(R.id.content_seal_list).setVisibility(View.GONE);

                    viewHolder.setTextValue(R.id.tv_status_item_seal, item.getCurrentState()); //当前节点
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
