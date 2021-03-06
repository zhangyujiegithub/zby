package com.biaozhunyuan.tianyi.newuihome;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.activity.ActivityListActivity;
import com.biaozhunyuan.tianyi.activity.CaptureActivity;
import com.biaozhunyuan.tianyi.address.AddressListActivity;
import com.biaozhunyuan.tianyi.apply.ApplylistActivity;
import com.biaozhunyuan.tianyi.apply.model.WorkflowInstance;
import com.biaozhunyuan.tianyi.attendance.NewAttendanceActivity;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.helper.Logger;
import com.biaozhunyuan.tianyi.bespoke.BespokeListActivity;
import com.biaozhunyuan.tianyi.buglist.BugListActivity;
import com.biaozhunyuan.tianyi.business.BusinessListActivity;
import com.biaozhunyuan.tianyi.client.ClientListActivity;
import com.biaozhunyuan.tianyi.clue.ClueListActivity;
import com.biaozhunyuan.tianyi.common.model.other.FunctionBoard;
import com.biaozhunyuan.tianyi.contact.ContactRecordListActivity;
import com.biaozhunyuan.tianyi.curriculum.CurriculumlistActivity;
import com.biaozhunyuan.tianyi.dispatch.FawenActivity;
import com.biaozhunyuan.tianyi.examination.ExaminationlistActivity;
import com.biaozhunyuan.tianyi.expenseaccount.ExpenseAccountActivity;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.helper.JsonParser;
import com.biaozhunyuan.tianyi.helper.ParamCallback;
import com.biaozhunyuan.tianyi.common.helper.PreferceManager;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.helper.WebviewNormalActivity;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.indispatch.ShouwenFragment;
import com.biaozhunyuan.tianyi.information.InformationListActivity;
import com.biaozhunyuan.tianyi.invoices.InvoicesListActivity;
import com.biaozhunyuan.tianyi.log.LogListActivity;
import com.biaozhunyuan.tianyi.log.LogNewActivity;
import com.biaozhunyuan.tianyi.common.model.request.Demand;
import com.biaozhunyuan.tianyi.models.EnumFunctionPoint;
import com.biaozhunyuan.tianyi.models.MenuChildItem;
import com.biaozhunyuan.tianyi.models.MenuFunction;
import com.biaozhunyuan.tianyi.notice.NoticeListActivity;
import com.biaozhunyuan.tianyi.notice.NoticeNewActivity;
import com.biaozhunyuan.tianyi.product.ProductListActivity;
import com.biaozhunyuan.tianyi.project.ProjectAttendanceActivity;
import com.biaozhunyuan.tianyi.project.ProjectListActivity;
import com.biaozhunyuan.tianyi.space.SpaceListActivity;
import com.biaozhunyuan.tianyi.task.TaskListActivityNew;
import com.biaozhunyuan.tianyi.task.TaskNewActivity;
import com.biaozhunyuan.tianyi.common.utils.LogUtils;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.utils.ImageUtils;
import com.biaozhunyuan.tianyi.common.utils.InfoUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.utils.ParamUtils;
import com.biaozhunyuan.tianyi.view.BaseSelectPopupWindow;
import com.biaozhunyuan.tianyi.view.DragFloatActionButton;
import com.biaozhunyuan.tianyi.view.NumImageView;
import com.biaozhunyuan.tianyi.view.OnViewGlobalLayoutListener;
import com.biaozhunyuan.tianyi.view.WaveView;
import com.biaozhunyuan.tianyi.view.commonpupupwindow.CommonPopupWindow;
import com.biaozhunyuan.tianyi.wechat.WeChatRecordActivity;
import com.biaozhunyuan.tianyi.workorder.WorkOrderListActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.Request;

import static com.biaozhunyuan.tianyi.common.utils.InfoUtils.getPermissionInfo;

/**
 * ??????(?????????)
 */

public class MenuHomeFragmentNew extends Fragment {

