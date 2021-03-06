package com.biaozhunyuan.tianyi.newuihome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biaozhunyuan.tianyi.R;
import com.biaozhunyuan.tianyi.base.BoeryunApp;
import com.biaozhunyuan.tianyi.base.LazyFragment;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.BitmapHelper;
import com.biaozhunyuan.tianyi.common.helper.DataCleanManager;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.ORMDataHelper;
import com.biaozhunyuan.tianyi.helper.PhotoHelper;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.UploadHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.login.LoginActivity;
import com.biaozhunyuan.tianyi.login.ReSetPasswordActivity;
import com.biaozhunyuan.tianyi.common.utils.ImageUtils;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.view.AlertDialog;
import com.biaozhunyuan.tianyi.common.view.CircleImageView;
import com.biaozhunyuan.tianyi.common.view.DictIosPickerBottomDialog;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Request;

import static android.app.Activity.RESULT_OK;
import static com.biaozhunyuan.tianyi.common.helper.PreferceManager.getInsance;

/**
 * ??????
 */
public class MineFragment extends LazyFragment {
    private View v;
    private Button btn_quite;//????????????
    private TextView userName;
    private TextView tvPosition; //??????
    private TextView tvVersion;
    private TextView tvCache;
    private RelativeLayout rlCache;
    private CircleImageView ivHead;
    private Activity context;
    private BoeryunApp app;
    private RelativeLayout setting_password;
    private RelativeLayout rlDownload;
    private RelativeLayout setting_tixing;
    private RelativeLayout rl_question;


    /* ?????????????????????????????????activity */
    private static final int CAMERA_WITH_DATA = 1;

    /* ??????????????????gallery???activity */
    private static final int PICKED_PHOTO_WITH_DATA = 2;

    private static final int CHOOSE_PHOTO = 3;

    /* ??????????????????????????? */
    private String filePath = Environment.getExternalStorageDirectory()
            + "/DCIM";
    private Bitmap photo; // ????????????
    private static String fileName; // ?????????
    private static String absoluteFileName; // ???????????????
    String[] choices = new String[]{"??????", "???????????????"};
    private static Uri uritempFile;

    private DictIosPickerBottomDialog dialog;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ProgressDialogHelper.dismiss();
                    String avatar = (String) msg.obj;
                    changeAvastar(avatar);
                    break;
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_mine_new, container, false);
        context = getActivity();
        app = (BoeryunApp) getActivity().getApplication();
        dialog = new DictIosPickerBottomDialog(context);
        initViews(v);
//        getPost();
//        getDept();
        setOnEvent();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PHOTO:
                    Intent intent = getCropImageIntent(data.getData());
                    startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
                    break;
                case PICKED_PHOTO_WITH_DATA: {// ??????Gallery?????????

                    fileName = getPhotoFileName();
                    if (data == null) {
                        Toast.makeText(context, "??????????????????..",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uritempFile));
                            ivHead.setImageBitmap(photo);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "??????????????????", Toast.LENGTH_SHORT).show();
                        }
                        // ???????????????????????????
                        absoluteFileName = PhotoHelper.PATH + File.separator
                                + fileName;
                        // ?????????????????????
                        BitmapHelper.createThumBitmap(absoluteFileName, photo);
                        uploadPhoto(absoluteFileName);
                    }
                    break;
                }
                case CAMERA_WITH_DATA: {// ????????????????????????,?????????????????????????????????????????????
                    SharedPreferences sp = getActivity().getSharedPreferences("config",
                            Context.MODE_PRIVATE);
                    absoluteFileName = sp.getString("path", "");
                    if (!TextUtils.isEmpty(absoluteFileName)) {
                        doCropPhoto(absoluteFileName);
                    }
                    break;
                }
            }
        }
    }

    private void setOnEvent() {
        setting_tixing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RemindActivity.class));
            }
        });

        rl_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GiveSuggestionActivity.class));
            }
        });
        btn_quite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApplication();
            }
        });

        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(choices);
