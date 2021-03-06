package com.biaozhunyuan.tianyi.newuihome;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.apply.FormInfoActivity;
import com.biaozhunyuan.tianyi.buglist.BugInfoActivity;
import com.biaozhunyuan.tianyi.client.AddRecordActivity;
import com.biaozhunyuan.tianyi.client.ClientRelatedInfoActivity;
import com.biaozhunyuan.tianyi.common.base.BaseActivity;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.PreferceManager;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.model.user.UserList;
import com.biaozhunyuan.tianyi.common.utils.AndroidBug5497Workaround;
import com.biaozhunyuan.tianyi.common.utils.LogUtils;
import com.biaozhunyuan.tianyi.common.view.AlertDialog;
import com.biaozhunyuan.tianyi.dynamic.Dynamic;
import com.biaozhunyuan.tianyi.helper.WebviewNormalActivity;
import com.biaozhunyuan.tianyi.log.LogInfoActivity;
import com.biaozhunyuan.tianyi.notice.NoticeInfoActivity;
import com.biaozhunyuan.tianyi.task.TaskInfoActivityNew;
import com.biaozhunyuan.tianyi.task.TaskListActivityNew;
import com.biaozhunyuan.tianyi.utils.SystemUtil;
import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushCallback;
import com.coloros.mcssdk.mode.SubscribeResult;
import com.gyf.barlibrary.ImmersionBar;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.Request;

import static com.biaozhunyuan.tianyi.task.TaskNewActivity.REQUEST_SELECT_EXCUTORS_STAFF_VIEW;
import static com.biaozhunyuan.tianyi.task.TaskNewActivity.RESULT_SELECT_USER;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private RelativeLayout rl_menu;   //??????
    private RelativeLayout rl_contact;
    private RelativeLayout rl_home;   //??????
    private Context context;
    private long lastClickTime;

    private ImageView iv_munu;
    private ImageView iv_contact;
    private ImageView iv_home;

    private TextView tv_menu;
    private TextView tv_contact;
    private TextView tv_home;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private AddressListFragment contactAllFragment;
    private MineFragment mineFragment;

    private FragmentTransaction fragmentTransaction1;
    private ServiceConnection sc;
    private Dialog progressDialog;
    private ProgressBar bar;
    private TextView tvProgress;
    private TextView tvSize;
    private boolean isPgsShow = false;

    private TextView tvRedUnRead;
    private boolean FLAG = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_home_new_ui, null);
        setContentView(view);
        context = HomeActivity.this;
        AndroidBug5497Workaround.assistActivity(HomeActivity.this);
        initView();
        initData();
        initPushNotification();
//        bindSocketService();
        updateVersion();
