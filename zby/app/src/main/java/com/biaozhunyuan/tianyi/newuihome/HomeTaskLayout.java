package com.biaozhunyuan.tianyi.newuihome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.CustomDayView;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.InputSoftHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.model.Task;
import com.biaozhunyuan.tianyi.task.TaskInfoActivityNew;
import com.biaozhunyuan.tianyi.task.TaskStatusEnum;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.utils.HolidayUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.helper.SharedPreferencesHelper;
import com.biaozhunyuan.tianyi.view.BaseSelectPopupWindow;
import com.biaozhunyuan.tianyi.view.ListRecentlySelectedStaffView;
import com.biaozhunyuan.tianyi.view.RollViewPager;
import com.biaozhunyuan.tianyi.view.SMNOScrollListView;
import com.biaozhunyuan.tianyi.view.SimpleIndicator;
import com.biaozhunyuan.tianyi.view.VoiceInputView;
import com.biaozhunyuan.tianyi.widget.TextEditTextView;
import com.chy.srlibrary.SwipeMenu;
import com.chy.srlibrary.SwipeMenuItem;
import com.chy.srlibrary.interfaceutil.SwipeMenuCreatorInterfaceUtil;
import com.chy.srlibrary.slistview.SMListView;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Request;

/**
 * ???????????????????????????: ??????
 */
@SuppressLint("NewApi")
public class HomeTaskLayout extends LinearLayout {

