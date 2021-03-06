package com.biaozhunyuan.tianyi.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.model.Task;
import com.biaozhunyuan.tianyi.common.model.user.Latest;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.CustomDayView;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ORMDataHelper;
import com.biaozhunyuan.tianyi.common.helper.PreferceManager;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.utils.DateTimeUtil;
import com.biaozhunyuan.tianyi.common.utils.HolidayUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.utils.StrUtils;
import com.biaozhunyuan.tianyi.view.BaseSelectPopupWindow;
import com.biaozhunyuan.tianyi.view.BoeryunSearchView;
import com.chy.srlibrary.SwipeMenu;
import com.chy.srlibrary.SwipeMenuItem;
import com.chy.srlibrary.interfaceutil.SwipeMenuCreatorInterfaceUtil;
import com.chy.srlibrary.slistview.SMListView;
import com.chy.srlibrary.slistview.SMRListView;
import com.j256.ormlite.dao.Dao;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.loonggg.weekcalendar.entity.CalendarData;
import com.loonggg.weekcalendar.view.WeekCalendar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by ????????? on 2017/10/8.
 * ??????????????????
 */

public class TaskListFragmentNew extends Fragment {

    private SMRListView lv;
    private List<Task> taskList;
    private CommanAdapter<Task> adapter;
    private Demand<Task> demand;
    public int pageIndex = 1;
    public static boolean isReasume = false;
    private static final String TASK_LIST_VIEW_CHECK = "TASK_LIST_VIEW_CHECK";
    //    private HorizontalScrollView scrollView;
    private WeekCalendar weekCalendar;
    private BoeryunSearchView searchView;
    private RelativeLayout rl_back_calander;
    private RelativeLayout rl_show_calander;
    private RelativeLayout rl_hide_calander;
    private ImageView iv_show_calander;
    private ImageView iv_hide_calander;
    private TextView tv_current_time;
    private ImageView iv_hide_head;
    private int calanderType = 0;//??????????????????0=?????????1=??????   ?????????????????????
    private boolean isHeadShow = false;
    private String currentTime = "";  //??????????????????
    private ORMDataHelper dataHelper;
    private List<Latest> recentList; //?????????????????????
    private TextView tvAllTask;
    public String currentUrl;
    private TaskListActivityNew activity;
    private DictionaryHelper helper;
    private SmartRefreshLayout refreshLayout;
    private BaseSelectPopupWindow calendarPop;
    private OnSelectDateListener onSelectDateListener;
    private List<String> selectDates = new ArrayList<>();
    private TaskListActivityNew mContext;
    private onSelectDateListener selectDateListener;
    private Map<String, String> keyMap;
    private MonthPager monthPager;
    private Task currentSelectItem; //???????????????????????????
    public String currentDate;

    /**
     * ????????????
     */
    private PopupWindow popupWindow;
    private LinearLayout llTimeTypes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, null);
        activity = (TaskListActivityNew) getActivity();
        initData();
        initPopwindow();
        initViews(view);
        ProgressDialogHelper.show(activity);
        initDemand();
        getTaskData();
        getTaskList();
