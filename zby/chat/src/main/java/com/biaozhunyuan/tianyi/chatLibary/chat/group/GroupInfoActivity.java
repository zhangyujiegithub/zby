
package com.biaozhunyuan.tianyi.chatLibary.chat.group;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.biaozhunyuan.tianyi.chatLibary.chat.ChatActivity;
import com.biaozhunyuan.tianyi.chatLibary.chat.UserInfoActivity;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.GroupMembers;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.GroupModel;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.GroupSession;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.RecentMessage;
import com.biaozhunyuan.tianyi.common.base.BaseActivity;
import com.biaozhunyuan.tianyi.common.base.BoeryunViewHolder;
import com.biaozhunyuan.tianyi.common.base.CommanAdapter;
import com.biaozhunyuan.tianyi.common.base.ParseException;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.ProgressDialogHelper;
import com.biaozhunyuan.tianyi.common.helper.SharedPreferencesHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.model.user.UserList;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.view.BoeryunHeaderView;
import com.biaozhunyuan.tianyi.common.view.NoScrollGridView;
import com.example.chat.R;


import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by wangAnMin on 2019/3/5.
 * ???????????????????????????????????????????????????????????????????????????..)
 */

public class GroupInfoActivity extends BaseActivity {

    private final int REQUEST_CODE_SELECT_MEMBER = 101;
    private final int REQUEST_CODE_DELETE_MEMBER = 102;
    private final int REQUEST_CODE_UPDATE_NAME = 103;
    private Context mContext;
    private String groupNames;
    private GroupSession groupSession;
    //    private AlertDialog alertDialog;
    private boolean isClearAllMessage = false;
    private boolean isDepartment = false;
    private boolean isTop = false;
    private SharedPreferencesHelper preferencesHelper;