    private Context mContext;
    private Activity activity;
    private SimpleIndicator indicator;
    private RollViewPager viewPager;
    private ImageView ivAddTask; //??????????????????
    private List<RelativeLayout> mListViewList; //?????????????????????????????????listview?????????
    private Demand<Task> taskDemand;
    private String currentTime; //????????????
    private SMNOScrollListView myTaskListView; //????????????ListView
    private SMNOScrollListView myAssignListView; //????????????ListView
    private BaseSelectPopupWindow addTaskPop; //???????????????popupwindow
    private BaseSelectPopupWindow calendarPop;//???????????????popupwindow
    private BaseSelectPopupWindow executorPop;//??????????????????popupwindow
    private String currentId = Global.mUser.getUuid(); //?????????????????????????????? ???????????????
    private Task currentSelectItem; //???????????????????????????
    private ListRecentlySelectedStaffView staffView;
    private String selectDate = ViewHelper.getDateToday();
    private ImageView myTaskIV;    //???????????? ?????????
    private ImageView myAssignIV; //???????????? ?????????
    private TextView tvMore; //????????????
    private LinearLayout ll_todayTask_select;//???????????? ??????
    private LinearLayout ll_assignTask_select;//???????????? ??????
    private TextView tv_dayNum;// ???????????? ??????
    private TextView tv_assignNum;// ???????????? ??????
    private TextView tv_assignTask;//
    private TextView tv_todayTask;//
    private static int LIST_DATA_SIZE = 3; //??????????????????????????????????????????????????????
    private boolean isUpdateTask = false; //?????????????????????????????? ????????????????????????
    private VoiceInputView inputView;
    private View placeholderView;
    private ArrayList<Calendar> currentCalendars = new ArrayList<Calendar>();
    private SharedPreferencesHelper preferencesHelper;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) { //????????????
                List<Task> allTasks = (List<Task>) msg.obj;
                if (allTasks.size() == 0) {//?????????????????? ????????????????????????
                    myTaskIV.setVisibility(VISIBLE);
                    myTaskListView.setVisibility(GONE);
                } else {
                    myTaskIV.setVisibility(GONE);
                    myTaskListView.setVisibility(VISIBLE);
                }
                if (isShowAllMyTask) {
                    taskAdapter = getTaskAdapter(allTasks);
                } else {
                    if (allTasks.size() > 5) {
                        taskAdapter = getTaskAdapter(allTasks.subList(0, 5));
                    } else {
                        taskAdapter = getTaskAdapter(allTasks);
                    }
                }
                myTaskListView.setAdapter(taskAdapter);
                setCheckAllStatus();
            } else if (msg.what == 0x112) { //????????????
                List<Task> allTasks = (List<Task>) msg.obj;
                if (allTasks.size() == 0) { //?????????????????? ????????????????????????
                    myAssignIV.setVisibility(VISIBLE);
                    myAssignListView.setVisibility(GONE);
                } else {
                    myAssignIV.setVisibility(GONE);
                    myAssignListView.setVisibility(VISIBLE);
                }
                if (isShowAllAssignTask) {
                    assignAdapter = getTaskAdapter(allTasks);
                } else {
                    if (allTasks.size() > 5) {
                        assignAdapter = getTaskAdapter(allTasks.subList(0, 5));
                    } else {
                        assignAdapter = getTaskAdapter(allTasks);
                    }
                }
                myAssignListView.setAdapter(assignAdapter);
                setCheckAllStatus();
            }
        }
    };
    private RelativeLayout myTaskRl;
    private RelativeLayout myAssignRl;
    private View viewMyTask;
    private View viewAssign;
    private CommanAdapter<Task> assignAdapter;
    private CommanAdapter<Task> taskAdapter;
    private List<Task> allMyTask;
    private List<Task> allAssignTask;

    private boolean isShowAllMyTask = false; //??????????????????????????????
    private boolean isShowAllAssignTask = false; //??????????????????????????????


    public HomeTaskLayout(Context context) {
        this(context, null);
    }

    public HomeTaskLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeTaskLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        activity = (Activity) mContext;

        View mParentView = LayoutInflater.from(context).
                inflate(R.layout.include_home_task, this, true);
        initView(mParentView);
        initData();
        initTaskDemand();
        setOnTouch();
    }

    private void setOnTouch() {
        tv_todayTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setHiddenAndShowTitle(0);
                viewPager.setCurrentItem(0);
            }
        });
        tv_assignTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setHiddenAndShowTitle(1);
                viewPager.setCurrentItem(1);
            }
        });

        tvMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    if (!isShowAllMyTask) {
                        taskAdapter.changeData(allMyTask);
                        tvMore.setText("??????");
                    } else {
                        List<Task> tasks;
                        if (allMyTask.size() > 5) {
                            tasks = allMyTask.subList(0, 5);
                        } else {
                            tasks = allMyTask;
                        }
                        tvMore.setText("????????????");
                        taskAdapter.changeData(tasks);
                    }
                    isShowAllMyTask = !isShowAllMyTask;
                } else {
                    if (!isShowAllAssignTask) {
                        assignAdapter.changeData(allAssignTask);
                        tvMore.setText("??????");
                    } else {
                        List<Task> tasks;
                        if (allAssignTask.size() > 5) {
                            tasks = allAssignTask.subList(0, 5);
                        } else {
                            tasks = allAssignTask;
                        }
                        tvMore.setText("????????????");
                        assignAdapter.changeData(tasks);
                    }
                    isShowAllAssignTask = !isShowAllAssignTask;
                }
                viewPager.resetHeight(viewPager.getCurrentItem());
            }
        });
        ivAddTask.setOnClickListener(new OnClickListener() { //???????????????????????????
            @Override
            public void onClick(View v) {
                showNewTaskCardPopWindow();
            }
        });
        myAssignListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //??????????????????????????????
                Task task = (Task) myAssignListView.getItemAtPosition(position);
                skipActivity(task);
            }
        });
        myTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //??????????????????????????????
                Task task = (Task) myTaskListView.getItemAtPosition(position);
                skipActivity(task);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { //??????tab??????
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1 && myAssignListView.getAdapter() == null) {
                    taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=2&beginTime=" + currentTime;
                } else if (position == 0 && (myTaskListView.getAdapter() == null || myTaskListView.getAdapter().getCount() == 0)) {
                    myTaskIV.setVisibility(VISIBLE);
                }
                viewPager.resetHeight(position);//??????????????????
            }

            @Override
            public void onPageSelected(int position) {
                setHiddenAndShowTitle(position);
                setCheckAllStatus();
                if (position == 1) {
                    viewMyTask.setVisibility(GONE);
                    viewAssign.setVisibility(VISIBLE);
                    taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=2&beginTime=" + currentTime;
                } else {
                    viewMyTask.setVisibility(VISIBLE);
                    viewAssign.setVisibility(GONE);
                    taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=1&beginTime=" + currentTime;
                }
                if (assignAdapter == null) {
                    getTaskList();
                }
//                setViewMoreVisible(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param current 0: ???????????????????????? 1:????????????????????????
     */
    private void setHiddenAndShowTitle(int current) {
        if (current == 0) {
            if (ll_todayTask_select.getVisibility() == GONE) {
                ll_assignTask_select.setVisibility(GONE);
                ll_todayTask_select.setVisibility(VISIBLE);
                tv_assignTask.setVisibility(VISIBLE);
                tv_todayTask.setVisibility(GONE);
            }
        } else {
            if (ll_assignTask_select.getVisibility() == GONE) {
                ll_assignTask_select.setVisibility(VISIBLE);
                ll_todayTask_select.setVisibility(GONE);
                tv_assignTask.setVisibility(GONE);
                tv_todayTask.setVisibility(VISIBLE);
            }
        }
    }


    /**
     * ??????????????????????????????
     */
    private void setCheckAllStatus() {
        if (allMyTask != null && viewPager.getCurrentItem() == 0) {
            if (allMyTask.size() > 5) {
                tvMore.setVisibility(VISIBLE);
                if (isShowAllMyTask) {
                    tvMore.setText("??????");
                } else {
                    tvMore.setText("????????????");
                }
            } else {
                tvMore.setVisibility(GONE);
            }
        } else {
            if (allAssignTask != null && allAssignTask.size() > 5) {
                tvMore.setVisibility(VISIBLE);
                if (isShowAllAssignTask) {
                    tvMore.setText("??????");
                } else {
                    tvMore.setText("????????????");
                }
            } else {
                tvMore.setVisibility(GONE);
            }
        }
    }


    /**
     * ??????????????????
     *
     * @param task ??????????????????
     */
    private void skipActivity(Task task) {
        Intent intent = new Intent(mContext, TaskInfoActivityNew.class);
        Bundle bundle = new Bundle();
        task.setFromTurnChat(true);
        bundle.putSerializable("taskInfo", task);
        intent.putExtra("taskIntentInfo", bundle);
        mContext.startActivity(intent);
    }

    /**
     * ??????????????????????????????  ticket=1 ??????????????? ticket=2 ???????????????
     */
    private void initTaskDemand() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=1&beginTime=" + currentTime;
        taskDemand = new Demand<Task>(Task.class);
        taskDemand.pageIndex = 1;
        taskDemand.pageSize = Integer.MAX_VALUE;
        taskDemand.sort = "desc";
        taskDemand.sortField = "creationTime";
        taskDemand.dictionaryNames = "creatorId.base_staff,executorIds.base_staff,customerId.crm_customer";
        taskDemand.src = url;
        getTaskList();
    }

    private void initData() {
        mListViewList = new ArrayList<>();
        preferencesHelper = new SharedPreferencesHelper(mContext);
        currentTime = ViewHelper.getDateString(new Date());
        myTaskListView = new SMNOScrollListView(mContext);
        myAssignListView = new SMNOScrollListView(mContext);

        initListView(); //?????????????????????

        initBgImageView(); //??????????????????????????? ?????????

        initDataList(); //???????????????????????? ????????????????????????????????????

        initViewPager(); //?????????viewpager?????????

        initIndicator(); //??????????????????
    }

    /**
     * ??????????????????
     */
    private void initIndicator() {
        indicator.setVisibleTabCount(2);
        indicator.setTabItemTitles(new String[]{"????????????", "????????????"},
                ViewHelper.getScreenWidth(mContext) / 5 * 2);
        indicator.setViewPager(viewPager, 0);
    }

    /**
     * ?????????viewpager?????????
     */
    private void initViewPager() {
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mListViewList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                RelativeLayout view = mListViewList.get(position);
                container.addView(view);
                viewPager.setObjectForPosition(view, position);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mListViewList.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    /**
     * ???????????????????????? ????????????????????????????????????
     */
    private void initDataList() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        myTaskRl = new RelativeLayout(mContext);
        myTaskRl.setLayoutParams(layoutParams);
//        myTaskRl.setOrientation(VERTICAL);
        myTaskRl.addView(myTaskListView);
        myTaskRl.addView(myTaskIV);

        myAssignRl = new RelativeLayout(mContext);
//        myAssignRl.setOrientation(VERTICAL);
        myAssignRl.setLayoutParams(layoutParams);
        myAssignRl.addView(myAssignListView);
        myAssignRl.addView(myAssignIV);

        //???LinearLayout??????????????????
        myTaskRl.setMinimumHeight((int) ViewHelper.dip2px(mContext, 200));
        myAssignRl.setMinimumHeight((int) ViewHelper.dip2px(mContext, 200));

        mListViewList.add(myTaskRl);
        mListViewList.add(myAssignRl);
    }

    /**
     * ??????????????????????????? ?????????
     */
    private void initBgImageView() {
        myTaskIV = new ImageView(mContext);
        myAssignIV = new ImageView(mContext);
        myTaskIV.setBackground(getResources().getDrawable(R.drawable.bg_task_nulldata));
        myAssignIV.setBackground(getResources().getDrawable(R.drawable.bg_task_nulldata));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        myTaskIV.setLayoutParams(params);
        myAssignIV.setLayoutParams(params);
//        myTaskIV.setMinimumHeight((int) ViewHelper.dip2px(mContext, 225));
//        myAssignIV.setMinimumHeight((int) ViewHelper.dip2px(mContext, 225));

//        myTaskIV.setVisibility(GONE); //????????????
//        myAssignIV.setVisibility(GONE);
    }

    /**
     * ?????????????????????
     */
    private void initListView() {
        myTaskListView.setDivider(mContext.getDrawable(R.drawable.list_divider));
        myAssignListView.setDivider(mContext.getDrawable(R.drawable.list_divider));
        myTaskListView.setDividerHeight(1);
        myAssignListView.setDividerHeight(1);

        initSMListView();
    }

    private void initView(View view) {
        indicator = view.findViewById(R.id.simpleindicatior);
        ivAddTask = view.findViewById(R.id.iv_add_task);
        viewPager = view.findViewById(R.id.viewpager_home_view);
        tvMore = view.findViewById(R.id.tv_view_more);
        ll_todayTask_select = view.findViewById(R.id.ll_today_task_select);
        ll_assignTask_select = view.findViewById(R.id.ll_assign_task_select);
        tv_dayNum = view.findViewById(R.id.tv_today_num);
        tv_assignNum = view.findViewById(R.id.tv_assign_num);
        tv_assignTask = view.findViewById(R.id.tv_assign_task);
        tv_todayTask = view.findViewById(R.id.tv_today_task);
        viewMyTask = view.findViewById(R.id.view_mytask);
        viewAssign = view.findViewById(R.id.view_assign);
    }

    public void refreshList() {
        taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=1&beginTime=" + currentTime;
        getTaskList();
        taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=2&beginTime=" + currentTime;
        getTaskList();
    }

    /**
     * ????????????????????????
     */
    public void getTaskList() {
        boolean isMyTask;
        if (taskDemand.src.contains("ticket=2")) {
            isMyTask = false;
        } else {
            isMyTask = true;
        }
        boolean finalIsMyTask = isMyTask;
        taskDemand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                List<Task> data = taskDemand.data;


                List<Task> doneTasks = new ArrayList<>();
                if (data != null) {
                    Iterator<Task> iterator = data.iterator();
                    while (iterator.hasNext()) {
                        Task t = iterator.next();
                        if (t.getStatus().equals(TaskStatusEnum.?????????.getName())
                                || t.getStatus().equals(TaskStatusEnum.?????????.getName())) {
                            doneTasks.add(t);
                            iterator.remove(); //?????????????????????????????????????????????
                        }
                    }
                    if (!finalIsMyTask) { //???????????????????????????????????????????????????????????????????????????
                        allAssignTask = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++) {
                            Task task = data.get(i);
                            if (!task.getExecutorIds().equals(Global.mUser.getUuid())) {
                                allAssignTask.add(task);
                            }
                        }
                        if (allAssignTask.size() > 0) {
                            tv_assignNum.setVisibility(VISIBLE);
                            tv_assignNum.setText(allAssignTask.size() + "");
                        } else {
                            tv_assignNum.setVisibility(GONE);
                        }
                        for (Task t : doneTasks) { //????????????????????????????????????????????????
                            if (!t.getExecutorIds().equals(Global.mUser.getUuid())) {
                                allAssignTask.add(t);
                            }
                        }
                        Message message = handler.obtainMessage();
                        message.what = 0x112;
                        message.obj = allAssignTask;
                        handler.sendMessage(message);
                    } else {
                        allMyTask = new ArrayList<>();
                        allMyTask.addAll(data);
                        if (allMyTask.size() > 0) {
                            tv_dayNum.setVisibility(VISIBLE);
                            tv_dayNum.setText(allMyTask.size() + "");
                        } else {
                            tv_dayNum.setVisibility(GONE);
                        }
                        allMyTask.addAll(doneTasks);
                        Message message = handler.obtainMessage();
                        message.what = 0x111;
                        message.obj = allMyTask;
                        handler.sendMessage(message);
                    }
                }
                ProgressDialogHelper.dismiss();
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