//        createAddView();
        setOnEvent();
        return view;
    }

    private void initData() {
        mContext = (TaskListActivityNew) getActivity();
        recentList = new ArrayList<>();
        dataHelper = ORMDataHelper.getInstance(getActivity());
        helper = new DictionaryHelper(activity);
    }


    private void initViews(View view) {
        lv = view.findViewById(R.id.lv_task_list);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        view.findViewById(R.id.lv_done).setVisibility(View.GONE);
        initSMListView();
        View view1 = View.inflate(getActivity(), R.layout.header_worklog_new, null);
        RelativeLayout rl_time = (RelativeLayout) view1.findViewById(R.id.rl_time_header);
        llTimeTypes = (LinearLayout) view1.findViewById(R.id.ll_select_time_types);
        weekCalendar = (WeekCalendar) view1.findViewById(R.id.weekcalendar_task_day_view);
        searchView = view1.findViewById(R.id.search_view);
        tv_current_time = (TextView) view1.findViewById(R.id.tv_current_time);
        iv_hide_calander = (ImageView) view1.findViewById(R.id.iv_hide_calander);
        iv_show_calander = (ImageView) view1.findViewById(R.id.iv_home_show_calander);
        rl_show_calander = (RelativeLayout) view1.findViewById(R.id.rl_home_calander);
        rl_hide_calander = (RelativeLayout) view1.findViewById(R.id.rl_home_yearmonth);
        rl_back_calander = (RelativeLayout) view1.findViewById(R.id.rl_calendar_month);
        tvAllTask = view1.findViewById(R.id.tv_all_view);

        iv_hide_head = (ImageView) view1.findViewById(R.id.iv_hide_head_log_List);
//        ll_user_root_task_day_view = view1.findViewById(R.id.ll_user_root_task_day_view);
        weekCalendar.setVisibility(View.VISIBLE);
        lv.addHeaderView(view1);
        rl_time.setVisibility(View.VISIBLE);

        calanderType = 0;
        showCanlender();

        String time = ViewHelper.getDateTodayStr();

        currentTime = time;
        String yue = time.substring(5, 7);
        String ri = time.substring(8, 10);
        if (yue.substring(0, 1).equals("0")) {
            yue = yue.substring(1, 2);
        }
        if (ri.substring(0, 1).equals("0")) {
            ri = ri.substring(1, 2);
        }
        tv_current_time.setText(currentTime);

        refreshLayout.setReboundDuration(200);//??????????????????????????????
        refreshLayout.setDragRate(0.7f);//??????????????????/????????????????????????=????????????
        refreshLayout.setEnableLoadMore(true);//??????????????????????????????
        refreshLayout.setEnableRefresh(true);//??????????????????????????????
        refreshLayout.setEnableOverScrollDrag(true);//?????????????????????????????????????????????1.0.4
        refreshLayout.setEnableAutoLoadMore(true);//????????????????????????????????????????????????????????????
    }

    private void initPopwindow() {
        View view = View.inflate(mContext, R.layout.popup_select_time_types, null);
        popupWindow = new PopupWindow(view, (int) ViewHelper.dip2px(mContext, 100), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout llWeek = view.findViewById(R.id.ll_this_week);
        LinearLayout llMonth = view.findViewById(R.id.ll_this_month);
        LinearLayout llAll = view.findViewById(R.id.ll_all_task);

        llWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAllTask.setText("??????");
                popupWindow.dismiss();
                List<String> dateThisWeeks = ViewHelper.getDateThisWeeks();
                if (dateThisWeeks.size() > 6) {
                    String startTime = dateThisWeeks.get(0) + " 00:00:00";
                    String endTime = dateThisWeeks.get(6) + " 23:59:59";
                    keyMap.put("beginTime", startTime);
                    keyMap.put("endTime", endTime);

                    pageIndex = 1;
                    getTaskData(startTime, endTime);
                    getTaskList();
                }
            }
        });

        llMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                tvAllTask.setText("??????");
                String startTime = ViewHelper.formatDateToStr1
                        (ViewHelper.getFirstDateOfThisMonth()) + " 00:00:00";
                String endTime = ViewHelper.formatDateToStr1
                        (ViewHelper.getLastDateOfThisMonth()) + " 23:59:59";

                keyMap.put("beginTime", startTime);
                keyMap.put("endTime", endTime);

                getTaskData(startTime, endTime);
                pageIndex = 1;
                getTaskList();
            }
        });

        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                tvAllTask.setText("??????");
                keyMap.put("beginTime", "");
                keyMap.put("endTime", "");
                String startTime = weekCalendar.getCurrentWeekDatas().get(0) + " 00:00:00";
                String endTime = weekCalendar.getCurrentWeekDatas().get(6) + " 23:59:59";
                getTaskData(startTime, endTime);
                pageIndex = 1;
                getTaskList();
            }
        });

    }

    /**
     * ?????????????????????????????????
     */
    private void getTaskData() {
        List<CalendarData> currentWeekDatas = weekCalendar.getCurrentWeekDatas();
        getTaskData(currentWeekDatas.get(0).getDateStr(),
                currentWeekDatas.get(currentWeekDatas.size() - 1).getDateStr());
    }

    /**
     * ?????????????????????????????????
     *
     * @param startTime ???????????????????????????
     * @param endTime   ???????????????????????????
     */
    public void getTaskData(String startTime, String endTime) {
        if (TextUtils.isEmpty(startTime)) {
            startTime = weekCalendar.getCurrentWeekDatas().get(0).getDateStr();
        }
        if (TextUtils.isEmpty(endTime)) {
            endTime = weekCalendar.getCurrentWeekDatas().get(6).getDateStr();
        }

        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????????????????????
                + "?beginTime=" + startTime
                + " 00:00:00&endTime=" + endTime
                + " 23:59:59&executorIds=" + activity.currentUser.getUuid()
                + "&projectId=" + StrUtils.pareseNull(keyMap.get("projectId"))
                + "&status=" + StrUtils.pareseNull(keyMap.get("status"));

        if (!TextUtils.isEmpty(keyMap.get("creatorId"))) {  //????????????
            url += "&creatorId=" + mContext.currentUser.getUuid() + "&index=myTask";
        } else if (TextUtils.isEmpty(keyMap.get("isMyTask"))) { //???????????????

        } else { //??????
            url += "&index=myTask";
        }
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                selectDates.clear();
                List<Task> data = JsonUtils.jsonToArrayEntity(JsonUtils.pareseData(response), Task.class);
                for (Task task : data) {
                    if (task.isHave()) {
                        String[] split = task.getBeginTime().split(" ");
                        selectDates.add(split[0]);
                    }
                }
                if (!selectDates.isEmpty()) {
                    weekCalendar.setSelectDates(selectDates);
                } else {
                    weekCalendar.clearMarkDay();
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

    /**
     * ??????????????????
     */
    private void initSMListView() {
        final SwipeMenuCreatorInterfaceUtil creator = new SwipeMenuCreatorInterfaceUtil() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#EC4D3D")));
                deleteItem.setWidth(250);
                deleteItem.setTitle("??????");
                deleteItem.setTitleColor(Color.parseColor("#ffffff"));
                deleteItem.setTitleSize(14);
                menu.addMenuItem(deleteItem);

                SwipeMenuItem dateItem = new SwipeMenuItem(getActivity());
                dateItem.setBackground(new ColorDrawable(Color.parseColor("#417EBF")));
                dateItem.setWidth(250);
                dateItem.setTitle("??????");
                dateItem.setTitleColor(Color.parseColor("#ffffff"));
                dateItem.setTitleSize(14);
                menu.addMenuItem(dateItem);
            }
        };
        // ??????????????????(??????????????????????????????????????????????????????)
        lv.setMenuCreator(creator);
        // ????????????????????????????????????????????????????????????????????????????????????SwipMenuListView??????????????????
        // ?????????????????????
        lv.setOnMenuItemClickListener(new SMListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                Task item = adapter.getItem(i);
                currentSelectItem = item;
                if (item.getCreatorId().equals(Global.mUser.getUuid())) {
                    switch (i1) {
                        case 0: //??????
                            deleteTask(item);
                            break;
                        case 1: //??????
                            showSelectCalendar(item);
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        setListSwipeEnable(lv);
    }

    /**
     * ??????????????????
     */
    private void upDateTask(Task task) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;
        StringRequest.postAsyn(url, task, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                mContext.refreshList(false);
                getTaskData();
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
     * ???????????? ????????????
     */
    @SuppressLint("WrongViewCast")
    private void showSelectCalendar(Task item) {

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();


        if (calendarPop == null) {
            calendarPop = new BaseSelectPopupWindow(getActivity(), R.layout.pop_add_task_calendar);
            calendarPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            calendarPop.setFocusable(true);
            calendarPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            calendarPop.setShowTitle(false);
            calendarPop.setBackgroundDrawable(new ColorDrawable(0));
            TextView currentData = calendarPop.getContentView().findViewById(R.id.tv_currentData);

            ImageView ivLeft = calendarPop.getContentView().findViewById(R.id.iv_left);
            ImageView ivRight = calendarPop.getContentView().findViewById(R.id.iv_right);
            TextView tvToday = calendarPop.getContentView().findViewById(R.id.tv_toady);
            TextView tvTomorrow = calendarPop.getContentView().findViewById(R.id.tv_tomorrow);

            currentData.setText(ViewHelper.formatDateToStr(new Date(), "yyyy-MM"));
            monthPager = calendarPop.getContentView().findViewById(R.id.calendar_view);
            CustomDayView customDayView = new CustomDayView(getActivity(), R.layout.custom_day);

            onSelectDateListener = new OnSelectDateListener() {
                @Override
                public void onSelectDate(CalendarDate date) {
                    String selectTime = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                    currentSelectItem.setBeginTime(selectTime);
                    currentSelectItem.setEndTime(selectTime);
                    upDateTask(currentSelectItem);
                    calendarPop.dismiss();
                    onSelectDateListener = null;  //???????????????????????????listener??????????????????????????????
                }

                @Override
                public void onSelectOtherMonth(int offset) {
                    //????????? -1????????????????????????????????? ??? 1?????????????????????????????????
                    monthPager.selectOtherMonth(offset);
                }
            };

            monthPager.setViewheight(Utils.dpi2px(getActivity(), 270));
            CalendarViewAdapter calendarAdapter = new CalendarViewAdapter(
                    getActivity(),
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
                    ArrayList<Calendar> currentCalendars = calendarAdapter.getPagers();
                    if (currentCalendars.get(position % currentCalendars.size()) instanceof Calendar) {
                        CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                        currentData.setText(date.getYear() + "???" + date.getMonth() + "???");
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //????????????
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
                }
            });
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //????????????
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
                }
            });
            tvToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //??????
                    item.setBeginTime(ViewHelper.getDateToday());
                    item.setEndTime(ViewHelper.getDateToday());
                    tvToday.setTextColor(Color.parseColor("#5B8AF2"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left_select));
                    tvTomorrow.setTextColor(Color.parseColor("#78787C"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);

                    currentSelectItem.setBeginTime(ViewHelper.getCurrentFullTime());
                    currentSelectItem.setEndTime(ViewHelper.getCurrentFullTime());
                    upDateTask(currentSelectItem);
                }
            });
            tvTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //??????
                    item.setBeginTime(ViewHelper.getDateTomorrow());
                    item.setEndTime(ViewHelper.getDateTomorrow());
                    tvTomorrow.setTextColor(Color.parseColor("#5B8AF2"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right_select));
                    tvToday.setTextColor(Color.parseColor("#78787C"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);

                    currentSelectItem.setBeginTime(ViewHelper.getTomorrowTime());
                    currentSelectItem.setEndTime(ViewHelper.getTomorrowTime());
                    upDateTask(currentSelectItem);
                }
            });

            calendarPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1f;
                    getActivity().getWindow().setAttributes(lp);
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        lp.alpha = 0.65f;
        getActivity().getWindow().setAttributes(lp);
        calendarPop.showAtLocation(getLayoutInflater().inflate(R.layout.fragment_tasklane_trello, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                mContext.refreshList(false);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(getActivity(), JsonUtils.pareseMessage(result), Toast.LENGTH_SHORT);
            }
        });

    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param listview
     */
    private void setListSwipeEnable(SMRListView listview) {
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
                        if (item != null) {
                            if (StrUtils.pareseNull(item.getStatus()).equals(TaskStatusEnum.?????????.getName()) ||
                                    StrUtils.pareseNull(item.getStatus()).equals(TaskStatusEnum.?????????.getName())) {
                                // ????????????????????????????????????
                                listview.setSwipeEnable(false);
                            } else {
                                listview.setSwipeEnable(true);
                            }
                            break;
                        }
                }
                return false;
            }
        });
    }

    private void showCanlender() {
        if (calanderType == 0) {
            rl_show_calander.setVisibility(View.VISIBLE);
            rl_hide_calander.setVisibility(View.GONE);
            iv_hide_calander.setImageResource(R.drawable.arrow_up);
            calanderType = 1;
        } else if (calanderType == 1) {
            rl_show_calander.setVisibility(View.GONE);
            rl_hide_calander.setVisibility(View.VISIBLE);
            iv_hide_calander.setImageResource(R.drawable.arrow_down);
            calanderType = 0;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isReasume) {
            pageIndex = 1;
            getTaskList();
            isReasume = false;
        }
    }

    private void setOnEvent() {


        llTimeTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(llTimeTypes);
            }
        });

        iv_hide_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHeadShow) {
//                    scrollView.setVisibility(View.GONE);
                    isHeadShow = false;
                    iv_hide_head.setImageResource(R.drawable.icon_show_head);
                } else {
//                    scrollView.setVisibility(View.VISIBLE);
                    isHeadShow = true;
                    iv_hide_head.setImageResource(R.drawable.icon_hide_head);
                }
            }
        });
        searchView.setOnSearchedListener(new BoeryunSearchView.OnSearchedListener() {
            @Override
            public void OnSearched(String str) {
                demand.key = "searchField_string_content";
                demand.value = "1|" + str;
                pageIndex = 1;
                getTaskList();
            }
        });

        searchView.setOnButtonClickListener(new BoeryunSearchView.OnButtonClickListener() {
            @Override
            public void OnCancle() {
                demand.key = "searchField_string_content";
                demand.value = "";
                pageIndex = 1;
//                lv.startRefresh();
                getTaskList();
            }

            @Override
            public void OnClick() {

            }
        });

        weekCalendar.setOnDateClickListener(new WeekCalendar.OnDateClickListener() {
            @Override
            public void onDateClick(String time) {
                if (selectDateListener != null) {
                    currentDate = time;
                    selectDateListener.onSelectDate(time);
                }
                currentTime = time;
                String times = ViewHelper.convertStrToFormatDateStr(time + " 00:00:00", "yyyy-MM-dd");
                tv_current_time.setText(times);
                keyMap.put("beginTime", currentTime + " 00:00:00");
                keyMap.put("endTime", currentTime + " 23:59:59");
                pageIndex = 1;
                getTaskList();
            }
        });
        weekCalendar.setOnPageChangedListener(new WeekCalendar.OnPageChangedListener() {
            @Override
            public void onPageChange(List<CalendarData> dataList) {
                getTaskData(dataList.get(0).getDateStr(), dataList.get(dataList.size() - 1).getDateStr());
            }
        });

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                getTaskList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                pageIndex = 1;
                getTaskList();
            }
        });
    }

    /**
     * 1?????????????????????
     * 2?????????????????????
     * 3?????????????????????
     */
    private void initDemand() {
        currentUrl = Global.BASE_JAVA_URL + GlobalMethord.????????????;
        demand = new Demand<Task>(Task.class);
        demand.pageSize = 20;
        demand.dictionaryNames = "creatorId.base_staff,executorIds.base_staff";
        keyMap = new HashMap<>();
        keyMap.put("index", "myTask");
        keyMap.put("isSort", "1");
        keyMap.put("userId", mContext.currentUser.getUuid());
        keyMap.put("isShowAllHistory", "true");
        keyMap.put("isMyTask", "true");

        /**
         * ???????????????????????????
         */
        String startTime = weekCalendar.getCurrentWeekDatas().get(0).getDateStr() + " 00:00:00";
        String endTime = weekCalendar.getCurrentWeekDatas().get(6).getDateStr() + " 23:59:59";
        keyMap.put("beginTime", startTime);
        keyMap.put("endTime", endTime);
        demand.keyMap = keyMap;
    }

    /**
     * ???????????????????????????
     */
    public void getTaskList() {
        getTaskList(null);
    }

    /**
     * ???????????????????????????
     */
    private void getTaskList(Latest latest) {
        demand.src = currentUrl;
        demand.keyMap = keyMap;
        ProgressDialogHelper.show(getActivity());
        demand.pageIndex = pageIndex;
        demand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
//                taskList = demand.data;
//                List<Task> tasks = new ArrayList<>();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                for (final Task task : demand.data) {
                    try {
                        task.setCreatorName(demand.getDictName(task, "creatorId"));
                        task.setExecutorNames(demand.getDictName(task, "executorIds"));
                        task.setParticipantNames(helper.getUserNamesById(task.getParticipantIds()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*if (task != null) {
                        //?????????????????????????????????
                        if (TaskStatusEnum.?????????.getName().equals(task.getStatus())) {
                            if (isInThisWeek(task.getBeginTime())) {
                                tasks.add(task);
                            }
                        } else {
                            tasks.add(task);
                        }
                    }*/
                }
                if (pageIndex == 1) {
                    adapter = getAdapter(demand.data);
                    lv.setAdapter(adapter);
                    refreshLayout.resetNoMoreData();
                } else {
                    adapter.addBottom(demand.data, false);
                    if (demand.data != null && demand.data.size() == 0) {
                        refreshLayout.finishLoadMoreWithNoMoreData();//???????????????????????????????????????
                        Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
//                    lv.loadCompleted();
                }
                pageIndex += 1;


                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            Intent intent = new Intent(getActivity(), TaskInfoActivityNew.class);
                            Task task = adapter.getDataList().get(position - 1);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("taskInfo", task);
                            intent.putExtra("taskIntentInfo", bundle);
                            startActivity(intent);
                        }
                    }
                });
                if (latest != null) {
                    initStaffHeader(latest);
                } else {
                    initStaffHeader();
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponseCodeErro(String result) {
                ProgressDialogHelper.dismiss();
                if (latest != null) {
                    initStaffHeader(latest);
                }
            }
        });
    }

    private void initStaffHeader() {
        initStaffHeader(null);
    }


    /**
     * ????????????????????????
     *
     * @param time
     * @return
     */
    private boolean isInThisWeek(String time) {
        List<String> weeks = ViewHelper.getWeeks(new Date());
        try {
            return DateTimeUtil.yearMonthBetween(time, weeks.get(0), weeks.get(6));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ??????????????????????????????
     */
    private void initStaffHeader(Latest latest) {
        Latest mine = ViewHelper.turnLatest(Global.mUser);
        recentList.clear();
        try {
            Dao<Latest, Integer> latestDao = dataHelper.getLatestDao();
            List<Latest> latests = latestDao.queryForAll();
            for (int i = 0; i < latests.size(); i++) { //????????????????????????
                Latest lat = latests.get(i);
                if (!removeDuplicate(lat)) {
                    if (latest != null && latest.getUuid().equals(lat.getUuid())) {
                        //????????????????????????latest?????? ?????????????????? ??????????????????
                    }
//                    else if (lat.getUuid().equals(currentUser.getUuid())){
//                        //????????????????????? ?????????????????? ??????????????????
//                    }
                    else {
                        if (lat.getUuid().equals(mine.getUuid())) {
                            //???????????????????????? ??????????????????
                        } else {
                            recentList.add(lat);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (latest != null) { //????????????????????????
            recentList.add(0, latest);
        }
//        if (!removeDuplicate(currentLatest)) {
//            recentList.add(0, currentLatest);
//        }
        if (!removeDuplicate(mine)) {
            recentList.add(0, mine);
        }
//        generationTaskCompletionView(recentList, latest);
    }

    //???list??????????????????????????????list.contain()??????????????????????????????????????????list?????????
    public boolean removeDuplicate(Latest user) {
        boolean isRePeat = false;
        for (Latest user1 : recentList) {
            if (user.getUuid().equals(user1.getUuid())) {
                isRePeat = true;
                break;
            }
        }
        return isRePeat;
    }

    private CommanAdapter<Task> getAdapter(List<Task> taskList) {
        return new CommanAdapter<Task>(taskList, getActivity(), R.layout.item_task_list_new) {

            @Override
            public void convert(int position, final Task item, final BoeryunViewHolder viewHolder) {
                viewHolder.getView(R.id.ll_task_time).setVisibility(View.VISIBLE);
                TextView time = viewHolder.getView(R.id.time_item_task_list);
                TextView tvContent = viewHolder.getView(R.id.task_content);


                time.setText(DateTimeUtil.dateformatTime(ViewHelper.formatStrToDateAndTime(item.getBeginTime()), false));

                if (!item.getCreatorId().equals(item.getExecutorIds())) {
                    viewHolder.setTextValue(R.id.assign_item_task_list, item.getCreatorName() + "???");
                } else {
                    viewHolder.setTextValue(R.id.assign_item_task_list, "");
                }
                viewHolder.setTextValue(R.id.tv_creater_task_item, item.getCreatorName());
                tvContent.setText(item.getContent());
                final ImageView ivStatus = viewHolder.getView(R.id.task_status);
                /**
                 * ??????????????????????????????????????????
                 */
                if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_finish);
                    tvContent.setTextColor(getResources().getColor(R.color.text_time));
                    time.setTextColor(getResources().getColor(R.color.text_time));
                } else {
                    ivStatus.setImageResource(R.drawable.icon_status_);
                    tvContent.setTextColor(getResources().getColor(R.color.black));

                    //???????????????????????????????????????0???
                    //???????????????????????????????????? -1???
                    //???????????????????????????????????? 1???
                    if (DateTimeUtil.compareDateByDay(ViewHelper.formatStrToStr(item.getEndTime(), "yyyy-MM-dd"),
                            ViewHelper.getDateToday()) == -1) {
                        //?????????????????????????????? ??????????????????
                        time.setTextColor(Color.parseColor("#dc1414"));
                    } else {
                        //?????????????????????????????? ??????????????????
                        time.setTextColor(getResources().getColor(R.color.black));
                    }
                }
                ivStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TaskStatusEnum.?????????.getName().equals(item.getStatus())
                                || TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                            saveTask(item, 1);
                        } else if (TaskStatusEnum.?????????.getName().equals(item.getStatus())) {
                            saveTask(item, 3);
                        } else {
                            Toast.makeText(getActivity(), "?????????????????????????????????????????????!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "????????????????????????!", Toast.LENGTH_SHORT).show();
                mContext.refreshList(false);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(getActivity(), JsonUtils.pareseMessage(result), Toast.LENGTH_SHORT);
            }
        });
    }


    public void getFilterList(TaskFilter filter) {

        keyMap.put("status", filter.getStatus());
        keyMap.put("projectId", filter.getProjectId());
        if (!TextUtils.isEmpty(filter.getUserId())) {
            if ("????????????".equals(filter.getUserId()) || "????????????".equals(filter.getUserId())) {
                keyMap.put("creatorId", mContext.currentUser.getUuid());
                keyMap.put("isMyTask", "true");
            } else if ("????????????".equals(filter.getUserId()) || "????????????".equals(filter.getUserId())) { //?????????????????????
                keyMap.remove("isMyTask");
                keyMap.remove("creatorId");
            } else { //??????
                keyMap.put("isMyTask", "true");
            }
        } else {
            keyMap.put("isMyTask", "true");
        }
        pageIndex = 1;
        getTaskData(weekCalendar.getCurrentWeekDatas().get(0).getDateStr(),
                weekCalendar.getCurrentWeekDatas().get(6).getDateStr());
        getTaskList();
    }

    /**
     * ??????????????????
     *
     * @param user
     */
    public void refreshUserList(User user) {
        weekCalendar.clearSelectedData();
        tvAllTask.setBackgroundResource(R.drawable.tv_tag_bg_selected);
        pageIndex = 1;
        keyMap.put("userId", mContext.currentUser.getUuid());
        getTaskList();
    }

    @Override
    public void onDestroy() {
        PreferceManager.getInsance().saveValueBYkey(TASK_LIST_VIEW_CHECK, "");
        super.onDestroy();
    }


    public interface onSelectDateListener {
        void onSelectDate(String date);
    }

    public void setOnSelectDateListener(onSelectDateListener listener) {
        this.selectDateListener = listener;
    }
}
