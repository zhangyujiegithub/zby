package com.biaozhunyuan.tianyi.cnis.inforfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.view.PullToRefreshAndLoadMoreListView;
import com.biaozhunyuan.tianyi.newuihome.Notice;
import com.biaozhunyuan.tianyi.newuihome.NoticeDetailActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 公告列表页面
 *
 * @author kjx
 * @since 2015/03/12
 */
@SuppressLint("ValidFragment")
public class InformationFragment extends Fragment {


    private Context context;
    private DictionaryHelper dictionaryHelper;
    private Demand demand;
    public static boolean isResume = false;

    private PullToRefreshAndLoadMoreListView lv;
    private RelativeLayout rl_nodata;

    private List<Notice> list;
    private CommanAdapter<Notice> adapter;

    private int pageIndex = 1; //页码


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        dictionaryHelper = new DictionaryHelper(context);
        View view = View.inflate(context, R.layout.fragment_send_doc_send_list, null);
        initViews(view);
        initDemand();
        ProgressDialogHelper.show(context);
        getApplyList();
        setOnEvent();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

    }

    private void initViews(View view) {
        lv = (PullToRefreshAndLoadMoreListView) view.findViewById(R.id.lv_send_doc_send_list);
        rl_nodata = (RelativeLayout) view.findViewById(R.id.rl_nodata);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isResume = false;
    }

    public void reloadData() {
        pageIndex = 1;
        getApplyList();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    private void setOnEvent() {
        lv.setOnRefreshListener(new PullToRefreshAndLoadMoreListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getApplyList();
            }
        });

        lv.setOnLoadMore(new PullToRefreshAndLoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getApplyList();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Notice item = adapter.getDataList().get(position - 1 );
                    Intent intent = new Intent(context, NoticeDetailActivity.class);
                    intent.putExtra("Notice", item);
                    intent.putExtra("NoticeType", "公告");
                    startActivity(intent);
                }
            }
        });

    }

    private void initDemand() {
        demand = new Demand();
        String url = Global.BASE_JAVA_URL + GlobalMethord.标准院首页栏目;
        demand.pageSize = 10;
        demand.sort = "desc";
        demand.sortField = "pushTime";
        Map<String, String> map =  new HashMap<>();
        map.put("type","1");
        demand.dictionaryNames = "type.dict_release_type,creatorId.base_staff,status.dict_release_status";
        demand.keyMap = map;
        demand.src = url;
    }


    /**
     * 获取申请列表
     */
    private void getApplyList() {

        demand.pageIndex = pageIndex;
        demand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                try {
                    list = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(JsonUtils.getStringValue(response, "Data"), "data"), Notice.class);
                    for (Notice notice : list) {
                        notice.setTypeName(demand.getDictName(notice, "type"));
                        notice.setStatusName(demand.getDictName(notice, "status"));
                    }
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
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private CommanAdapter<Notice> getAdapter(List<Notice> list) {
        return new CommanAdapter<Notice>(list, context, R.layout.item_notice_information_list) {
            @Override
            public void convert(int position, Notice item, BoeryunViewHolder viewHolder) {

                if (item != null) {
//                   viewHolder.setTextValue(R.id.tv_creator_seal_receive_item, dictionaryHelper.getUserNameById(item.getCreatorId())); //创建人
                    viewHolder.getView(R.id.tv_creator_seal_receive_item).setVisibility(View.GONE);
//                    viewHolder.getView(R.id.tv_item_seal_receive_type).setVisibility(View.GONE);//
//                    viewHolder.setTextValue(R.id.tv_item_seal_receive_type, item.getTypeName()); //类型
                    viewHolder.getView(R.id.tv_item_seal_receive_type).setVisibility(View.GONE);

                    viewHolder.setTextValue(R.id.tv_creatTime_seal_receive_text_item, dictionaryHelper.getUserNameById(item.getCreatorId())); //创建人
                    viewHolder.setTextValue(R.id.tv_creatTime_seal_receive_item, item.getCreateTime()); //时间

                    viewHolder.getView(R.id.tv_handleTime_text_item).setVisibility(View.GONE);//
                    viewHolder.getView(R.id.tv_handleTime_seal_receive_item).setVisibility(View.GONE);//

                    viewHolder.getView(R.id.tv_no_seal_receive_item).setVisibility(View.GONE);//文号

                    viewHolder.setTextValue(R.id.content_seal_receive_list, item.getTitle()); //标题

//                    viewHolder.setTextValue(R.id.tv_status_item_seal_receive, item.getStatusName()); //当前节点
                    viewHolder.getView(R.id.tv_status_item_seal_receive).setVisibility(View.GONE);
                    viewHolder.getView(R.id.head_item_seal_receive_list).setVisibility(View.GONE);
//                    ImageUtils.displyUserPhotoById(context, item.getCreatorId(),
//                            viewHolder.getView(R.id.head_item_seal_receive_list), Color.parseColor("#3366CC"));
                }
            }
        };
    }

}