//    /**
//     * ???????????????????????? ???????????????
//     * @param position
//     */
//    public void setViewMoreVisible(int position){
//        if(position == 0){
//            ListAdapter adapter = myTaskListView.getAdapter();
//            if(adapter!=null && adapter.getMsgTotalCount() > LIST_DATA_SIZE){
//                tvMore.setVisibility(VISIBLE);
//            } else {
//                tvMore.setVisibility(GONE);
//            }
//        } else {
//            ListAdapter adapter = myAssignListView.getAdapter();
//            if(adapter!=null && adapter.getMsgTotalCount() > LIST_DATA_SIZE){
//                tvMore.setVisibility(VISIBLE);
//            } else {
//                tvMore.setVisibility(GONE);
//            }
//        }
//
//    }

    /**
     * ??????????????????
     */
    private void initSMListView() {
        final SwipeMenuCreatorInterfaceUtil creator = new SwipeMenuCreatorInterfaceUtil() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#EC4D3D")));
                deleteItem.setWidth(250);
                deleteItem.setTitle("??????");
                deleteItem.setTitleColor(Color.parseColor("#ffffff"));
                deleteItem.setTitleSize(14);
                menu.addMenuItem(deleteItem);

                SwipeMenuItem dateItem = new SwipeMenuItem(mContext);
                dateItem.setBackground(new ColorDrawable(Color.parseColor("#417EBF")));
                dateItem.setWidth(250);
                dateItem.setTitle("??????");
                dateItem.setTitleColor(Color.parseColor("#ffffff"));
                dateItem.setTitleSize(14);
                menu.addMenuItem(dateItem);
            }
        };
        // ??????????????????(??????????????????????????????????????????????????????)
        myTaskListView.setMenuCreator(creator);
        myAssignListView.setMenuCreator(creator);
        // ????????????????????????????????????????????????????????????????????????????????????SwipMenuListView??????????????????
        // ?????????????????????
        myTaskListView.setOnMenuItemClickListener(new SMListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                Task item = (Task) myTaskListView.getAdapter().getItem(i);
                currentSelectItem = item;
                if (item.getCreatorId().equals(Global.mUser.getUuid())) {
                    switch (i1) {
                        case 0: //??????
                            deleteTask(item);
                            break;
                        case 1: //??????
                            isUpdateTask = true;
                            showSelectCalendar(item);
                            break;
                    }
                } else {
                    Toast.makeText(mContext, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        myAssignListView.setOnMenuItemClickListener(new SMListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                Task item = (Task) myAssignListView.getAdapter().getItem(i);
                currentSelectItem = item;
                switch (i1) {
                    case 0: //??????
                        deleteTask(item);
                        assignAdapter.remove(item);
                        break;
                    case 1: //??????
                        isUpdateTask = true;
                        showSelectCalendar(item);
                        break;
                }
                return false;
            }
        });
        setListSwipeEnable(myTaskListView);
        setListSwipeEnable(myAssignListView);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param listview
     */
    private void setListSwipeEnable(SMNOScrollListView listview) {
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        /**
                         * oldPos  ????????????????????????position
                         * setSwipeEnable() ?????????????????????
                         *           ?????????false????????????????????????
                         *           ?????????true????????????
                         *           ????????????true
                         *
                         * ???????????????????????????????????????????????????????????????
                         */
                        int oldPos = listview.pointToPosition((int) event.getX(), (int) event.getY());
                        if (oldPos < 0) {
                            // ??????????????????????????????Item????????????false
                            return false;
                        }
                        Task item = (Task) listview.getAdapter().getItem(oldPos);
                        if (item.getStatus().equals(TaskStatusEnum.?????????.getName()) ||
                                item.getStatus().equals(TaskStatusEnum.?????????.getName())) {
                            // ????????????????????????????????????
                            listview.setSwipeEnable(false);
                        } else {
                            listview.setSwipeEnable(true);
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * ????????????
     */
    private void deleteTask(Task item) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????????????????????????????;
        JSONObject jo = new JSONObject();
        try {
            jo.put("ids", item.getUuid());
            jo.put("tableName", "oa_work_scheduling");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest.postAsyn(url, jo, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                String data = JsonUtils.pareseData(response);
                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
                refreshList();
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, JsonUtils.pareseMessage(result), Toast.LENGTH_SHORT);
            }
        });

    }


    private CommanAdapter<Task> getTaskAdapter(List<Task> taskList) {
        return new CommanAdapter<Task>(taskList, mContext, R.layout.item_task_home) {

            @Override
            public void convert(int position, final Task item, final BoeryunViewHolder viewHolder) {
                viewHolder.setTextValue(R.id.tv_creater_task_item, item.getCreatorName());
                TextView tvContent = viewHolder.getView(R.id.task_content);
                tvContent.setText(item.getContent());
                if (item.getStatus().equals(TaskStatusEnum.?????????.getName())
                        || item.getStatus().equals(TaskStatusEnum.?????????.getName())) {
                    tvContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //????????????????????????????????????
                } else {
                    tvContent.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);  // ????????????????????????
                }
                final ImageView ivStatus = viewHolder.getView(R.id.task_status);
                /**
                 * ??????????????????????????????????????????
                 */
                tvContent.setTextColor(getResources().getColor(R.color.list_content));
                if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_finish);
                    tvContent.setTextColor(getResources().getColor(R.color.color_message));
                } else if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_);
                } else if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_);
                    tvContent.setTextColor(getResources().getColor(R.color.color_message));
                } else if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_);
                }
                ivStatus.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getExecutorIds().equals(Global.mUser.getUuid())) {
                            if (TaskStatusEnum.?????????.getName().equals(item.getStatus()) ||
                                    TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                                saveTask(item, 1);
                            } else if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                                saveTask(item, 3);
                            } else {
                                Toast.makeText(mContext, "?????????????????????????????????????????????!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "?????????????????????????????????????????????!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    /**
     * ????????????
     *
     * @param status ???????????????1==????????????3==?????????
     */
    private void saveTask(Task task, int status) {

        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?uuid=" + task.getUuid() + "&ticket=" + status;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                taskDemand.src = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?ticket=1&beginTime=" + currentTime;
                getTaskList();
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, JsonUtils.pareseMessage(result), Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * ??????????????????
     */
    private void upDateTask(Task task) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;
        StringRequest.postAsyn(url, task, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                currentSelectItem = null;
                refreshList();
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {

            }
        });
    }

    /**
     * ?????????????????????
     */
    public void receiveSelectedUser(User user) {
        if (staffView != null) {
            staffView.reloadStaffList(user);
        }
    }

    /**
     * ????????????????????????
     */
    private void showNewTaskCardPopWindow() {
        addTaskPop = new BaseSelectPopupWindow(activity, R.layout.pop_add_task);
        // popWiw.setOpenKeyboard(true);
        addTaskPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        addTaskPop.setFocusable(true);
//        addTaskPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addTaskPop.setShowTitle(false);
        addTaskPop.setBackgroundDrawable(new ColorDrawable(0));

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        TextEditTextView edt = addTaskPop.getContentView().findViewById(R.id.et_input_home_add_task);
        LinearLayout addBtn = addTaskPop.getContentView().findViewById(R.id.btn_home_add_task);
        ImageView tvDate = addTaskPop.getContentView().findViewById(R.id.tv_tomorrow);
        ImageView voiceBtn = addTaskPop.getContentView().findViewById(R.id.btn_voice);
        ImageView selectexBtn = addTaskPop.getContentView().findViewById(R.id.select_executor);
        placeholderView = addTaskPop.getContentView().findViewById(R.id.placeholderView);
        inputView = addTaskPop.getContentView().findViewById(R.id.voice_input_view_home_add_task);
        inputView.setSwitvhKeyBoardandVoiceVisible(false);
        inputView.setRelativeInputView(edt);

        edt.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        edt.setImeOptions(EditorInfo.IME_ACTION_SEND);

        //????????????????????????
        boolean isVoiceOpen = preferencesHelper.getBooleanValue("isVoiceOpen", false);

        if (isVoiceOpen) {
            voiceBtn.setBackground(getResources().getDrawable(R.drawable.ico_add_task_keyboard));
            inputView.setVisibility(View.VISIBLE);
            InputSoftHelper.hideKeyboard(edt);
        } else {
            voiceBtn.setBackground(getResources().getDrawable(R.drawable.ico_add_task_voice));
            inputView.setVisibility(View.GONE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputSoftHelper.ShowKeyboard(edt);
                }
            }, 100);
        }

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowVoiceType()) { //?????????????????????
                    preferencesHelper.putBooleanValue("isVoiceOpen", false);
                    voiceBtn.setBackground(getResources().getDrawable(R.drawable.ico_add_task_voice));
                    inputView.setVisibility(View.GONE);
                    InputSoftHelper.ShowKeyboard(edt);
                } else {//?????????????????????
                    preferencesHelper.putBooleanValue("isVoiceOpen", true);
                    voiceBtn.setBackground(getResources().getDrawable(R.drawable.ico_add_task_keyboard));
                    inputView.setVisibility(View.VISIBLE);
                    InputSoftHelper.hideKeyboard(edt);
                }
            }
        });
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceBtn.setBackground(getResources().getDrawable(R.drawable.icon_add_task_voice));
                inputView.setVisibility(View.GONE);
                InputSoftHelper.ShowKeyboard(edt);
            }
        });

        edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (!TextUtils.isEmpty(edt.getText().toString().trim())) {
                        String content = edt.getText().toString().trim();
                        addTask(content);
                        edt.setText("");
                        addTaskPop.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        selectexBtn.setOnClickListener(new View.OnClickListener() { //???????????????
            @Override
            public void onClick(View v) {
                InputSoftHelper.hideKeyboard(edt);
                showSelectExecutor();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() { //????????????

            public void onClick(View v) {
                String content = edt.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    addTask(content);
                    edt.setText("");
                    addTaskPop.dismiss();
                } else {
                    Toast.makeText(mContext, "????????????????????????!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //????????????
                InputSoftHelper.hideKeyboard(tvDate);
                showSelectCalendar(null);
            }
        });

        addTaskPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
                if (!isShowVoiceType() && InputSoftHelper.isSoftShowing(activity)) {
                    InputSoftHelper.hideShowSoft(mContext);
                }
            }
        });
        lp.alpha = 0.65f;
        activity.getWindow().setAttributes(lp);
        addTaskPop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_menu_home, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * ????????????????????? ???????????????
     */
    private void showSelectExecutor() {
        if (executorPop == null) {
            executorPop = new BaseSelectPopupWindow(activity, R.layout.pop_add_task_executor);
            executorPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            executorPop.setFocusable(true);
//            executorPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            executorPop.setShowTitle(false);
            executorPop.setBackgroundDrawable(new ColorDrawable(0));

            staffView = executorPop.getContentView().findViewById(R.id.staff_view);

            staffView.setOnKeyBoardSelectedUserListener(new ListRecentlySelectedStaffView.OnKeyBoardSelectedUserListener() {
                @Override
                public void onSelected(User user) {
                    currentId = user.getUuid();
                    executorPop.dismiss();
                }
            });

            executorPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!isShowVoiceType()) {
                        placeholderView.setVisibility(GONE);
                        if (!isShowVoiceType() && InputSoftHelper.isSoftShowing(activity)) {
                            InputSoftHelper.hideShowSoft(mContext);
                        }
                    }
//                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        if (!isShowVoiceType()) {
            placeholderView.setVisibility(VISIBLE);
        }
        executorPop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_menu_home, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return true ?????????????????? false ??????????????????
     */
    public boolean isShowVoiceType() {
        return inputView.getVisibility() == VISIBLE;
    }

    /**
     * ???????????? ????????????
     */
    @SuppressLint("WrongViewCast")
    private void showSelectCalendar(Task item) {
        if (calendarPop == null) {
            calendarPop = new BaseSelectPopupWindow(activity, R.layout.pop_add_task_calendar);
            calendarPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            calendarPop.setFocusable(true);
//            calendarPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            calendarPop.setShowTitle(false);
            calendarPop.setBackgroundDrawable(new ColorDrawable(0));
            TextView currentData = calendarPop.getContentView().findViewById(R.id.tv_currentData);

            ImageView ivLeft = calendarPop.getContentView().findViewById(R.id.iv_left);
            ImageView ivRight = calendarPop.getContentView().findViewById(R.id.iv_right);
            TextView tvToday = calendarPop.getContentView().findViewById(R.id.tv_toady);
            TextView tvTomorrow = calendarPop.getContentView().findViewById(R.id.tv_tomorrow);

            currentData.setText(ViewHelper.formatDateToStr(new Date(), "yyyy-MM"));
            MonthPager monthPager = calendarPop.getContentView().findViewById(R.id.calendar_view);
            CustomDayView customDayView = new CustomDayView(mContext, R.layout.custom_day);
            OnSelectDateListener onSelectDateListener = new OnSelectDateListener() {
                @Override
                public void onSelectDate(CalendarDate date) {
                    customDayView.setIsShowSelected(true);
                    tvToday.setTextColor(Color.parseColor("#78787C"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left));
                    tvTomorrow.setTextColor(Color.parseColor("#78787C"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right));
                    if (currentSelectItem == null) {
                        selectDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                        calendarPop.dismiss();
                    } else {
                        selectDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                        currentSelectItem.setBeginTime(selectDate);
                        currentSelectItem.setEndTime(selectDate);
                        upDateTask(currentSelectItem);
                        calendarPop.dismiss();
                        currentSelectItem = null;
                    }
                }

                @Override
                public void onSelectOtherMonth(int offset) {
                    //????????? -1????????????????????????????????? ??? 1?????????????????????????????????
                    monthPager.selectOtherMonth(offset);
                }
            };
            monthPager.setViewheight(Utils.dpi2px(mContext, 270));
            CalendarViewAdapter calendarAdapter = new CalendarViewAdapter(
                    mContext,
                    onSelectDateListener,
                    CalendarAttr.CalendayType.MONTH,
                    customDayView);
            HashMap<String, String> holidayMap = HolidayUtils.getHolidayMap();
            calendarAdapter.setMarkData(holidayMap);

            monthPager.setAdapter(calendarAdapter);
            monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
            monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                    position = (float) Math.sqrt(1 - Math.abs(position));
                    page.setAlpha(position);
                }
            });
            monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    currentCalendars = calendarAdapter.getPagers();
                    if (currentCalendars.get(position % currentCalendars.size()) instanceof Calendar) {
                        CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                        currentData.setText(date.getYear() + "???" + date.getMonth() + "???");
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            ivLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) { //????????????
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
                }
            });
            ivRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) { //????????????
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
                }
            });
            tvToday.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) { //??????
                    customDayView.setIsShowSelected(false);
                    selectDate = ViewHelper.getDateToday();
                    tvToday.setTextColor(Color.parseColor("#5B8AF2"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left_select));
                    tvTomorrow.setTextColor(Color.parseColor("#78787C"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);

                    if (currentSelectItem != null && isUpdateTask) {
                        currentSelectItem.setBeginTime(ViewHelper.getCurrentTime());
                        currentSelectItem.setEndTime(ViewHelper.getCurrentTime());
                        upDateTask(currentSelectItem);
                    }
                }
            });
            tvTomorrow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) { //??????
                    customDayView.setIsShowSelected(false);
                    selectDate = ViewHelper.getDateTomorrow();
                    tvTomorrow.setTextColor(Color.parseColor("#5B8AF2"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right_select));
                    tvToday.setTextColor(Color.parseColor("#78787C"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left));
                    cancelSelectState(calendarAdapter);

                    if (currentSelectItem != null && isUpdateTask) {
                        currentSelectItem.setBeginTime(ViewHelper.getTomorrowTime());
                        currentSelectItem.setEndTime(ViewHelper.getTomorrowTime());
                        upDateTask(currentSelectItem);
                    }
                    calendarPop.dismiss();
                }
            });

            calendarPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isUpdateTask = false;
                    if (inputView != null) {
                        if (!isShowVoiceType() && InputSoftHelper.isSoftShowing(activity)) {
                            InputSoftHelper.hideShowSoft(mContext);
                        }
                    }