    private Context mContext;//?????????
    private GridView menuGridView; //???????????????Gridview
    private LinearLayout menu_ll; //??????????????? ?????????????????????
    private List<MenuChildItem> itemOAList = new ArrayList<MenuChildItem>(); //oa?????????
    private List<MenuChildItem> lastOAList = new ArrayList<MenuChildItem>(); //oa?????????
    private CommanAdapter<MenuChildItem> menuListAdapter; //??????????????????????????????????????????
    private CommanAdapter<MenuChildItem> menuPopAdapter;  //??????????????????????????????????????????
    private MenuFunction function;
    private Demand approvalDemand;
    private CommonPopupWindow popupWindow; //????????????????????????popupwindow
    private GridView popup_gv; //???????????????????????????Gridview
    private List<FunctionBoard> mLinearLayoutList;
    private View showView;
    private ImageView ivScan; //??????
    private LinearLayout ll_root; //????????????????????? ?????????
    private HomeTaskLayout taskLayout; //???????????? ????????????
    private HomeApplyLayout applyLayout; //???????????? ??????????????????
    private HomeNoticeLayout noticeLayout;//???????????? ????????????
    private int screenHeight; //????????????/2
    private DragFloatActionButton voiceButton; //?????????????????????
    private BaseSelectPopupWindow voicePop; //??????????????????
    private HomeActivity activity;
    private LinearLayout tvRedact; //??????
    private List<H5Menu> h5Menus; //????????????H5???????????????