    private BoeryunHeaderView headerView;
    private NoScrollGridView gridView;
    private TextView tvGroupName;//????????????
    private TextView tvNumber;//????????????
    private LinearLayout rlMsgRecord; //????????????
    private TextView tvClearAll; //??????????????????
    private TextView tvQuiteGroup; //?????????????????????
    private LinearLayout llMembers;
    private LinearLayout llGroupImage; //????????????
    private LinearLayout llGroupFile; //????????????
    private LinearLayout llUpdateName; //???????????????
    private LinearLayout llSetTop; //????????????
    private LinearLayout llSetNoInterrupt; //???????????????
    private TextView tvDeptTag;
    private ImageView ivArrowName;
    private Switch switchTop;
    private Switch switchNoInterrupt; //???????????????
    private RelativeLayout rlGroupManage;
    private List<GroupMembers> groupMembers;
    private GroupModel mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        initViews();
        getIntentData();
        setOnEvent();
    }


    /**
     * ??????????????????????????????????????????
     */
    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            groupSession = (GroupSession) getIntent().getSerializableExtra("GroupSession");
            if (groupSession != null) {
                initData();
                getGroupInfo();
            }
        }
    }


    /**
     * ?????????view
     */
    private void initViews() {
        headerView = findViewById(R.id.headerview_group_info);
        gridView = findViewById(R.id.grid_group_members);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvNumber = findViewById(R.id.tv_nmb_member);
        rlMsgRecord = findViewById(R.id.rl_message_record);
        tvClearAll = findViewById(R.id.clear_all_message);
        llMembers = findViewById(R.id.ll_members);
        llGroupImage = findViewById(R.id.ll_group_image);
        llGroupFile = findViewById(R.id.ll_group_file);
        llUpdateName = findViewById(R.id.ll_update_name);
        ivArrowName = findViewById(R.id.iv_arrow_name);
        tvDeptTag = findViewById(R.id.tv_dept_tag);
        llSetTop = findViewById(R.id.ll_set_top);
        llSetNoInterrupt = findViewById(R.id.ll_set_no_interrupt);
        switchTop = findViewById(R.id.switch_top);
        switchNoInterrupt = findViewById(R.id.switch_no_interrupt);
        tvQuiteGroup = findViewById(R.id.quit_group);
        rlGroupManage = findViewById(R.id.rl_group_manage);
    }


    /**
     * ???????????????
     */
    private void initData() {
        mContext = GroupInfoActivity.this;
        preferencesHelper = new SharedPreferencesHelper(mContext);
        tvGroupName.setText(groupSession.getName());//????????????
        switchNoInterrupt.setChecked(groupSession.isSetNoInterrupt());
    }


    /**
     * ??????????????????
     */
    private void setOnEvent() {
        headerView.setOnButtonClickListener(new BoeryunHeaderView.OnButtonClickListener() {
            @Override
            public void onClickBack() {
                setOnResult();
            }

            @Override
            public void onClickFilter() {

            }

            @Override
            public void onClickSaveOrAdd() {

            }
        });

        /**
         * ????????????????????????????????????
         */
        llUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateGroupNameActivity.class);
                intent.putExtra("groupName", groupSession.getName());
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NAME);
            }
        });

        rlMsgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupSearchActivity.class);
                intent.putExtra("groupInfo", groupSession);
                startActivity(intent);
            }
        });


        /**
         * ??????????????????
         */
        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMessage();
                //???????????????????????????
                HashMap<String, RecentMessage> hashMapData = preferencesHelper.getHashMapData
                        (Global.mUser.getUuid() + "ChatRecord", RecentMessage.class);
                hashMapData.remove(groupSession.getChatId());
                preferencesHelper.putHashMapData(Global.mUser.getUuid() + "ChatRecord", hashMapData);


                //???????????????????????????????????????
                List<GroupSession> sessions = preferencesHelper.getListData
                        (Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
                for (GroupSession session : sessions) {
                    if (groupSession.getChatId().equals(session.getChatId())) {
                        session.setLastMessage("");
                        break;
                    }
                }
                preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), sessions);
                showShortToast("????????????");
                isClearAllMessage = true;
            }
        });

        /**
         * ?????????????????????
         */

        tvQuiteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiteGroup(groupSession.getChatId());
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                GroupMembers item = (GroupMembers) gridView.getItemAtPosition(position);
                intent.putExtra("userId", item.getUuid());
                startActivity(intent);
            }
        });


        /**
         * ???????????????
         */
        llMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupMembersActivity.class);
                intent.putExtra("isDeleteMembers", false);
                intent.putExtra("groupMembers", (Serializable) groupMembers);
                startActivity(intent);
            }
        });


        llGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupImageActivity.class);
                intent.putExtra("groupInfo", groupSession);
                startActivity(intent);
            }
        });

        llGroupFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupFileActivity.class);
                intent.putExtra("groupInfo", groupSession);
                startActivity(intent);
            }
        });


        rlGroupManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupManageActivity.class);
                intent.putExtra("groupInfo", groupSession);
                startActivity(intent);
            }
        });

        switchTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchTop.setEnabled(false);
                if (isChecked) {
                    setGroupTop();
                } else {
                    cancelGroupTop();
                }
            }
        });


        //???????????????/?????????????????????
        switchNoInterrupt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchNoInterrupt.setEnabled(false);
                if (isChecked) {
                    closeMessageNotice();
                } else {
                    openMessageNotice();
                }
            }
        });


    }

    private void setOnResult() {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("isClearAllMessage", isClearAllMessage);
        if (!TextUtils.isEmpty(groupNames)) {
            intent.putExtra("groupNames", groupNames);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setOnResult();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ??????????????????
     *
     * @param groupName ??????????????????
     */
    private void updateGroupName(final String groupName) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId="
                + groupSession.getChatId() + "&groupName=" + groupName;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("????????????");
                tvGroupName.setText(groupName);
                groupSession.setName(groupName);
                groupNames = groupName;
                /**
                 * ????????????????????????
                 */
                List<GroupSession> sessions = preferencesHelper.getListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
                for (GroupSession session : sessions) {
                    if (session.getChatId() == null)
                        continue;
                    if (session.getChatId().equals(groupSession.getChatId())) {
                        session.setName(groupName);
                        break;
                    }
                }
                preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), sessions);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                showShortToast("????????????");
            }
        });
    }

    /**
     * ??????????????????
     */
    private void getGroupInfo() {
        ProgressDialogHelper.show(mContext);
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                try {
                    mGroup = JsonUtils.jsonToEntity(JsonUtils.pareseData(response), GroupModel.class);
                    if (mGroup != null) {
                        initGroupData();
                    }
                } catch (ParseException e) {
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

    private void initGroupData() {
        isDepartment = mGroup.getIsDepartment() == 1;
        getGroupMembers();
        if (isDepartment) {
            llUpdateName.setEnabled(false);
            tvQuiteGroup.setVisibility(View.GONE);
            ivArrowName.setVisibility(View.GONE);
            tvDeptTag.setVisibility(View.VISIBLE);
        }
        if (mGroup.getIsTop() == 1) {
            isTop = true;
            switchTop.setChecked(true);
        } else {
            switchTop.setChecked(false);
        }
    }

    private void setGroupTop() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                switchTop.setEnabled(true);
                isTop = true;
                groupSession.setTop(isTop);
                updateLocalSessionList(groupSession);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                switchTop.setEnabled(true);
            }

            @Override
            public void onResponseCodeErro(String result) {
                switchTop.setEnabled(true);
                isTop = true;
                groupSession.setTop(isTop);
                updateLocalSessionList(groupSession);
            }
        });
    }


    private void cancelGroupTop() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                switchTop.setEnabled(true);
                isTop = false;
                groupSession.setTop(isTop);
                updateLocalSessionList(groupSession);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                switchTop.setEnabled(true);
            }

            @Override
            public void onResponseCodeErro(String result) {
                switchTop.setEnabled(true);
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void closeMessageNotice() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                switchNoInterrupt.setEnabled(true);
                setLocalMessageNotice(true);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                switchNoInterrupt.setEnabled(true);
            }

            @Override
            public void onResponseCodeErro(String result) {
                switchNoInterrupt.setEnabled(true);
            }
        });
    }


    /**
     * ?????????????????????
     */
    private void openMessageNotice() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                switchNoInterrupt.setEnabled(true);
                setLocalMessageNotice(false);
            }

            @Override
            public void onFailure(Request request, Exception ex) {
                switchNoInterrupt.setEnabled(true);
            }

            @Override
            public void onResponseCodeErro(String result) {
                switchNoInterrupt.setEnabled(true);
            }
        });
    }


    /**
     * ?????????????????? ???????????????/????????????
     *
     * @param session ?????????/?????????????????????
     */
    private void updateLocalSessionList(GroupSession session) {
        List<GroupSession> topList = new ArrayList<>(); //?????????????????????
        List<GroupSession> unTopList = new ArrayList<>(); //????????????????????????
        List<GroupSession> lists = preferencesHelper.getListData
                (Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
        for (GroupSession list : lists) {
            if (list.isTop()) {
                topList.add(list);
            } else {
                unTopList.add(list);
            }
            if (session.getChatId().equals(list.getChatId())) {
                list.setTop(session.isTop());
            }
        }
        //??????????????????????????????????????????
        sortListByTopTime(topList);
        //??????????????????????????????????????????????????????????????????
        sortListByLastSendTime(unTopList);
        lists.clear();
        lists.addAll(topList);
        lists.addAll(unTopList);
        preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), lists);
    }


    /**
     * ?????????????????????????????????
     *
     * @param isSetNoInterrupt ???????????????
     */
    private void setLocalMessageNotice(boolean isSetNoInterrupt) {
        List<GroupSession> lists = preferencesHelper.getListData
                (Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
        for (GroupSession list : lists) {
            if (list.getChatId().equals(groupSession.getChatId())) {
                list.setSetNoInterrupt(isSetNoInterrupt);
                break;
            }
        }
        preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), lists);
    }


    /**
     * ???????????????????????????
     */
    private void clearMessage() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId=" + groupSession.getChatId();

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

    /**
     * ????????????????????????
     */
    private void getGroupMembers() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId=" + groupSession.getChatId();

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                ProgressDialogHelper.dismiss();
                groupMembers = JsonUtils.jsonToArrayEntity(JsonUtils.pareseData(response), GroupMembers.class);

                if (!response.contains(Global.mUser.getUuid())) {
                    llUpdateName.setClickable(false);
                    llUpdateName.setEnabled(false);
                }

                if (groupMembers != null) {
                    for (GroupMembers groupMember : groupMembers) {
                        if (groupMember.getUuid().equals(mGroup.getGroupOwner())) {
                            groupMember.setManager(true);
                            break;
                        }
                    }
                    tvNumber.setText(groupMembers.size() + "???");
                    List<GroupMembers> showMemebers = new ArrayList<>();
                    if (groupMembers.size() >= 6) {
                        showMemebers.addAll(groupMembers.subList(0, 6));
                    } else {
                        showMemebers.addAll(groupMembers);
                    }


                    if (!isDepartment) { //????????????????????? ??????????????????????????????
                        //??????????????????????????????????????????????????????????????????
                        if (Global.mUser.getUuid().equals(mGroup.getGroupOwner())) {
                            rlGroupManage.setVisibility(View.VISIBLE);

                            GroupMembers addMember = new GroupMembers();
                            addMember.setType("1");
                            showMemebers.add(addMember); //??????????????????

                            GroupMembers deleteMember = new GroupMembers();
                            deleteMember.setType("2");
                            showMemebers.add(deleteMember); //??????????????????
                        } else if (mGroup.isManagerUpdateMemberOnly() == 0
                                && response.contains(Global.mUser.getUuid())) {
                            //?????? ??????????????????????????????????????????????????? ?????? ??????????????????????????????????????????????????????
                            GroupMembers addMember = new GroupMembers();
                            addMember.setType("1");
                            showMemebers.add(addMember); //??????????????????
                        }
                    }
                    gridView.setAdapter(getMemberAdapter(showMemebers));
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


    /**
     * ????????????????????????
     *
     * @param groupId ??????????????????????????????id
     * @param staffId ?????????????????????????????????id
     */
    private void deleteMemberOfGroup(String groupId, String staffId) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? +
                "?groupId=" + groupId + "&staffIds=" + staffId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("?????????????????????");
                getGroupMembers();
                EventBus.getDefault().postSticky("?????????????????????");
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
     *
     * @param groupId ??????????????????????????????id
     */
    private void quiteGroup(String groupId) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.????????????????????? +
                "?groupId=" + groupId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("??????????????????");
//                getGroupMembers();
                removeCurrentConversition();
                EventBus.getDefault().postSticky("??????????????????");
                finish();
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
     *
     * @param staffId
     */
    private void addGroupMember(String staffId) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? +
                "?groupId=" + groupSession.getChatId() + "&staffIds=" + staffId;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                showShortToast("????????????");
                EventBus.getDefault().postSticky("?????????????????????");
                getGroupMembers();
                /**
                 * ??????????????????????????????
                 */
                List<GroupSession> sessions = preferencesHelper.getListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
                for (GroupSession session : sessions) {
                    if (TextUtils.isEmpty(session.getChatId()))
                        continue;
                    if (session.getChatId().equals(groupSession.getChatId())) {
                        session.setLastUpdateTime(ViewHelper.getCurrentFullTime());
                        break;
                    }
                }
                preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), sessions);
            }

            @Override
            public void onFailure(Request request, Exception ex) {

            }

            @Override
            public void onResponseCodeErro(String result) {
                showShortToast(JsonUtils.pareseMessage(result));
            }
        });
    }

    private CommanAdapter<GroupMembers> getMemberAdapter(List<GroupMembers> members) {
        return new CommanAdapter<GroupMembers>(members, mContext, R.layout.item_group_member) {
            @Override
            public void convert(int position, GroupMembers item, BoeryunViewHolder viewHolder) {
                if ("1".equals(item.getType())) {
                    viewHolder.setTextValue(R.id.tv_name, "");
                    viewHolder.setImageResoure(R.id.iv_head, R.drawable.ic_add);


                    /**
                     * ???????????????????????????
                     */
                    viewHolder.getView(R.id.iv_head).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ComponentName comp = new ComponentName(mContext, "com.biaozhunyuan.tianyi.notice.SelectedNotifierActivity");
                            Intent intent = new Intent();
                            intent.putExtra("isSingleSelect", false);
                            intent.putExtra("unClickAbleUsers", (Serializable) turnToUserList(groupMembers));
                            intent.setComponent(comp);
                            intent.setAction("android.intent.action.VIEW");
                            startActivityForResult(intent, REQUEST_CODE_SELECT_MEMBER);
                        }
                    });
                } else if ("2".equals(item.getType())) {
                    viewHolder.setTextValue(R.id.tv_name, "");
                    viewHolder.setImageResoure(R.id.iv_head, R.drawable.ico_delete);

                    /**
                     * ????????????
                     */
                    viewHolder.getView(R.id.iv_head).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, GroupMembersActivity.class);
                            intent.putExtra("isDeleteMembers", true);

                            //???????????????????????????????????????????????????
                            List<GroupMembers> membersList = new ArrayList<>();
                            membersList.addAll(groupMembers);
                            for (int i = 0; i < membersList.size(); i++) {
                                GroupMembers members1 = membersList.get(i);
                                if (members1.isManager()) {
                                    membersList.remove(i);
                                    break;
                                }
                            }
                            intent.putExtra("groupMembers", (Serializable) membersList);
                            startActivityForResult(intent, REQUEST_CODE_DELETE_MEMBER);
                        }
                    });
                } else {
                    viewHolder.setTextValue(R.id.tv_name, item.getName());
                    viewHolder.setUserPhoto(R.id.iv_head, item.getUuid());
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_MEMBER) {
                UserList userList = (UserList) data.getSerializableExtra("RESULT_SELECT_USER");
                String uuids = "";
                for (User members : userList.getUsers()) {
                    uuids += members.getUuid() + ",";
                }
                if (uuids.length() > 0) {
                    uuids = uuids.substring(0, uuids.length() - 1);
                }
                addGroupMember(uuids);
            } else if (requestCode == REQUEST_CODE_DELETE_MEMBER) {
                List<GroupMembers> list = (List<GroupMembers>) data.getSerializableExtra("deleteUsers");
                if (list != null) {
                    String uuids = "";
                    for (GroupMembers members : list) {
                        uuids += members.getUuid() + ",";
                    }
                    if (uuids.length() > 0) {
                        uuids = uuids.substring(0, uuids.length() - 1);
                        deleteMemberOfGroup(groupSession.getChatId(), uuids);
                    }
                }
            } else if (requestCode == REQUEST_CODE_UPDATE_NAME) {
                String name = data.getStringExtra("GroupName");
                if (!TextUtils.isEmpty(name)) {
                    updateGroupName(name);
                }
            }
        }
    }

    private List<User> turnToUserList(List<GroupMembers> list) {
        List<User> users = new ArrayList<>();

        for (GroupMembers members : list) {
            User user = new User();
            user.setUuid(members.getUuid());
            user.setName(members.getName());
            users.add(user);
        }
        return users;
    }


    /**
     * ??????????????????????????????
     *
     * @param list
     */
    private void sortListByTopTime(List<GroupSession> list) {
        Collections.sort(list, new Comparator<GroupSession>() {
            @Override
            public int compare(GroupSession o1, GroupSession o2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dt1 = format.parse(o1.getTopTime());
                    Date dt2 = format.parse(o2.getTopTime());
                    if (dt1.getTime() < dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() > dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param list
     */
    private void sortListByLastSendTime(List<GroupSession> list) {
        Collections.sort(list, new Comparator<GroupSession>() {
            @Override
            public int compare(GroupSession o1, GroupSession o2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dt1 = format.parse(o1.getLastMessageSendTime());
                    Date dt2 = format.parse(o2.getLastMessageSendTime());
                    if (dt1.getTime() > dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    /**
     * ??????????????????????????????
     */
    private void removeCurrentConversition() {
        List<GroupSession> sessions = preferencesHelper.getListData
                (Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
        for (int i = 0; i < sessions.size(); i++) {
            GroupSession session = sessions.get(i);
            if (groupSession.getChatId().equals(session.getChatId())) {
                sessions.remove(session);
                break;
            }
        }
        preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), sessions);
    }
}
