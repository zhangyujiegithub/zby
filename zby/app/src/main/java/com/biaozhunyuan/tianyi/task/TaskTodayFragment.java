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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.CustomDayView;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.Task;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.common.utils.HolidayUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.view.BaseSelectPopupWindow;
import com.chy.srlibrary.SwipeMenu;
import com.chy.srlibrary.SwipeMenuItem;
import com.chy.srlibrary.interfaceutil.SwipeMenuCreatorInterfaceUtil;
import com.chy.srlibrary.slistview.SMListView;
import com.chy.srlibrary.slistview.SMRListView;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 任务:今日任务列表
 */

public class TaskTodayFragment extends Fragment {

    private Demand<Task> demand;
    private CommanAdapter<Task> adapter;
    private SMRListView lv;
    public static boolean isResume = false;
    private TaskListActivityNew mContext;
    public int pageIndex = 1;
    private String currentTime = "";
    private SmartRefreshLayout refreshLayout;
    private BaseSelectPopupWindow calendarPop;
    private String currentUrl = "";
    private String currentUserId;
    private Map<String, String> keyMap;
    private List<Task> doneTasks = new ArrayList<>();
    private List<Task> unFinishedTasks = new ArrayList<>();
    private Task currentSelectItem; //当前侧滑选中的条目


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_today_view, null);
        initView(view);
        initData();
        initDemand();
        getTaskList();
        setOnTouchEvent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isResume) {
            isResume = false;
            pageIndex = 1;
            getTaskList();
        }
    }

    private void initData() {
        mContext = (TaskListActivityNew) getActivity();
        currentTime = ViewHelper.getDateToday();
        refreshLayout.setReboundDuration(200);//回弹动画时长（毫秒）
        refreshLayout.setDragRate(0.7f);//显示下拉高度/手指真实下拉高度=阻尼效果
        refreshLayout.setEnableLoadMore(true);//是否启用上拉加载功能
        refreshLayout.setEnableRefresh(true);//是否启用下拉刷新功能
        refreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        refreshLayout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
    }

    /**
     * 获取列表信息并展示
     */
    public void getTaskList() {
        demand.src = currentUrl;
        demand.pageIndex = pageIndex;
        if (pageIndex == 1) {
            doneTasks.clear();
            unFinishedTasks.clear();
            ProgressDialogHelper.show(mContext);
        }
        demand.init(new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                List<Task> taskList = new ArrayList<>();
                for (final Task task : demand.data) {
                    try {
                        task.setCreatorName(demand.getDictName(task, "creatorId"));
                        task.setExecutorNames(demand.getDictName(task, "executorIds"));
//                        task.setCustomerName(demand.getDictName(task, "customerId"));
                        task.setParticipantNames(new DictionaryHelper(mContext).getUserNamesById(task.getParticipantIds()));
                        task.setLike(task.isLike());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (demand.data.size() > 0) {
                    for (Task task : demand.data) {
                        if (TaskStatusEnum.已完成.getName().equals(task.getStatus())
                                || TaskStatusEnum.已取消.getName().equals(task.getStatus())) {
                            doneTasks.add(task);
                        } else {
                            unFinishedTasks.add(task);
                        }
                    }
                }
                taskList.addAll(unFinishedTasks);
                taskList.addAll(doneTasks);



                if (demand.pageIndex == 1) {
                    adapter = getTaskAdapter(taskList);
                    lv.setAdapter(adapter);
                    refreshLayout.resetNoMoreData();
                } else {
                    adapter.changeData(taskList);
                    if (demand.data != null && demand.data.size() == 0) {
                        refreshLayout.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据
                    }
                }
                pageIndex += 1;
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


    private CommanAdapter<Task> getTaskAdapter(List<Task> taskList) {
        return new CommanAdapter<Task>(taskList, getActivity(), R.layout.item_task_list_new) {

            @Override
            public void convert(int position, final Task item, final BoeryunViewHolder viewHolder) {
                viewHolder.setTextValue(R.id.tv_creater_task_item, item.getCreatorName());
                viewHolder.setTextValue(R.id.task_content, item.getContent());
                if (!item.getCreatorId().equals(item.getExecutorIds())) {
                    viewHolder.setTextValue(R.id.assign_item_task_list, item.getCreatorName() + "→");
                } else {
                    viewHolder.setTextValue(R.id.assign_item_task_list, "");
                }


                TextView tvContent = viewHolder.getView(R.id.task_content);
                tvContent.setText(item.getContent());
                final ImageView ivStatus = viewHolder.getView(R.id.task_status);
                /**
                 * 根据任务状态枚举类型显示状态
                 */
                if (TaskStatusEnum.已完成.getName().equals(item.getStatus())) {
                    ivStatus.setImageResource(R.drawable.icon_status_finish);
                    tvContent.setTextColor(getResources().getColor(R.color.text_time));
                } else {
                    ivStatus.setImageResource(R.drawable.icon_status_);
                    tvContent.setTextColor(getResources().getColor(R.color.black));
                }

                ivStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TaskStatusEnum.进行中.getName().equals(item.getStatus()) ||
                                TaskStatusEnum.已逾期.getName().equals(item.getStatus())) {
                            saveTask(item, 1);
                        } else if (TaskStatusEnum.已完成.getName().equals(item.getStatus())) {
                            saveTask(item, 3);
                        } else {
                            Toast.makeText(getActivity(), "当前任务状态下不能修改任务状态!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    /**
     * 完成任务
     *
     * @param status 任务状态：1==已完成，3==未完成
     */
    private void saveTask(Task task, int status) {

        String url = Global.BASE_JAVA_URL + GlobalMethord.改变任务状态 + "?uuid=" + task.getUuid() + "&ticket=" + status;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                adapter.notifyDataSetChanged();
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
     * 添加侧滑事件
     */
    private void initSMListView() {
        final SwipeMenuCreatorInterfaceUtil creator = new SwipeMenuCreatorInterfaceUtil() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#EC4D3D")));
                deleteItem.setWidth(250);
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.parseColor("#ffffff"));
                deleteItem.setTitleSize(14);
                menu.addMenuItem(deleteItem);

                SwipeMenuItem dateItem = new SwipeMenuItem(mContext);
                dateItem.setBackground(new ColorDrawable(Color.parseColor("#417EBF")));
                dateItem.setWidth(250);
                dateItem.setTitle("日期");
                dateItem.setTitleColor(Color.parseColor("#ffffff"));
                dateItem.setTitleSize(14);
                menu.addMenuItem(dateItem);
            }
        };
        // 设置侧滑删除(如果不设置则不会有侧滑删除，仅有刷新)
        lv.setMenuCreator(creator);
        // 点击侧滑按钮的响应事件（如果涉及自定义的侧滑布局，可参考SwipMenuListView的实现方法）
        // 侧滑的监听事件
        lv.setOnMenuItemClickListener(new SMListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int i, SwipeMenu swipeMenu, int i1) {
                Task item = adapter.getItem(i);
                currentSelectItem = item;
                if (item.getCreatorId().equals(Global.mUser.getUuid())) {
                    switch (i1) {
                        case 0: //删除
                            deleteTask(item);
                            break;
                        case 1: //日期
                            showSelectCalendar(item);
                            break;
                    }
                } else {
                    Toast.makeText(mContext, "当前任务不可删除或修改结束时间", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        setListSwipeEnable(lv);
    }

    /**
     * 删除任务
     */
    private void deleteTask(Task item) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.任务看板详情删除卡片;
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
                adapter.remove(item);
                mContext.refreshList(false);
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
     * 显示日历 选择日期
     */
    @SuppressLint("WrongViewCast")
    private void showSelectCalendar(Task item) {

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        if (calendarPop == null) {
            calendarPop = new BaseSelectPopupWindow(mContext, R.layout.pop_add_task_calendar);
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
            MonthPager monthPager = calendarPop.getContentView().findViewById(R.id.calendar_view);
            CustomDayView customDayView = new CustomDayView(mContext, R.layout.custom_day);
            OnSelectDateListener onSelectDateListener = new OnSelectDateListener() {
                @Override
                public void onSelectDate(CalendarDate date) {
                    String selectTime = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                    currentSelectItem.setBeginTime(selectTime);
                    currentSelectItem.setEndTime(selectTime);
                    upDateTask(currentSelectItem);
                    calendarPop.dismiss();
                }

                @Override
                public void onSelectOtherMonth(int offset) {
                    //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
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
                    ArrayList<Calendar> currentCalendars = calendarAdapter.getPagers();
                    if (currentCalendars.get(position % currentCalendars.size()) instanceof Calendar) {
                        CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                        currentData.setText(date.getYear() + "年" + date.getMonth() + "月");
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //向前翻页
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() - 1);
                }
            });
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //向后翻页
                    monthPager.setCurrentItem(monthPager.getCurrentPosition() + 1);
                }
            });
            tvToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //今天
                    item.setBeginTime(ViewHelper.getDateToday());
                    item.setEndTime(ViewHelper.getDateToday());
                    tvToday.setTextColor(Color.parseColor("#5B8AF2"));
                    tvToday.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_left_select));
                    tvTomorrow.setTextColor(Color.parseColor("#78787C"));
                    tvTomorrow.setBackground(getResources().getDrawable(R.drawable.tv_bg_home_task_right));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);

                    currentSelectItem.setBeginTime(ViewHelper.getCurrentTime());
                    currentSelectItem.setEndTime(ViewHelper.getCurrentTime());
                    upDateTask(currentSelectItem);
                }
            });
            tvTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //明天
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
                    mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });

        }

        lp.alpha = 0.65f;
        getActivity().getWindow().setAttributes(lp);
        calendarPop.showAtLocation(getLayoutInflater().inflate(R.layout.fragment_tasklane_trello, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 修改任务时间
     */
    private void upDateTask(Task task) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.任务保存;
        StringRequest.postAsyn(url, task, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                mContext.refreshList(false);
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
     * 如果有侧滑删除事件，则可根据具体条件设置滑动项是否可以侧滑
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
                         * oldPos  滑动的所处位置的position
                         * setSwipeEnable() 是否进行侧滑：
                         *           设置为false则不会发生侧滑；
                         *           设置为true则会侧滑
                         *           默认值为true
                         *
                         * 这里可根据具体的条件来判定是否可以进行滑动
                         */
                        int oldPos = listview.pointToPosition((int) event.getX(), (int) event.getY());
                        if (oldPos < 0) {
                            // 判定当没有找到侧滑的Item时，返回false
                            return false;
                        }
                        Task item = (Task) listview.getAdapter().getItem(oldPos);
                        if (item.getStatus().equals(TaskStatusEnum.已完成.getName()) ||
                                item.getStatus().equals(TaskStatusEnum.已取消.getName())) {
                            // 根据具体条件设置不滑动项
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
     * 日历上选中的日期取消选中
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

    private void setOnTouchEvent() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, TaskInfoActivityNew.class);
                Task task = adapter.getDataList().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskInfo", task);
                intent.putExtra("taskIntentInfo", bundle);
                startActivity(intent);
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

    private void initDemand() {
        demand = new Demand<Task>(Task.class);
        currentUrl = Global.BASE_JAVA_URL + GlobalMethord.任务列表;
        demand.pageSize = 20;
        keyMap = new HashMap<>();
        keyMap.put("beginTime", currentTime + " 00:00:00");
        keyMap.put("endTime", currentTime + " 23:59:59");
        keyMap.put("index", "myTask");
        keyMap.put("userId", Global.mUser.getUuid());
        keyMap.put("isShowAllHistory", "true");
        keyMap.put("isMyTask", "true");
        keyMap.put("isSort", "true");
        demand.keyMap = keyMap;
        demand.dictionaryNames = "creatorId.base_staff,executorIds.base_staff,customerId.crm_customer";
    }

    private void initView(View view) {
        lv = view.findViewById(R.id.lv_task_day_list);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        initSMListView();
    }


    /**
     * 获取过滤的列表
     *
     * @param filter 过滤条件
     */
    public void getFilterList(TaskFilter filter) {
        currentUserId = filter.getUserId();//如果是我创建的自己过滤
        currentUrl = Global.BASE_JAVA_URL + GlobalMethord.任务列表;
        keyMap.put("status", filter.getStatus());
        keyMap.put("projectId", filter.getProjectId());
        if (!TextUtils.isEmpty(filter.getUserId())) {
            if ("我创建的".equals(filter.getUserId())) {
                keyMap.put("creatorId", Global.mUser.getUuid());
                keyMap.put("isMyTask", "true");
            } else if ("指派给我".equals(filter.getUserId())) { //指派给我的任务
                keyMap.remove("isMyTask");
                keyMap.remove("creatorId");
            } else {
                keyMap.put("isMyTask", "true");
            }
        }
        demand.keyMap = keyMap;
        pageIndex = 1;
        getTaskList();
    }
}