//        preventProjectInSeaEveryDay();
        getIntentData();
        setOnTouch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            Dynamic dynamic = (Dynamic) intent.getSerializableExtra("dynamicInfo");
            if (dynamic != null) {
                intoInfoByPushType(dynamic);
            }
            String isGetAllUser = intent.getStringExtra("isGetAllUser");
            /*if ("true".equals(isGetAllUser) && backlogFragment != null) {
                backlogFragment.getAllUser();
            }*/
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);  //????????????????????????????????????????????????????????????fragment?????????????????????
    }

    private void setOnTouch() {

    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            Dynamic dynamic = (Dynamic) getIntent().getSerializableExtra("dynamicInfo");
            if (dynamic != null) {
                intoInfoByPushType(dynamic);
            }
            /*String isGetAllUser = getIntent().getStringExtra("isGetAllUser");
            if ("true".equals(isGetAllUser) && backlogFragment != null) {
                backlogFragment.getAllUser();
            }*/
        }
    }

    private void initData() {
        contactAllFragment = new AddressListFragment();
        homeFragment = new HomeFragment();
        mineFragment = new MineFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1
                .add(R.id.fl_home_fragment, homeFragment)
                .add(R.id.fl_home_fragment, contactAllFragment)
                .add(R.id.fl_home_fragment, mineFragment);
        fragmentTransaction1.hide(contactAllFragment).hide(mineFragment).show(homeFragment);
        fragmentTransaction1.commit();
        changeTab(1);
        changeStatusBar(1);
    }

    private void initView() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tvRedUnRead = findViewById(R.id.red_point_message);


        tv_menu = findViewById(R.id.tv_home_navigation); //??????
        tv_contact = findViewById(R.id.tv_home_share);
        tv_home = findViewById(R.id.tv_home_set);    //??????


        iv_munu = findViewById(R.id.iv_home_navigation); //??????
        iv_contact = findViewById(R.id.iv_home_share);
        iv_home = findViewById(R.id.iv_home_set);  //??????


        rl_menu = findViewById(R.id.rl_home_navigation); //??????
        rl_contact = findViewById(R.id.rl_home_share);  //??????
        rl_home = findViewById(R.id.rl_home_set);  //??????


        rl_menu.setOnClickListener(HomeActivity.this);
        rl_contact.setOnClickListener(HomeActivity.this);
        rl_home.setOnClickListener(HomeActivity.this);
    }

    /**
     * ??????fragment????????????????????????????????????
     *
     * @param num ???????????????fragment
     */
    private void changeStatusBar(int num) {
        switch (num) {
            case 1:
                ImmersionBar.with(this).statusBarColor(R.color.new_theme_blue).statusBarDarkFont(false).fitsSystemWindows(true).init();
                break;
            case 2:
                ImmersionBar.with(this).statusBarColor(R.color.new_theme_blue).statusBarDarkFont(false).fitsSystemWindows(true).init();
                break;
            case 3:
                ImmersionBar.with(this).statusBarColor(R.color.hanyaRed).statusBarDarkFont(false).fitsSystemWindows(true).init();
                break;
        }
    }


    /**
     * ??????tab???????????????
     *
     * @param num ??????????????????
     */
    private void changeTab(int num) {
        switch (num) {
            case 1:
                iv_munu.setImageResource(R.drawable.new_home_menu);
                iv_contact.setImageResource(R.drawable.new_home_mine);
                iv_home.setImageResource(R.drawable.new_home_home_select);


                tv_home.setTextColor(getResources().getColor(R.color.new_navigation_text_blue));
                tv_contact.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));
                tv_menu.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));

                break;
            case 2:
                iv_contact.setImageResource(R.drawable.new_home_mine);
                iv_munu.setImageResource(R.drawable.new_home_menu_select);
                iv_home.setImageResource(R.drawable.new_home_home);


                tv_menu.setTextColor(getResources().getColor(R.color.new_navigation_text_blue));
                tv_contact.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));
                tv_home.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));
                break;
            case 3:
                iv_contact.setImageResource(R.drawable.new_home_mine_select);
                iv_munu.setImageResource(R.drawable.new_home_menu);
                iv_home.setImageResource(R.drawable.new_home_home);

                tv_home.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));
                tv_menu.setTextColor(getResources().getColor(R.color.new_navigation_text_gray));
                tv_contact.setTextColor(getResources().getColor(R.color.new_navigation_text_blue));
                break;
        }
    }


    /**
     * ???????????????
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (FLAG) {
//                menu.toggle();
            }/* else if (contactAllFragment != null && contactAllFragment.isDepart) {
                //???????????????????????????????????????????????????
                contactAllFragment.backLast();
                return false;
            }*/ else {
                if (System.currentTimeMillis() - lastClickTime > 2000) {
                    Toast.makeText(this, "??????????????????" + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                    lastClickTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECT_EXCUTORS_STAFF_VIEW: //???????????????
                    Bundle bundle = data.getExtras();
                    UserList userList = (UserList) bundle.getSerializable(RESULT_SELECT_USER);
                    if (userList != null) {
                        try {
                            List<User> users = userList.getUsers();
//                            backlogFragment.receiveSelectedUser(users.get(0));
                        } catch (IndexOutOfBoundsException e) {
                            showShortToast("?????????????????????");
                        }
                    }
            }
        }
    }

    /***
     * ??????????????????
     */
    private void updateVersion() {
        /** ????????? **/
        new PgyUpdateManager.Builder()
                .setForced(true)                //??????????????????????????????,?????????????????????????????????????????????
                .setUserCanRetry(false)         //?????????????????????????????????????????????????????? apk ?????????????????????
                .setDeleteHistroyApk(false)     // ??????????????????????????????????????? Apk??? ?????????true
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //??????????????????????????????
                        Log.d("pgyer", "there is no new version");
                    }

                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //????????????????????????
                        Log.d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());
                        AlertDialog dialog = new AlertDialog(HomeActivity.this).builder()
                                .setTitle("????????????")
                                .setMsg("??????????????????" + appBean.getVersionName() + "??????????????????")
                                .setPositiveButton("??????", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //?????????????????????DownloadFileListener ????????????
                                        //?????????????????????????????????????????????????????????DownloadFileListener
                                        PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                                    }
                                })
                                .setNegativeButton("??????", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                        dialog.show();
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //????????????????????????
                        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        Log.e("pgyer", "check update failed ", e);
                    }
                })
                //?????? ???
                //?????????????????? PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); ??????????????????
                //???????????????????????????????????????????????????????????? UI ???????????????
                //?????????????????????????????????????????????UI?????????????????????
                .setDownloadFileListener(new DownloadFileListener() {
                    @Override
                    public void downloadFailed() {
                        //????????????
                        Log.e("pgyer", "download apk failed");
                    }

                    @Override
                    public void downloadSuccessful(File uri) {
                        Log.e("pgyer", "download apk success");
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            setBackgroundLight();
                        }
                        // ???????????????????????????????????????????????? ??????apk
                        PgyUpdateManager.installApk(uri);
                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Integer[] clone = integers.clone();
                        for (Integer integer : clone) {
                            Log.e("pgyer", "update download apk progress" + integer);
                            if (progressDialog == null) {
                                progressDialog = new Dialog(HomeActivity.this, R.style.progress_dialog);
                                progressDialog.setContentView(R.layout.dialog_download_progress);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(true);
                                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                bar = progressDialog.findViewById(R.id.pb_main_download);
                                tvProgress = progressDialog.findViewById(R.id.tv_progress);
                                tvSize = progressDialog.findViewById(R.id.tv_size_download);
                            }
                            if (!isPgsShow) {
                                progressDialog.show();
                                setBackgroundDark();
                                isPgsShow = true;
                            }
                            bar.setProgress(integer);
                            tvProgress.setText(integer + "%");
                            tvSize.setText((39 * integer / 100) + "M/39M");
                        }

                    }
                })
                .register();
    }


    private void setBackgroundDark(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void setBackgroundLight(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * ????????????token
     */
    private void bindToken(String token, String mobileType) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;

        JSONObject jo = new JSONObject();
        try {
            jo.put("token", token);
            jo.put("allowPush", "1");
            jo.put("deviceType", "android");
            jo.put("mobileType", mobileType);
            jo.put("otherToken", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest.postAsyn(url, jo, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {

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
     * ???????????????????????????
     */
    private void initPushNotification() {
        String mobileType = SystemUtil.getDeviceBrand(); //????????????
        LogUtils.i("?????????????????????", mobileType);
        if ("huawei".equalsIgnoreCase(mobileType) || "honor".equalsIgnoreCase(mobileType)) { //????????????
            HMSAgent.init(this); //?????????????????????
            HMSAgent.Push.getToken(new GetTokenHandler() {
                @Override
                public void onResult(int rst) {
                    LogUtils.i("??????????????????????????????", "" + rst);
                }
            });
        } else if ("xiaomi".equalsIgnoreCase(mobileType)) {//????????????
            if (shouldInit()) {
                MiPushClient.registerPush(this, Global.xmAppId, Global.xmAppKey); //?????????????????????
            }
        } else if ("vivo".equalsIgnoreCase(mobileType)) {
            PushClient.getInstance(getApplicationContext()).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    if (state != 0) {
                        LogUtils.i("Vivo????????????", "??????push??????[" + state + "]");
                    } else {
                        LogUtils.i("??????push??????", state + "");
                        String token = PushClient.getInstance(getApplicationContext()).getRegId();
                        bindToken(token, "vivo");
                        LogUtils.i("VivoToken?????????", token);
                    }
                }
            });
        } else if ("oppo".equalsIgnoreCase(mobileType)) {
            boolean isSupport = PushManager.isSupportPush(this);
            registerChannal();
            if (isSupport) {
                PushManager.getInstance().register(context, Global.oppoAppKey, Global.oppoAppSecret, new PushCallback() {

                    //???????????????,??????????????????,registerID????????????????????????????????????
                    @Override
                    public void onRegister(int responseCode, String registerID) {
                        bindToken(registerID, "oppo");
                    }

                    @Override
                    public void onUnRegister(int i) {

                    }

                    @Override
                    public void onGetAliases(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onSetAliases(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onUnsetAliases(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onSetUserAccounts(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onUnsetUserAccounts(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onGetUserAccounts(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onSetTags(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onUnsetTags(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onGetTags(int i, List<SubscribeResult> list) {

                    }

                    @Override
                    public void onGetPushStatus(int i, int i1) {

                    }

                    @Override
                    public void onSetPushTime(int i, String s) {

                    }

                    @Override
                    public void onGetNotificationStatus(int i, int i1) {

                    }
                });
            }
        } else {
            //????????????????????????
            XGPushConfig.enableDebug(this, true);
            //XGPushConfig.setHuaweiDebug(true);
            //XGPushConfig.enableOtherPush(getApplicationContext(), true);//?????????????????????

            XGPushManager.registerPush(this, new XGIOperateCallback() {//??????????????????
                @Override
                public void onSuccess(Object data, int flag) {
                    String deviceToken = data.toString();
                    PreferceManager.getInsance().saveValueBYkey("deviceToken", deviceToken);
                    Log.d("TPush", "?????????????????????token??????" + deviceToken);
                    bindToken(deviceToken, "??????");
                }

                @Override
                public void onFail(Object data, int errCode, String msg) {
                    Log.d("TPush", "???????????????????????????" + errCode + ",???????????????" + msg);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {

            case R.id.rl_home_navigation:
                changeTab(2);
                changeStatusBar(2);
                fragmentTransaction.hide(mineFragment).hide(homeFragment).show(contactAllFragment);
//                fragmentTransaction.replace(R.id.fl_home_fragment,new MenuHomeFragment());
                break;

            case R.id.rl_home_share:
                changeTab(3);
                changeStatusBar(3);
                fragmentTransaction.hide(homeFragment).hide(mineFragment).show(mineFragment);

                break;

            case R.id.rl_home_set:
                changeTab(1);
                changeStatusBar(1);
                fragmentTransaction.hide(mineFragment).hide(contactAllFragment).show(homeFragment);
                break;
        }

        fragmentTransaction.commit();
    }

    /**
     * ??????????????????????????????????????????
     */
    private void intoInfoByPushType(Dynamic dynamic) {
        Intent intent = new Intent();
        switch (dynamic.getDataType()) {
            case "??????????????????":
                intent.setClass(context, FormInfoActivity.class);
                String url1 = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?workflowId=" + dynamic.getDataId();
                intent.putExtra("exturaUrl", url1);
                intent.putExtra("formDataId", dynamic.getDataId());
                break;
            case "????????????":
                intent.setClass(context, LogInfoActivity.class);
                break;
            case "????????????":
                intent.setClass(context, TaskInfoActivityNew.class);
                break;
            case "????????????":
                intent.setClass(context, NoticeInfoActivity.class);
                break;
            case "????????????":
                intent.setClass(context, ClientRelatedInfoActivity.class);
                break;
            case "??????????????????":
            case "??????????????????":
                intent.setClass(context, AddRecordActivity.class);
                break;
            case "????????????":
//                intent.setClass(context, ChatActivity.class);
//                GroupSession session = new GroupSession();
//                session.setChatId(dynamic.getDataId());
//                intent.putExtra("chatUser", session);
//                intent.putExtra("isPush", true);
//                break;
                return;
            case "????????????":
                String url = Global.BASE_JAVA_URL + GlobalMethord.????????????H5 + dynamic.getDataId();
                intent.setClass(context, WebviewNormalActivity.class);
                intent.putExtra(WebviewNormalActivity.EXTRA_TITLE, dynamic.getDataType());
                intent.putExtra(WebviewNormalActivity.EXTRA_URL, url);
                break;
            case "??????????????????":
                intent.setClass(context, TaskListActivityNew.class);
                break;
            case "BUG??????":
                intent.setClass(context, BugInfoActivity.class);
                break;
            case "????????????":
                intent.setClass(context, FormInfoActivity.class);
                String url2 = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?workflowId=" + dynamic.getDataId();
                intent.putExtra("exturaUrl", url2);
                intent.putExtra("formDataId", dynamic.getDataId());
                intent.putExtra("isShowCancelPush", true);
                break;

        }
        intent.putExtra("dynamicInfo", dynamic);
        startActivity(intent);
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        //??????socket??????
        super.onDestroy();
    }

    private void registerChannal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "tianyi";
            String channelName = "????????????";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}
