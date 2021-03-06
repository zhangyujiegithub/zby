package com.biaozhunyuan.tianyi.newuihome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.apply.FormInfoActivity;
import com.biaozhunyuan.tianyi.apply.model.Backlog;
import com.biaozhunyuan.tianyi.base.LazyFragment;
import com.biaozhunyuan.tianyi.cnis.model.AppliedSeal;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.utils.ToastUtils;
import com.biaozhunyuan.tianyi.common.view.AvatarImageView;
import com.biaozhunyuan.tianyi.common.view.NoScrollListView;
import com.biaozhunyuan.tianyi.models.OldBacklog;
import com.biaozhunyuan.tianyi.view.AutoMaxHeightViewpager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 原系统待办
 *
 * @author GaoB
 * @description:
 * @date : 2020/11/19 9:25
 */


@SuppressLint("ValidFragment")
public class OldBacklogListFragment extends LazyFragment {


    private Context context;
    private DictionaryHelper dictionaryHelper;
    private int pageIndex = 1; //当前页
    private Demand demand = new Demand();//分页需上传实体类
    private ArrayList<String> datas = new ArrayList();
    private CommanAdapter<AppliedSeal> adapter;
    private List<AppliedSeal> list;

    private NoScrollListView lv;
    private RelativeLayout rl_nodata;
    private int fragmentID = 2;
    private AutoMaxHeightViewpager vp;
    private View v;
    public static boolean isResume = false;

    private boolean isFirst = true;


    public OldBacklogListFragment(AutoMaxHeightViewpager vp, int fragmentID) {
        this.vp = vp;
        this.fragmentID = fragmentID;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_partysubject_list_new, null);
        initView(v);
        initData();
        initDemand();
        vp.setObjectForPosition(v, fragmentID);
        ProgressDialogHelper.show(getActivity());
        getOldBacklogList(null);
        setOnEvent();
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (isResume) {
            pageIndex = 1;
            ProgressDialogHelper.show(getActivity());
            getOldBacklogList(null);
            isResume = false;
        }
    }


    private void initView(View v) {
        lv = v.findViewById(R.id.lv);
        rl_nodata = v.findViewById(R.id.rl_nodata);
    }


    private void setOnEvent() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppliedSeal template = adapter.getDataList().get(position);
                Intent intent = new Intent(context, FormInfoActivity.class);
                intent.putExtra("formName", template.getFormName());
                intent.putExtra("formDataId", template.getFormDataId());
                intent.putExtra("createrId", template.getWorkflowTemplate());
                intent.putExtra("workflowTemplateId", template.getWorkflowTemplate());
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            vp.resetHeight(fragmentID);
        }
    }


    private void initData() {
        context = getActivity();
        dictionaryHelper = new DictionaryHelper(context);
    }

    private void initDemand() {

        String url = Global.BASE_JAVA_URL + GlobalMethord.标准院已办列表;
        demand.pageSize = 10;
        demand.sort = "desc";
        demand.sortField = "processTime";
        demand.dictionaryNames = "creatorId.base_staff,prevStepAuditorId.base_staff";
        Map<String, String> map = new HashMap<>();
        map.put("category", Global.CATEGORY);
        map.put("templateIds", "dbfdf689c48c41e5ab8bc6db2ed4bdd8,e79613f127aa42cdbaa4b38d0d5d6c72");
//        map.put("fields", "标题");
        demand.keyMap = map;
        demand.src = url;

    }


    public void loadMoreData(RefreshLayout refreshLayout) {
        getOldBacklogList(refreshLayout);
    }

    public void refreshData(RefreshLayout refreshLayout) {
        pageIndex = 1;
        getOldBacklogList(refreshLayout);
    }


    private void getOldBacklogList(final RefreshLayout refreshLayout) {

        demand.pageIndex = pageIndex;
        demand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    list = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(JsonUtils.pareseData(response), "data"), AppliedSeal.class);
                    if (list != null) {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }

                        for (AppliedSeal backlog : list) {
                            backlog.setPrevStepAuditorName(demand.getDictName(backlog, "prevStepAuditorId"));
                            backlog.setCreatorName(demand.getDictName(backlog, "creatorId"));
                        }

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
                                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                            }
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadMore();
                            }
                        }
                        pageIndex += 1;
                        if (list.size() > 0) {
                            if (!isFirst) {
                                vp.resetHeight(fragmentID);
                            } else {
                                isFirst = false;
                            }
                        }
                    } else {
                        if (adapter != null) {
                            adapter.clearData();
                        }
                    }
                    ProgressDialogHelper.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (adapter != null) {
                        adapter.clearData();
                    }
                    ProgressDialogHelper.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (adapter != null) {
                        adapter.clearData();
                    }
                    ProgressDialogHelper.dismiss();
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                if (adapter != null) {
                    adapter.clearData();
                }
            }
        });
    }


    private CommanAdapter<AppliedSeal> getAdapter(final List<AppliedSeal> list) {
        return new CommanAdapter<AppliedSeal>(list, context, R.layout.item_backlog_list) {
            @Override
            public void convert(int position, final AppliedSeal item, BoeryunViewHolder viewHolder) {
                if (item != null) {

                    String name = item.getCreatorName();
//                    dictionaryHelper.getUserNameById(item.getCreatorId());
                    viewHolder.setTextValue(R.id.tv_creator_backlog_item, name); //创建人

                    viewHolder.setTextValue(R.id.tv_item_backlog_type, item.getCode()); //类型

                    viewHolder.setTextValue(R.id.tv_doctyep_backlog_item, item.get文号()); //文号
                    viewHolder.getView(R.id.tv_doctyep_backlog_item).setVisibility(View.GONE);

                    viewHolder.setTextValue(R.id.tv_creatTime_backlog_item, item.getProcessTime()); //接收时间

                    viewHolder.setTextValue(R.id.tv_prevAuditor_backlog_item, item.getPrevStepAuditorName()); //上一办理人

                    viewHolder.setTextValue(R.id.content_backlog_list, item.get标题()); //标题

                    viewHolder.getView(R.id.tv_status_item_backlog).setVisibility(View.GONE);

//                    viewHolder.setTextValue(R.id.tv_status_item_backlog, item.getCurrentState()); //当前节点


                    if (TextUtils.isEmpty(item.get文号())) {
                        viewHolder.getView(R.id.tv_doctyep_backlog_item).setVisibility(View.GONE);//文号
                    }


                    ((AvatarImageView) viewHolder.getView(R.id.head_item_backlog_list))
                            .setTextAndColor(name, Color.parseColor("#3366CC"));

//                    ImageUtils.displyUserPhotoById(context, item.getCreatorId(),
//                            viewHolder.getView(R.id.head_item_backlog_list), Color.parseColor("#3366CC"));
                }
            }
        };
    }


}
