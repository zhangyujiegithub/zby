package com.biaozhunyuan.tianyi.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.common.base.ParseException;
import com.biaozhunyuan.tianyi.common.CustomDayView;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.InputSoftHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.utils.HolidayUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.helper.SharedPreferencesHelper;
import com.biaozhunyuan.tianyi.view.BaseSelectPopupWindow;
import com.biaozhunyuan.tianyi.view.ListRecentlySelectedStaffView;
import com.biaozhunyuan.tianyi.view.VoiceInputView;
import com.biaozhunyuan.tianyi.view.bragboard.PagerRecyclerView;
import com.biaozhunyuan.tianyi.view.bragboard.adapter.HorizontalAdapter;
import com.biaozhunyuan.tianyi.view.bragboard.model.DragColumn;
import com.biaozhunyuan.tianyi.view.dateDialog.dialog.DateChooseWheelViewDialog;
import com.biaozhunyuan.tianyi.widget.TextEditTextView;
import com.ldf.calendar.Utils;
import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription ?????????????????????
 * @usage null
 */
public class ColumnAdapter extends HorizontalAdapter<ColumnAdapter.ViewHolder> {

    private Context mContext;
    private String workTaskPanelIid;
    private TaskLaneTrelloActivity activity;
    private PagerRecyclerView recyclerView;
    private BaseSelectPopupWindow popWiw;
    private String currentId = Global.mUser.getUuid();
    private Handler handler = new Handler();
    private SharedPreferencesHelper preferencesHelper;
    private CalendarViewAdapter calendarAdapter;
    private VoiceInputView inputView;
    private View placeholderView;
    private ArrayList<Calendar> currentCalendars = new ArrayList<Calendar>();
    private ListRecentlySelectedStaffView staffView;
    private String selectDate = ViewHelper.getDateToday();
    private CalendarDate selectCalendarDate;  //???????????????
    private ListRecentlySelectedStaffView topStaffView;


    private String currentWorkLaneId = "";
    private List<WorkScheduleList> currentItemList;
    private ItemAdapter currentItemAdapter;


    private DateChooseWheelViewDialog endDateChooseDialog;
    private BaseSelectPopupWindow addTaskPop; //???????????????popupwindow
    private BaseSelectPopupWindow executorPop;//??????????????????popupwindow
    private BaseSelectPopupWindow calendarPop;//???????????????popupwindow


    public ColumnAdapter(Context context) {
        super(context);
        this.mContext = context;
        preferencesHelper = new SharedPreferencesHelper(mContext);
        activity = (TaskLaneTrelloActivity) mContext;
        recyclerView = activity.dragBoardView.getRecyclerView();
        this.workTaskPanelIid = activity.getBoardUuid();
    }

    @Override
    public boolean needFooter() {
        return true;
    }

    @Override
    public int getContentLayoutRes() {
        return R.layout.recyclerview_item_entry;
    }