//                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        calendarPop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_tasklane_trello, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * ????????????????????????????????????
     *
     * @param calendarAdapter
     */
    private void cancelSelectState(CalendarViewAdapter calendarAdapter) {
        ArrayList<Calendar> calendars = calendarAdapter.getPagers();
        for (int i = 0; i < calendars.size(); ++i) {
            Calendar calendar = calendars.get(i);
            calendar.cancelSelectState();
        }
    }

    /**
     * ????????????
     *
     * @param content
     */
    private void addTask(String content) {
        ProgressDialogHelper.show(mContext, "?????????...");
        Task task = new Task();
        task.setContent(content);
        task.setExecutorIds(currentId);
        task.setBeginTime(selectDate);
        String endTime;
        if (!TextUtils.isEmpty(selectDate) && selectDate.length() >= 10) {
            endTime = selectDate.substring(0, 10);
            task.setEndTime(endTime + " 23:59:59");
        }
        task.setCreatorId(Global.mUser.getUuid());

        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;

        StringRequest.postAsyn(url, task, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                currentId = Global.mUser.getUuid();
                if (staffView != null) {
                    staffView.reloadStaffList(currentId);
                }
                refreshList();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, JsonUtils.pareseData(result), Toast.LENGTH_SHORT).show();
                ProgressDialogHelper.dismiss();
            }
        });
    }


}