//                startActivity(new Intent(context, HomeBoardSettingActivity.class));
            }
        });

        setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReSetPasswordActivity.class);
                startActivity(intent);
            }
        });
        /**
         * ????????????
         */

        rlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownPop();
            }
        });

        /**
         * ????????????
         */
        rlCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog(getActivity()).builder()
                        .setTitle("????????????")
                        .setMsg("?????????????????????")
                        .setPositiveButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataCleanManager.clearAllCache(context);
                                try {
                                    tvCache.setText(DataCleanManager.getTotalCacheSize(context));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                dialog.show();
            }
        });

        dialog.setOnSelectedListener(new DictIosPickerBottomDialog.OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                if (index == 0) { //??????
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 222);
                                return;
                            } else {
                                doTakePhoto();//??????????????????
                            }
                        } else {
                            doTakePhoto();//??????????????????
                        }
                    } else {
                        Toast.makeText(context, "??????SD???", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (index == 1) { //????????????
                    pickPhoto();
                }
            }
        });
    }

    private void initViews(View view) {
        setting_tixing = view.findViewById(R.id.rl_setting_tixing);
        rl_question = view.findViewById(R.id.rl_question);
        rlDownload = view.findViewById(R.id.rl_download);
        btn_quite = (Button) view.findViewById(R.id.btn_quit_setting);
        userName = (TextView) view.findViewById(R.id.tv_userinfo_setting_company);
        tvPosition = (TextView) view.findViewById(R.id.tv_userinfo_setting_position);
        ivHead = (CircleImageView) view.findViewById(R.id.imageViewCamera1);
        tvVersion = (TextView) view.findViewById(R.id.tv_version_setting);
        tvCache = (TextView) view.findViewById(R.id.tv_setting_clear_cache);
        rlCache = (RelativeLayout) view.findViewById(R.id.rl_setting_clear_cache);
        setting_password = view.findViewById(R.id.rl_setting_password);

        tvVersion.setText(ViewHelper.getVersionName(context));
        userName.setText(Global.mUser.getName());

        try {
            tvCache.setText(DataCleanManager.getTotalCacheSize(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        //??????????????????????????????
        //???????????????intent.setDataAndType("image/*");
        //????????????????????????
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * Constructs an intent for image cropping. ????????????????????????
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        }
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // ???
        intent.putExtra("outputY", 300); // ???
        intent.putExtra("return-data", true);

        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

    /**
     * ???????????????????????????????????????
     */
    private String getPhotoFileName() {
        return "IMG"
                + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
    }


    /**
     * ???????????? ????????????
     *
     * @param path
     * @return
     */
    private void uploadPhoto(final String path) {
        ProgressDialogHelper.show(context);
        final File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "???????????????????????????",
                    Toast.LENGTH_SHORT).show();
        } else {
            new Thread() {
                @Override
                public void run() {
                    String attachId = UploadHelper.uploadFileGetAttachId("attendance", file);
                    Message msg = new Message();
                    msg.obj = attachId;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }


    /**
     * ????????????
     */
    private void changeAvastar(final String uuid) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?addedId=" + uuid;
        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                Global.mUser.setAvatar(uuid);
                new DictionaryHelper(context).changeUserAvastar(Global.mUser.getUuid(), uuid);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponseCodeErro(String result) {
                Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void doCropPhoto(String fileName) {
        Uri uri;
        try {
            File file = new File(fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.biaozhunyuan.tianyi.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            final Intent intent = getCropImageIntent(uri);
            startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "failure", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ??????????????????
     */
    protected void doTakePhoto() {
        Uri contentUri = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// ??????????????????

        fileName = getPhotoFileName();
        absoluteFileName = filePath + File.separator + fileName;
        File file = new File(absoluteFileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, "com.biaozhunyuan.tianyi.fileprovider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("path", absoluteFileName);
        editor.commit();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }


    /**
     * ????????????
     */

    private void showDownPop() {
        View view = View.inflate(getActivity(), R.layout.pop_scan_download, null);
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout rlErweima = (RelativeLayout) view.findViewById(R.id.rl_erweima);

        rlErweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        window.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
    }

    private void getPost() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????2 + "?tableName=base_position&ids=" + Global.mUser.getPost();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(JsonUtils.pareseData(response))) {
                    userName.setText(Global.mUser.getName() + "(" + JsonUtils.pareseData(response) + ")");
                }
//                corpName.setText(new DictionaryHelper(context).getDepartNameById(Global.mUser.getDepartmentId()) + "--" + JsonUtils.pareseData(response));
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {

            }
        });
    }


    private void getDept() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????2 + "?tableName=base_department&ids=" + Global.mUser.getDepartmentId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(JsonUtils.pareseData(response))) {
                    Global.mUser.setDepartmentName(JsonUtils.pareseData(response));
                }
//                corpName.setText(new DictionaryHelper(context).getDepartNameById(Global.mUser.getDepartmentId()) + "--" + JsonUtils.pareseData(response));
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
     * ???????????????,??????????????????,????????????????????????????????????
     */
    protected void closeApplication() {
        AlertDialog dialog = new AlertDialog(getActivity()).builder()
                .setTitle("????????????")
                .setMsg("???????????????????????????????????????")
                .setPositiveButton("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getInsance().saveValueBYkey("baseURL", "");
                        getInsance().saveValueBYkey("isExit", true);
                        getInsance().saveValueBYkey("IsShowAuditeBtnOnFlowList",true);
                        getInsance().saveValueBYkey("IsShowFormAddDetailBtn",true);
                        clearToken();
                        ORMDataHelper.getInstance(context).deleteOldDb();
                        XGPushManager.unregisterPush(context);
                        startActivity(new Intent(context, LoginActivity.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton("??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        dialog.show();
    }


    /**
     * ???????????????token
     */
    private void clearToken() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            userName.setText(Global.mUser.getName());
            tvPosition.setText(Global.mUser.getPost());
            ImageUtils.displyUserPhotoById(context, Global.mUser.getUuid(), ivHead, Color.parseColor("#3366CC"));
        }
    }
}