    @Override
    public int getFooterLayoutRes() {
        return R.layout.recyclerview_footer_addlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(View parent, int viewType) {
        return new ViewHolder(parent, viewType);
    }

    @Override
    public void onBindContentViewHolder(final ViewHolder holder, DragColumn dragColumn, int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                dragCol(holder); ??????????????????????????????
                return true;
            }
        });
        List<DragColumn> oaWorkLaneList = getData();
        final OaWorkLaneList oaWorkLane = (OaWorkLaneList) dragColumn;
        holder.tv_title.setText(oaWorkLane.getTitle());
        final List<WorkScheduleList> itemList = oaWorkLane.getItemList();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.rv_item.setLayoutManager(layoutManager);
        final ItemAdapter itemAdapter = new ItemAdapter(mContext, dragHelper, oaWorkLaneList);
        itemAdapter.setData(itemList);
        holder.rv_item.setAdapter(itemAdapter);
        holder.tv_title.setOnClickListener(new View.OnClickListener() { //???????????? ??????????????? ???????????????
            @Override
            public void onClick(View v) {
                if (holder.et_title.getVisibility() == View.GONE) {
                    holder.et_title.setVisibility(View.VISIBLE);
                    holder.et_title.requestFocus();
                    holder.tv_title.setVisibility(View.GONE);
                    holder.et_title.setHint(oaWorkLane.getTitle());
                    InputSoftHelper.ShowKeyboard(holder.et_title);
                }
            }
        });

        holder.et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() { //???????????????????????????
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //????????????????????????
                } else {
                    //??????????????????????????? ?????????????????? ???????????? ?????????
                    holder.et_title.setVisibility(View.GONE);
                    holder.tv_title.setVisibility(View.VISIBLE);
                    String title = holder.et_title.getText().toString();
                    if (!TextUtils.isEmpty(title.trim())) {
                        updateLaneColumnTitle(oaWorkLane.getUuid(), title, holder.tv_title);
                        holder.et_title.setText("");
                    }
                }
            }
        });

        holder.et_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) { //??????????????????????????? ???EditText????????????
                    holder.et_title.clearFocus();
                    InputSoftHelper.hiddenSoftInput(mContext, holder.et_title);
                }
                return false;
            }
        });
        holder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.et_title.clearFocus();
                InputSoftHelper.hiddenSoftInput(mContext, holder.et_title);
            }
        });
        holder.col_content_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.col_content_container.setFocusable(true);
                holder.col_content_container.setFocusableInTouchMode(true);
                holder.col_content_container.requestFocus();
                InputSoftHelper.hideKeyboard(holder.et_title);
            }
        });

        holder.add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWorkLaneId = oaWorkLane.getUuid();
                currentItemList = itemList;
                currentItemAdapter = itemAdapter;
                showNewTaskCardPopWindow();
            }
        });
        holder.title_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(holder.title_icon, oaWorkLane, position);
            }
        });
    }


    @Override
    public void onBindFooterViewHolder(final ViewHolder holder, int position) {
        holder.add_subPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add_subPlan.setVisibility(View.GONE);
                holder.edit_sub_plan.setVisibility(View.VISIBLE);
            }
        });
        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add_subPlan.setVisibility(View.VISIBLE);
                holder.edit_sub_plan.setVisibility(View.GONE);
            }
        });
        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //????????????
                String name = holder.editText.getText().toString();
                if (!TextUtils.isEmpty(name.trim())) {
                    InputSoftHelper.hideKeyboard(holder.editText);
                    holder.add_subPlan.setVisibility(View.VISIBLE);
                    holder.edit_sub_plan.setVisibility(View.GONE);
                    holder.editText.setText("");
                    addLaneColumn(workTaskPanelIid, name);
                } else {
                    Toast.makeText(mContext, "????????????????????????!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ViewHolder extends HorizontalAdapter.ViewHolder {

        RelativeLayout col_content_container;
        ImageView title_icon;
        ImageView iv_icon;
        TextView tv_title;
        RecyclerView rv_item;
        RelativeLayout add_task;
        TextEditTextView et_title;

        RelativeLayout add_subPlan;
        RelativeLayout edit_sub_plan;
        Button btn_cancel;
        Button btn_ok;
        EditText editText;

        public ViewHolder(View convertView, int itemType) {
            super(convertView, itemType);
        }

        @Override
        public RecyclerView getRecyclerView() {
            return rv_item;
        }

        @Override
        public void findViewForContent(View convertView) {
            col_content_container = convertView.findViewById(R.id.col_content_container);
            title_icon = convertView.findViewById(R.id.title_icon);
            iv_icon = convertView.findViewById(R.id.iv_icon);
            tv_title = convertView.findViewById(R.id.tv_title);
            et_title = convertView.findViewById(R.id.et_update_title);
            rv_item = convertView.findViewById(R.id.rv);
            add_task = convertView.findViewById(R.id.add);
            RefreshLayout refreshLayout = convertView.findViewById(R.id.refreshLayout);
            refreshLayout.setReboundDuration(200);//??????????????????????????????
            refreshLayout.setDragRate(0.5f);//??????????????????/????????????????????????=????????????
            refreshLayout.setEnableLoadMore(false);//??????????????????????????????
            refreshLayout.setEnableRefresh(false);//??????????????????????????????
            refreshLayout.setEnableOverScrollDrag(true);//?????????????????????????????????????????????1.0.4
        }

        @Override
        public void findViewForFooter(View convertView) {
            add_subPlan = convertView.findViewById(R.id.add_sub_plan);
            edit_sub_plan = convertView.findViewById(R.id.edit_sub_plan);
            btn_cancel = convertView.findViewById(R.id.add_cancel);
            btn_ok = convertView.findViewById(R.id.add_ok);
            editText = convertView.findViewById(R.id.add_et);
        }
    }

    private void showPopupWindow(View view, OaWorkLaneList oaWorkLane, int position) {
        PopupWindow mPopupWindow = new PopupWindow(mContext);
        // ??????????????????
        mPopupWindow.setContentView(LayoutInflater.from(mContext).inflate(R.layout.pop_task_trello_info, null));
        // ????????????????????????????????????????????????????????????????????????
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // ??????pop????????????
        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // ??????pop????????????????????????false?????????????????????????????????Activity?????????pop??????Editor?????????focusable????????????true
        mPopupWindow.setFocusable(true);
        // ??????pop???????????????false??????????????????????????????true
        mPopupWindow.setTouchable(true);
        // ????????????pop????????????????????????false??????focusable???true???????????????????????????
        mPopupWindow.setOutsideTouchable(true);
        // ????????? + ??????????????????????????????????????????
        int width = ViewHelper.px2dip(mContext, ViewHelper.getScreenWidth(mContext) / 2 + 300);
        mPopupWindow.showAsDropDown(view, -width, 0);
        TextView tv_delete = mPopupWindow.getContentView().findViewById(R.id.tv_talk_only);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLaneColumn(oaWorkLane, position);
                getData().remove(position);
                recyclerView.setFocusable(false);
                notifyDataSetChanged();
                mPopupWindow.dismiss();
            }
        });

    }


    /**
     * ????????????????????????
     */
    private void showNewTaskCardPopWindow() {


        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (addTaskPop == null) {
            addTaskPop = new BaseSelectPopupWindow(mContext, R.layout.pop_add_task);
            // popWiw.setOpenKeyboard(true);
            addTaskPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            addTaskPop.setFocusable(true);
            addTaskPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            addTaskPop.setShowTitle(false);
            addTaskPop.setBackgroundDrawable(new ColorDrawable(0));

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
                voiceBtn.setBackground(mContext.getResources().getDrawable(R.drawable.ico_add_task_keyboard));
                inputView.setVisibility(View.VISIBLE);
                InputSoftHelper.hideKeyboard(edt);
            } else {
                voiceBtn.setBackground(mContext.getResources().getDrawable(R.drawable.ico_add_task_voice));
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
                    if (getCurrentInputType()) {
                        preferencesHelper.putBooleanValue("isVoiceOpen", false);
                        voiceBtn.setBackground(mContext.getResources().getDrawable(R.drawable.ico_add_task_voice));
                        inputView.setVisibility(View.GONE);
                        InputSoftHelper.ShowKeyboard(edt);
                    } else {
                        preferencesHelper.putBooleanValue("isVoiceOpen", true);
                        voiceBtn.setBackground(mContext.getResources().getDrawable(R.drawable.ico_add_task_keyboard));
                        inputView.setVisibility(View.VISIBLE);
                        InputSoftHelper.hideKeyboard(edt);
                    }

                }
            });
            edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voiceBtn.setBackground(mContext.getResources().getDrawable(R.drawable.icon_add_task_voice));
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
                            addTaskCard(recyclerView, content, currentWorkLaneId, currentItemList, currentItemAdapter);
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
                        addTaskCard(recyclerView, content, currentWorkLaneId, currentItemList, currentItemAdapter);
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
                    showSelectCalendar();
                }
            });

            addTaskPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1f;
                    activity.getWindow().setAttributes(lp);
                    InputSoftHelper.hideKeyboard(tvDate);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
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
            executorPop = new BaseSelectPopupWindow(mContext, R.layout.pop_add_task_executor);
            executorPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            executorPop.setFocusable(true);
            executorPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            executorPop.setShowTitle(false);
            executorPop.setBackgroundDrawable(new ColorDrawable(0));

            topStaffView = executorPop.getContentView().findViewById(R.id.staff_view);

            topStaffView.setOnKeyBoardSelectedUserListener(new ListRecentlySelectedStaffView.OnKeyBoardSelectedUserListener() {
                @Override
                public void onSelected(User user) {
                    currentId = user.getUuid();
                    executorPop.dismiss();
                    if (InputSoftHelper.isSoftShowing(activity)) {
                        InputSoftHelper.hideShowSoft(mContext);
                    }
                }
            });

            executorPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!getCurrentInputType()) {
                        placeholderView.setVisibility(GONE);
                        InputSoftHelper.hideShowSoft(mContext);
                    }
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        if (!getCurrentInputType()) {
            placeholderView.setVisibility(VISIBLE);
        }
        executorPop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_menu_home, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * ???????????? ????????????
     */
    @SuppressLint("WrongViewCast")
    private void showSelectCalendar() {
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
                    selectDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
                    calendarPop.dismiss();
                }

                @Override
                public void onSelectOtherMonth(int offset) {
                    //????????? -1????????????????????????????????? ??? 1?????????????????????????????????
                    monthPager.selectOtherMonth(offset);
                }
            };
            monthPager.setViewheight(Utils.dpi2px(mContext, 270));
            calendarAdapter = new CalendarViewAdapter(
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
                    selectDate = ViewHelper.getDateToday();
                    tvToday.setTextColor(Color.parseColor("#5B8AF2"));
                    tvToday.setBackground(mContext.getResources().getDrawable(R.drawable.tv_bg_home_task_left_select));
                    tvTomorrow.setTextColor(Color.parseColor("#78787C"));
                    tvTomorrow.setBackground(mContext.getResources().getDrawable(R.drawable.tv_bg_home_task_right));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);
                    calendarAdapter.notifyDataChanged(new CalendarDate());
                }
            });
            tvTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //??????
                    selectDate = ViewHelper.getDateTomorrow();
                    tvTomorrow.setTextColor(Color.parseColor("#5B8AF2"));
                    tvTomorrow.setBackground(mContext.getResources().getDrawable(R.drawable.tv_bg_home_task_right_select));
                    tvToday.setTextColor(Color.parseColor("#78787C"));
                    tvToday.setBackground(mContext.getResources().getDrawable(R.drawable.tv_bg_home_task_left));
                    calendarPop.dismiss();
                    cancelSelectState(calendarAdapter);
                    //?????????????????????
                    String date = ViewHelper.getDateTomorrow();
                    CalendarDate calendarDate = new CalendarDate(
                            ViewHelper.getYearByDate(date),
                            ViewHelper.getMonthByDate(date),
                            ViewHelper.getDayByDate(date));
                    calendarAdapter.notifyDataChanged(calendarDate);
                }
            });
            calendarPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
            calendarPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (!getCurrentInputType()) {
                        InputSoftHelper.hideShowSoft(mContext);
                    }
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        calendarPop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_tasklane_trello, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);


        //??????????????????????????????????????????????????????????????????????????????????????????
        if (selectCalendarDate != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    calendarAdapter.notifyDataChanged(selectCalendarDate);
                    selectCalendarDate = null;
                }
            });
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return true ?????????????????? false ??????????????????
     */
    public boolean getCurrentInputType() {
        return inputView.getVisibility() == VISIBLE;
    }

    /**
     * ????????????
     *
     * @param oaWorkLane ?????????uuid
     * @param position   ???????????????
     */
    private void deleteLaneColumn(OaWorkLaneList oaWorkLane, int position) {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????????????????? + oaWorkLane.getUuid();
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                List<DragColumn> data = getData();
                if (position >= data.size()) {
                    data.add(oaWorkLane);
                } else {
                    data.add(position, oaWorkLane);
                }
                recyclerView.setFocusable(false);
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onResponseCodeErro(String result) {
                List<DragColumn> data = getData();
                if (position >= data.size()) {
                    data.add(oaWorkLane);
                } else {
                    data.add(position, oaWorkLane);
                }
                recyclerView.setFocusable(false);
                notifyDataSetChanged();
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                ProgressDialogHelper.dismiss();
            }
        });
    }

    /**
     * ????????????
     *
     * @param oaWorkTaskPanelIid ?????????uuid
     * @param title              ????????????
     */
    private void addLaneColumn(String oaWorkTaskPanelIid, String title) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????????????????????
                + "?oaWorkTaskPanelIid=" + oaWorkTaskPanelIid + "&title=" + title + "&type=1";
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                String data = JsonUtils.pareseData(response);
                try {
                    String title1 = JsonUtils.getStringValue(data, "title");
                    String oaWorkTaskPanelIid = JsonUtils.getStringValue(data, "oaWorkTaskPanelIid");
                    String uuid = JsonUtils.getStringValue(data, "uuid");
                    recyclerView.setFocusable(false);
                    appendNewColumn(new OaWorkLaneList(
                            title1,
                            oaWorkTaskPanelIid,
                            "1",
                            uuid,
                            new ArrayList<WorkScheduleList>()), recyclerView);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
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
     * ??????????????????
     *
     * @param msg            ????????????
     * @param oaWorkLaneUuid ?????????uuid
     */
    private void addTaskCard(RecyclerView itemRecyclerView, String msg, String oaWorkLaneUuid, List<WorkScheduleList> itemList, ItemAdapter itemAdapter) {
        ProgressDialogHelper.show(mContext).show();
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????????????????????????????
                + "?content=" + msg + "&oaWorkLaneUuid=" + oaWorkLaneUuid + "&executorIds=" + currentId
                + "&beginTime=" + selectDate + " 00:00:00" + "&endTime=" + selectDate + " 23:59:59";
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    WorkScheduleList workScheduleList = JsonUtils.jsonToEntity(JsonUtils.pareseData(response), WorkScheduleList.class);
                    if (workScheduleList != null) {
                        recyclerView.setFocusable(false);
                        itemList.add(0, workScheduleList);
                        itemAdapter.notifyItemInserted(0);
                        itemRecyclerView.scrollToPosition(0);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                }
                ProgressDialogHelper.dismiss();
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                ProgressDialogHelper.dismiss();
            }
        });
    }

    /**
     * ????????????title
     *
     * @param uuid
     * @param title
     */
    private void updateLaneColumnTitle(String uuid, String title, TextView textView) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????????????????????
                + "?uuid=" + uuid + "&title=" + title;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    String title1 = JsonUtils.getStringValue(JsonUtils.pareseData(response), "title");
                    textView.setText(title1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
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

}