    private final int REQUEST_SCAN = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, container, false);
        initData();
        initView(view);
        getPermissionInfo();
        initMyApprovalDeamnd();
        ParamUtils.getMenuCountList(mContext);
        getHomeComposeType();
        showDefaultOAFunction();
        setOnTouch();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i(getClass().getName(), "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == REQUEST_SCAN && data != null) {
            String scanCode = data
                    .getStringExtra(CaptureActivity.RESULT_SCAN_CODE);
            if (!TextUtils.isEmpty(scanCode)) {

                Intent intent = new Intent(mContext, WebviewNormalActivity.class);
                if (scanCode.contains("http")) {
                    intent.putExtra(WebviewNormalActivity.EXTRA_URL, scanCode);
                } else {
                    intent.putExtra(WebviewNormalActivity.EXTRA_URL, Global.BASE_JAVA_URL + scanCode);
                }
                startActivity(intent);
            }
        }
    }

    /**
     * ?????????????????? ?????????????????????????????????????????????
     */
    private void getHomeComposeType() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????H5???????????? + "?menuType=menu";
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                mLinearLayoutList.clear();
                h5Menus = JsonUtils.jsonToArrayEntity(JsonUtils.pareseData(response), H5Menu.class);

                ParamUtils.getParam(ParamUtils.MENUHOME_FUNCTION_BOARD, new ParamCallback() {
                    @Override
                    public void onParam(String value) {
                        List<FunctionBoard> functionBoards = new ArrayList<>();
                        try {
                            JSONArray ja = new JSONArray(value);
                            for (int i = 0; i < ja.length(); i++) {
                                FunctionBoard fb = new FunctionBoard();
                                JSONObject jo = ja.getJSONObject(i);
                                fb.setIndex(Integer.parseInt(jo.optString("index")));
                                fb.setFunction(jo.optString("title"));
                                functionBoards.add(fb);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                    List<FunctionBoard> functionBoards = JsonUtils.jsonToArrayEntity(value, FunctionBoard.class);
                        if ("null".equals(value)) {
                            updateDataList(new ArrayList<>());
                        } else {
                            if (functionBoards.size() > 0) {
                                updateDataList(functionBoards);
                            } else {
                                initBoard();
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        initBoard();
                    }
                });

            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                initBoard();
            }
        });
    }

    /**
     * ????????????
     */
    private void updateDataList(List<FunctionBoard> functionBoards) {
        for (FunctionBoard fb : functionBoards) {
            switch (fb.getFunction()) {
                case "??????":
                    addNoticeBoard(fb.getIndex());
                    break;
                case "??????":
                    addApplyBoard(fb.getIndex());
                    break;
                case "??????":
                    addTaskBoard(fb.getIndex());
                    break;
                default:
                    for (int i = 0; i < h5Menus.size(); i++) {
                        if (fb.getFunction().equals(h5Menus.get(i).getTitle())) {
                            addH5Board(h5Menus.get(i).getH5url(), fb.getFunction(), fb.getIndex());
                            break;
                        }
                    }
                    break;
            }
        }
        initParentLayout();
    }

    /**
     * ???????????????????????????
     */
    private void initBoard() {
        initLayout();
        initParentLayout();
    }


    /**
     * ?????????????????????
     */
    private void initLayout() {
        addNoticeBoard(1);
        addApplyBoard(2);
        addTaskBoard(3);
//        addH5Board("https://www.baidu.com/", "??????", 4);
    }

    /**
     * ???????????????layout?????????????????????
     */
    private void initParentLayout() {
        ViewHelper.sortList(mLinearLayoutList);
        String value = ParamUtils.listToJson(mLinearLayoutList);
        if ("[]".equals(value)) {
            value = "null";
        }
        ParamUtils.addParam(ParamUtils.MENUHOME_FUNCTION_BOARD, value);
        ll_root.removeAllViews();
        for (FunctionBoard fb : mLinearLayoutList) {
            ll_root.addView(fb.getLayout());
        }
    }

    /**
     * ??????????????????????????????????????? ????????????????????????
     *
     * @param function
     * @param board
     */
    public void isExistFBandAdd(String function, FunctionBoard board) {
        boolean isRePeat = false;
        for (int i = 0; i < mLinearLayoutList.size(); i++) {
            FunctionBoard fb = mLinearLayoutList.get(i);
            if (function.equals(fb.getFunction())) {
                isRePeat = true;
                fb.setLayout(board.getLayout());
                fb.setIndex(board.getIndex());
            }
        }
        if (!isRePeat) {
            mLinearLayoutList.add(board);
        }
    }

    /**
     * ??????????????????
     */
    private void addTaskBoard(int index) {
        if (taskLayout == null) {
            taskLayout = new HomeTaskLayout(mContext);
        }
        isExistFBandAdd("??????", new FunctionBoard(taskLayout, index, "??????"));
    }

    /**
     * ??????????????????
     */
    private void addApplyBoard(int index) {
        if (applyLayout == null) {
            applyLayout = new HomeApplyLayout(mContext);
        }


        applyLayout.setOnAuditSuccessListener(new HomeApplyLayout.AuditSuccessListener() {
            @Override
            public void onAuditSuccess() {
                getMyApprovalList(); // ??????????????????????????????????????????
            }
        });
        isExistFBandAdd("??????", new FunctionBoard(applyLayout, index, "??????"));
    }

    /**
     * ??????????????????
     */
    private void addNoticeBoard(int index) {
        if (noticeLayout == null) {
            noticeLayout = new HomeNoticeLayout(mContext);
        }
        isExistFBandAdd("??????", new FunctionBoard(noticeLayout, index, "??????"));
    }

    /**
     * ??????H5??????
     */
    private void addH5Board(String url, String title, int index) {
        HomeNormalWebViewLayout webviewLayout = new HomeNormalWebViewLayout(mContext, url, title);
        isExistFBandAdd(title, new FunctionBoard(webviewLayout, index, title));
    }


    public void getAllUser() {
        InfoUtils.getAllStaff(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
//                            getHomeComposeType();
                        if (applyLayout != null)
                            applyLayout.refreshDataList();
                        if (noticeLayout != null) {
                            noticeLayout.refreshData();
                        }
                        return true;
                }
                return false;
            }
        }));
    }

    private void setOnTouch() {
        tvRedact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeBoardSettingActivity.class);
                startActivity(intent);
            }
        });
        menu_ll.setOnClickListener(new View.OnClickListener() { //???????????????????????????
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });
        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuChildItem item = itemOAList.get(position);
                skipMenuItemClick(item);
            }
        });
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoicePop();
            }
        });

        /**
         * ???????????????
         */
        ivScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_SCAN);
            }
        });
    }

    /**
     * ??????????????????
     */
    private void showVoicePop() {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (voicePop == null) {
            voicePop = new BaseSelectPopupWindow(mContext, R.layout.pop_voice_order);
            voicePop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            voicePop.setFocusable(true);
            voicePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            voicePop.setShowTitle(false);
            voicePop.setBackgroundDrawable(new ColorDrawable(0));


            ImageView mIvVoice = voicePop.getContentView().findViewById(R.id.iv_voice_value); //????????????
            WaveView mWaveView = voicePop.getContentView().findViewById(R.id.waveView);  //????????????????????????????????????
            TextView tvInputText = voicePop.getContentView().findViewById(R.id.voice_input); //??????????????????
            SpeechRecognizer mIat = initVoice();
            RecognizerListener mRecoListener = new RecognizerListener() {

                @Override
                public void onVolumeChanged(int i) {
                    if (i == 0) {
                        mIvVoice.setImageResource(R.drawable.icon_voice_value1);
                    } else {
                        int value = i / 5;
                        switch (value) {
                            case 0:
                            case 1:
                                mIvVoice.setImageResource(R.drawable.icon_voice_value1);
                                break;
                            case 2:
                            case 3:
                                mIvVoice.setImageResource(R.drawable.icon_voice_value2);
                                break;
                            case 4:
                            case 5:
                                mIvVoice.setImageResource(R.drawable.icon_voice_value3);
                                break;
                            default:
                                mIvVoice.setImageResource(R.drawable.icon_voice_value4);
                                break;
                        }
                    }
                }

                @Override
                public void onBeginOfSpeech() {

                }

                @Override
                public void onEndOfSpeech() {
                    Logger.i("onResult" + "onEndOfSpeech");
                }

                @Override
                public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {
                    String results = recognizerResult.getResultString();
                    Logger.i("onResult" + results + "");
                    String text = JsonParser.parseIatResult(results);

                    if (tvInputText != null) {
//                        String inputText = tvInputText.getText().toString() + text;

                        if (text.contains("??????")) {
                            if (text.contains("??????")) {
                                startActivity(new Intent(mContext, TaskNewActivity.class));
                            } else if (text.contains("??????")) {
                                startActivity(new Intent(mContext, NoticeNewActivity.class));
                            } else if (text.contains("??????")) {
                                startActivity(new Intent(mContext, LogNewActivity.class));
                            }
                        } else {
                            tvInputText.setText(text);
                        }

                    }
                }

                @Override
                public void onError(SpeechError speechError) {
                    Logger.i(speechError.toString());
                }

                @Override
                public void onEvent(int i, int i1, int i2, String s) {

                }
            };

            mIvVoice.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mWaveView.startAnim(); //????????????
                            mIvVoice.setImageResource(R.drawable.icon_voice_value1);
                            int i = mIat.startListening(mRecoListener);
                            if (i != ErrorCode.SUCCESS) {
                                Logger.d("????????????,?????????: " + i);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            mWaveView.stopAnim();  //????????????
                            mIat.stopListening();
                            mIvVoice.setImageResource(R.drawable.ico_voice_value_default);
                            break;
                    }
                    return true;
                }
            });
            voicePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lp.alpha = 1f;
                    activity.getWindow().setAttributes(lp);
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            });
        }
        lp.alpha = 0.8f;
        activity.getWindow().setAttributes(lp);
        voicePop.showAtLocation(activity.getLayoutInflater().inflate(R.layout.fragment_menu_home, null), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private SpeechRecognizer initVoice() {
        //1.??????SpeechRecognizer?????????????????????????????????????????????InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(getContext(), null);
        //2.??????????????????????????????MSC Reference Manual???SpeechConstant???
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        return mIat;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskLayout != null) {
            taskLayout.refreshList();
        }
        if (applyLayout != null) {
            applyLayout.refreshDataList();
        }
        if (noticeLayout != null) {
            noticeLayout.refreshData();
        }
        getMyApprovalList();
    }

    private void initData() {
        mContext = getActivity();
        activity = (HomeActivity) mContext;
        mLinearLayoutList = new ArrayList<>();
        h5Menus = new ArrayList<>();
        function = new MenuFunction();
        screenHeight = ViewHelper.getScreenHeight(mContext) / 2; //????????????
        EventBus.getDefault().register(this);
        ShortcutBadger.applyCount(mContext, 0);
    }

    /**
     * EventBus????????????
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(BoardMessageEvent messageEvent) {
        List<FunctionBoard> functionList = messageEvent.getFunctionList();
        for (FunctionBoard fb : functionList) { //????????????????????????????????? ?????????????????????????????????
            for (FunctionBoard functionBoard : mLinearLayoutList) {
                if (fb.getFunction().equals(functionBoard.getFunction())) {
//                    functionBoard.setIndex(fb.getIndex());
                    fb.setLayout(functionBoard.getLayout());
                }
            }
        }
        mLinearLayoutList = functionList;
        updateDataList(mLinearLayoutList);
    }


    private void initView(View view) {
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);//??????????????????????????????
        refreshLayout.setEnableLoadMore(false);//??????????????????????????????
        refreshLayout.setEnableOverScrollDrag(true);//????????????????????????

        menuGridView = view.findViewById(R.id.Menu_ListView);
        tvRedact = view.findViewById(R.id.tv_redact);
        menu_ll = view.findViewById(R.id.Menu_ll);
        menu_ll.setMinimumWidth(ViewHelper.getScreenWidth(mContext) / 5);
        showView = view.findViewById(R.id.view);
        ll_root = view.findViewById(R.id.ll_home_root);
        ivScan = view.findViewById(R.id.iv_scan);
        voiceButton = view.findViewById(R.id.voiceButton);
    }

    private void initMyApprovalDeamnd() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;
        approvalDemand = new Demand();
        approvalDemand.pageSize = 999;
        approvalDemand.src = url;
        approvalDemand.pageIndex = 1;
    }

    /**
     * ????????????????????????popupwindow
     */
    private void initPopupWindow() {
        popupWindow = new CommonPopupWindow.Builder(getActivity())
                //??????PopupWindow??????
                .setView(R.layout.popup_home)
                //????????????
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                //????????????
                .setAnimationStyle(R.style.AnimDown)
                //?????????????????????????????????0.0f-1.0f ??????????????? 1.0f?????????
                .setBackGroundLevel(0.65f)
                //??????PopupWindow?????????View???????????????
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        //???gridview??????????????????
                        LinearLayout llGrid = view.findViewById(R.id.ll_grid);
                        llGrid.getViewTreeObserver().addOnGlobalLayoutListener(new OnViewGlobalLayoutListener(llGrid, screenHeight));

                        popup_gv = view.findViewById(R.id.popup_gv);
                        if (menuPopAdapter == null) {
                            menuPopAdapter = getMenuListAdapter(itemOAList);
                        }
                        popup_gv.setAdapter(menuPopAdapter);
                        popup_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                MenuChildItem item = itemOAList.get(position);
                                skipMenuItemClick(item);

                            }
                        });
                    }
                })
                //??????????????????????????? ?????????true
                .setOutsideTouchable(true)
                //????????????
                .create();
        popupWindow.showAsDropDown(showView);
    }

    /**
     * ??????oa???????????????
     */
    private void showDefaultOAFunction() {
        List<EnumFunctionPoint> functionList = function.getDefaultOAFunctions();
//        getDefaultOAFunction(functionList);
        getMunuPoint(functionList);
    }

    private void getMunuPoint(final List<EnumFunctionPoint> list) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????????????????;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    List<FunctionMenu> listName = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(response, "Data"), FunctionMenu.class);
                    if (listName == null || (listName != null && listName.size() == 0)) {
                        Toast toast = Toast.makeText(mContext, "??????????????????,??????PC?????????", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    PreferceManager.getInsance().saveValueBYkey("UserPermission", JsonUtils.pareseData(response));
                    for (int i = 0; i < listName.size(); i++) {
                        for (int j = 0; j < listName.get(i).getChildrenSubmenu().size(); j++) {
                            String name = listName.get(i).getChildrenSubmenu().get(j).getId();
                            switch (name) {
                                case "aa41149b35904c339e543bfc1d61c924": //????????????
                                    list.add(EnumFunctionPoint.ATTANCE);
                                    break;
                                case "d27c1cac654e11e7a75b001517543a38": //????????????
                                    list.add(EnumFunctionPoint.LOG);
                                    break;
                                case "d5d3fda3654e11e7a75b001517543a38": //????????????
                                    list.add(EnumFunctionPoint.TASK);
                                    break;
                                case "b4036abd654e11e7a75b001517543a38":  //????????????
                                    list.add(EnumFunctionPoint.NOTICE);
                                    break;
                                case "41eac0a8ac64457d9d81fbb1b0bf5323":  //????????????
                                    list.add(EnumFunctionPoint.APPLY);
                                    break;
                                case "21fe612a54d842238c7cc2fbe58c4ed4":  //?????????????????????
                                    list.add(EnumFunctionPoint.APPLY);
                                    PreferceManager.getInsance().saveValueBYkey(Global.mUser.getUuid() + "APPLY", "21fe612a54d842238c7cc2fbe58c4ed4");
                                    break;
                                case "72c118359e3b47a5bf859ea431131460":  //????????????
                                    list.add(EnumFunctionPoint.PRODUCT);
                                    break;
                                case "????????????":  //????????????
                                    list.add(EnumFunctionPoint.CHANGHUI_BESPOKE_LIST);
                                    break;
                                case "1ac20722419e4f739d949b3d913ea778":  //??????????????????
                                    list.add(EnumFunctionPoint.WE_CHAT_RECORD);
                                    break;
                                case "b8295ea13ac1450993953daffd64432f":  //????????????
                                    list.add(EnumFunctionPoint.CONPACT);
                                    break;
                                case "??????":
                                    list.add(EnumFunctionPoint.ACTIVITY);//??????
                                    break;
                                case "fe72556015b0452790a9c190ab4951f5": //????????????
                                    list.add(EnumFunctionPoint.CONTACTS);
                                    break;
                                /*case "43eb05dfe7b74b3fbdc0a117e5d12d04":  //?????????
                                    list.add(EnumFunctionPoint.INSIDE_COMMUNICATION);
                                    break;*/
                                case "b0526a815ddb4e40a50a67989d4e1c07":  //????????????
                                    list.add(EnumFunctionPoint.CLIENT);
                                    break;
                                case "2158c04a54d34afdb745d021618ab230":  //????????????
                                    list.add(EnumFunctionPoint.PROJECT);
                                    break;
                                case "aad9b4b6f4bf42df825710cd1684b283": //????????????
                                    list.add(EnumFunctionPoint.PROJECT_ATTENDANCE);
                                    break;
                                case "????????????":
                                    list.add(EnumFunctionPoint.DISPATCH);//????????????
                                    break;
                                case "????????????":
                                    list.add(EnumFunctionPoint.INCOMING);//????????????
                                    break;
                                case "39c53047bf6141d3aa8981d5dbb6314f":  //????????????
                                    list.add(EnumFunctionPoint.CRMPROJECT);
                                    break;
                                case "a9149cc0c9e54d36bb220f3cacd23c49":  //????????????
                                    list.add(EnumFunctionPoint.CRMBUSINESS);
                                    break;
                                case "1e363a27c66a4430b2cfa994e7af0ee3": //????????????
                                    list.add(EnumFunctionPoint.EXAMINATION);
                                    break;
                                case "241124f4c9164d85a3872a3edc5e06c7": //????????????
                                    list.add(EnumFunctionPoint.CURRICULUM);
                                    break;
//                                case "12bd6b338726435aa4bfa6615ce0f769": //??????
//                                    list.add(EnumFunctionPoint.INVOICES);
//                                    break;
                                case "2b042ca7ae5e4e4d96eb42b185d95f6e"://??????
                                    list.add(EnumFunctionPoint.WORKORDER);
                                    break;
                                case "90fc3df83e6040139b7124c7bb594ca6"://??????
                                    list.add(EnumFunctionPoint.SPACE);
                                    break;
                                case "3b9e574627414b0d9fab8993ce36eb73"://??????
                                    list.add(EnumFunctionPoint.ACTIVITY);
                                    break;
                                case "d854ddc540d4486ea11bfacf3d0b464c"://??????
                                    list.add(EnumFunctionPoint.CRMCLUE);
                                    break;
                                case "086345144d4f4d2193751d8ca4f938b2"://bug??????
                                    list.add(EnumFunctionPoint.BUG);
                                    break;
                                case "b7219f5358c54f5795e02d3275867683": //????????????
                                    list.add(EnumFunctionPoint.INFORMATION);
                                    break;
                            }
                        }
                    }
//                    list.add(EnumFunctionPoint.APPLYFOR_INVOICE); //???????????? ?????????
                    itemOAList = function.getFunctions(list, true);
                    itemOAList = ParamUtils.sortMenuByClickNumber(itemOAList); //????????????????????????
                    sortMenuList(itemOAList);//??????
//                    lastOAList = setShowMenuNumber(itemOAList);
//                    menuListAdapter = getMenuListAdapter(lastOAList);
//                    menuGridView.setAdapter(menuListAdapter);
                    getH5Menu();
                    getMyApproval();
                } catch (Exception e) {
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

    /**
     * ?????????list??????
     *
     * @param list
     */
    private void sortMenuList(List<MenuChildItem> list) {
        //????????????
        Collections.sort(list, new Comparator<MenuChildItem>() {
            @Override
            public int compare(MenuChildItem o1, MenuChildItem o2) {
                return o2.count - o1.count;
            }
        });
    }

    /**
     * ????????????list??????
     */
    private void sortBoardList(List<FunctionBoard> list) {
        //????????????
        Collections.sort(list, new Comparator<FunctionBoard>() {
            @Override
            public int compare(FunctionBoard o1, FunctionBoard o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
    }

    /**
     * ???????????????????????????
     */
    private List<MenuChildItem> setShowMenuNumber(List<MenuChildItem> list) {
        List<MenuChildItem> childItemList = new ArrayList<>();
        if (list.size() > 5) {  //????????????5 ??????????????????4????????????????????????
            menu_ll.setVisibility(View.VISIBLE);
            menuGridView.setNumColumns(4);
            for (int i = 0; i < list.size() && i < 4; i++) {
                childItemList.add(list.get(i));
            }
        } else {  //???????????????5 ?????????????????? ????????????
            menu_ll.setVisibility(View.GONE);
            menuGridView.setNumColumns(5);
            for (int i = 0; i < list.size(); i++) {
                childItemList.add(list.get(i));
            }
        }

        return childItemList;
    }

    /**
     * ??????H5??????????????????
     */
    private void getH5Menu() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????H5????????????;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                List<H5Menu> h5Menus = JsonUtils.jsonToArrayEntity(JsonUtils.pareseData(response), H5Menu.class);
                if (h5Menus != null && h5Menus.size() > 0) {
                    for (H5Menu h5Menu : h5Menus) {
                        MenuChildItem item = new MenuChildItem();
                        item.icon = h5Menu.getLogo();
                        item.category = "H5";
                        item.title = h5Menu.getTitle();
                        item.URL = h5Menu.getH5url();
                        itemOAList.add(item);
                    }
                }
                lastOAList = setShowMenuNumber(itemOAList);
                menuListAdapter = getMenuListAdapter(lastOAList);
                menuGridView.setAdapter(menuListAdapter);
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
     * ????????????????????????
     */
    public void getMyApproval() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                getMyApprovalList();
            }
        });
    }

    /**
     * ??????????????????
     */
    private void getMyApprovalList() {
        StringRequest.postAsyn(approvalDemand.src, approvalDemand, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    List<WorkflowInstance> workflowInstances = JsonUtils.jsonToArrayEntity(JsonUtils.getStringValue(JsonUtils.getStringValue(response, "Data"), "data"), WorkflowInstance.class);
                    if (workflowInstances != null && menuPopAdapter == null) { //??????????????????popupwindow??????????????????
                        for (int i = 0; i < itemOAList.size(); i++) {
                            if (itemOAList.get(i).title.equals("??????")) {
                                itemOAList.get(i).unreadCount = workflowInstances.size();
                                break;
                            }
                        }
                    }

                    if (workflowInstances != null && menuListAdapter != null) { //????????????????????? ??????gridview?????????????????????
                        List<MenuChildItem> dataList = menuListAdapter.getDataList();
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).title.equals("??????")) {
                                dataList.get(i).unreadCount = workflowInstances.size();
                                menuListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    if (workflowInstances != null && menuPopAdapter != null) { //????????????????????? ??????popupwindow?????????????????????
                        List<MenuChildItem> dataList1 = menuPopAdapter.getDataList();
                        for (int i = 0; i < dataList1.size(); i++) {
                            if (dataList1.get(i).title.equals("??????")) {
                                dataList1.get(i).unreadCount = workflowInstances.size();
                                menuPopAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
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

    private CommanAdapter<MenuChildItem> getMenuListAdapter(List<MenuChildItem> gridItems) {
        return new CommanAdapter<MenuChildItem>(gridItems, getActivity(), R.layout.item_menu_list) {
            public void convert(int position, MenuChildItem item, BoeryunViewHolder viewHolder) {
                ImageView iv = viewHolder.getView(R.id.menu_icon);
                int padding = (int) ViewHelper.dip2px(mContext, 8);
                if ("H5".equals(item.category)) {
                    if (!TextUtils.isEmpty(item.icon)) {
                        String url = Global.BASE_JAVA_URL + GlobalMethord.??????H5?????? + item.icon;
                        ImageUtils.displyImage(url, iv, R.drawable.icon_home_space_new);
                        iv.setPadding(padding, padding, padding, padding);
                    } else {
                        iv.setPadding(0, 0, 0, 0);
                        viewHolder.setImageResoure(R.id.menu_icon, R.drawable.icon_home_space_new);
                    }
                } else {
                    iv.setPadding(0, 0, 0, 0);
                    viewHolder.setImageResoure(R.id.menu_icon, item.ico);
                }
                if (item.title.equals("????????????")) {
                    viewHolder.setTextValue(R.id.menu_tv, Global.CONTACT_TITLE);
                } else {
                    viewHolder.setTextValue(R.id.menu_tv, item.title);
                }

                NumImageView view = viewHolder.getView(R.id.menu_icon);
                if (item.title.equals("??????")) {
                    if (item.unreadCount > 0) {
                        view.setNum(item.unreadCount);
                        ShortcutBadger.applyCount(mContext, item.unreadCount);
                    } else {
                        view.setNum(0);
                        ShortcutBadger.applyCount(mContext, 0);
                    }
                } else {
                    view.setNum(0);
                }
            }
        };
    }

    /**
     * ?????????????????????
     *
     * @param item
     */
    private void skipMenuItemClick(MenuChildItem item) {
        if ("H5".equals(item.category)) {
            Intent intent = new Intent(mContext, WebviewNormalActivity.class);
            intent.putExtra(WebviewNormalActivity.EXTRA_TITLE, item.title);
            intent.putExtra("isHome", true);
            if (!TextUtils.isEmpty(item.URL)) {
                if (item.URL.contains("http")) {
                    intent.putExtra(WebviewNormalActivity.EXTRA_URL, item.URL);
                } else {
                    intent.putExtra(WebviewNormalActivity.EXTRA_URL, Global.BASE_JAVA_URL + item.URL);
                }
            }
            startActivity(intent);
        } else {
            ParamUtils.clickMenuAddParams(item.ponit); //??????????????????????????????????????????????????????
            switch (item.ponit) {
                case ATTANCE:  //????????????????????????
                    setMemuItemClickListener(NewAttendanceActivity.class);
                    break;

                case TASK:  //????????????????????????
                    setMemuItemClickListener(TaskListActivityNew.class);
                    break;

                case NOTICE:  //????????????????????????
                    setMemuItemClickListener(NoticeListActivity.class);
                    break;

                case LOG:  //????????????????????????
                    setMemuItemClickListener(LogListActivity.class);
                    break;

                case INSIDE_COMMUNICATION:  //???????????????????????????
                    setMemuItemClickListener(AddressListActivity.class);
                    break;

                case APPLY:  //????????????????????????
                    setMemuItemClickListener(ApplylistActivity.class);
                    break;
                case CLIENT: //??????
                    setMemuItemClickListener(ClientListActivity.class);
                    break;
                case PRODUCT: //??????
                    setMemuItemClickListener(ProductListActivity.class);
                    break;
                case INVOICES: //??????
                    setMemuItemClickListener(InvoicesListActivity.class);
                    break;
                case WORKORDER: //??????
                    setMemuItemClickListener(WorkOrderListActivity.class);
                    break;
                case CHANGHUI_BESPOKE_LIST: //??????
                    setMemuItemClickListener(BespokeListActivity.class);
                    break;
                case ACTIVITY: //??????
                    setMemuItemClickListener(ActivityListActivity.class);
                    break;
                case CONTACTS: //????????????
                    setMemuItemClickListener(ContactRecordListActivity.class);
                    break;
                case WE_CHAT_RECORD://??????????????????
                    setMemuItemClickListener(WeChatRecordActivity.class);
                    break;
                case SPACE://??????
                    setMemuItemClickListener(SpaceListActivity.class);
                    break;
                case DISPATCH://??????
                    setMemuItemClickListener(FawenActivity.class);
                    break;
                case INCOMING://??????
                    setMemuItemClickListener(ShouwenFragment.class);
                    break;
                case CRMPROJECT: //CRM????????????
                    setMemuItemClickListener(ProjectListActivity.class);
                    break;
                case PROJECT_ATTENDANCE: //??????????????????
                    setMemuItemClickListener(ProjectAttendanceActivity.class);
                    break;
                case CRMBUSINESS: //????????????
                    setMemuItemClickListener(BusinessListActivity.class);
                    break;
                case EXAMINATION: //????????????
                    setMemuItemClickListener(ExaminationlistActivity.class);
                    break;
                case CURRICULUM: //????????????
                    setMemuItemClickListener(CurriculumlistActivity.class);
                    break;
                case APPLYFOR_INVOICE: //??????
                    setMemuItemClickListener(ExpenseAccountActivity.class);
                    break;
                case CRMCLUE: //??????
                    setMemuItemClickListener(ClueListActivity.class);
                    break;
                case BUG: //bug
                    setMemuItemClickListener(BugListActivity.class);
                    break;
                case INFORMATION: //??????
                    setMemuItemClickListener(InformationListActivity.class);
                    break;
            }
        }
        //?????????????????? ?????????
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * ????????????????????????
     *
     * @param newActivity ???????????????
     */
    private void setMemuItemClickListener(final Class<?> newActivity) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), newActivity);
        startActivity(intent);
    }

    /**
     * ?????????????????????
     */
    public void receiveSelectedUser(User user) {
        if (taskLayout != null) {
            taskLayout.receiveSelectedUser(user);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
