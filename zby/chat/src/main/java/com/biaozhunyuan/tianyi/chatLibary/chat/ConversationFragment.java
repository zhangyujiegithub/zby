package com.biaozhunyuan.tianyi.chatLibary.chat;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.biaozhunyuan.tianyi.chatLibary.chat.model.ChatMessage;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.GroupSession;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.MessageSendStatusEnum;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.RecentMessage;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.SharedPreferencesHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.model.user.User;
import com.biaozhunyuan.tianyi.common.utils.AnimUtil;
import com.biaozhunyuan.tianyi.common.utils.JsonUtils;
import com.biaozhunyuan.tianyi.common.view.DictIosPickerBottomDialog;
import com.example.chat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Request;

/**
 * Created by wangAnMin on 2018/4/19.
 * ????????????
 */

public class ConversationFragment extends Fragment {

    public static final String RESULT_SELECT_USER = "RESULT_SELECT_USER";
    public static final int REQUEST_SELECT_PARTICIPANT = 101; //???????????????
    private Context mContext;
    private NewsAdapter adapter;
    private SharedPreferencesHelper helper;
    private DictionaryHelper dictionaryHelper;
    private List<GroupSession> list;
    private DictIosPickerBottomDialog dialog;


    private ListView lv;
    private RelativeLayout rlSearch;
    private LinearLayout llDisconnect;
    private PopupWindow mPopupWindow;
    private AnimUtil animUtil;
    private ImageView ivDynamic; //??????
    private ImageView ivMutiChat; //????????????

    private static final long DURATION = 100;
    private static final float START_ALPHA = 1f;
    private static final float END_ALPHA = 1f;
    private float bgAlpha = 1f;
    private boolean bright = false;
    private TextView tv_talk_all;
    private TextView tv_talk_only;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, null);
        initViews(view);
        initData();
//        getSessionList();
        setOnEvent();
        return view;
    }

    private void initViews(View view) {
        mPopupWindow = new PopupWindow(getActivity());
        animUtil = new AnimUtil();
        lv = view.findViewById(R.id.lv_conversation_list);
        rlSearch = view.findViewById(R.id.rl_search_ico);
        llDisconnect = view.findViewById(R.id.ll_disconnect);
        ivDynamic = view.findViewById(R.id.iv_show_dynamic);
        ivMutiChat = view.findViewById(R.id.iv_create_muti_chat);
    }

    private void initData() {
        mContext = getActivity();
        dialog = new DictIosPickerBottomDialog(mContext);
        helper = new SharedPreferencesHelper(mContext);
        dictionaryHelper = new DictionaryHelper(mContext);
        updateLocalSessionList();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Event(String message) {
        if ("????????????".equals(message)) {
            llDisconnect.setVisibility(View.VISIBLE);
        } else if ("????????????".equals(message)) {
            llDisconnect.setVisibility(View.GONE);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Event(ChatMessage message) {
        if (message != null) {
            //?????????????????????????????????????????????????????????????????????????????????
            if ("sak".equalsIgnoreCase(message.getCmd())) {
                updateMsgStatusRecoverd(message);
            } else {
                boolean isSaveMsg = true;
                if (ChatMessage.FORMAT_VOICE.equals(message.getFormat())) {
                    message.setChatCategory(ChatMessage.CHAT_LEFT_AUDIO);
                } else if (ChatMessage.FORMAT_FILE.equals(message.getFormat())) {
                    message.setChatCategory(ChatMessage.CHAT_LEFT_FILE);
                } else if (ChatMessage.FORMAT_TIP.equals(message.getFormat())) {
                    //????????????
                    if (!TextUtils.isEmpty(message.getBody())
                            && message.getBody().contains("??????")) {
                        HashMap<String, RecentMessage> msgs = helper.getHashMapData
                                (Global.mUser.getUuid() + "ChatRecord", RecentMessage.class);
                        RecentMessage message1 = msgs.get(message.getChatId());
                        for (ChatMessage msg : message1.getRecentMessages()) {
                            if (msg.getId().equals(message.getMessageId())) {
                                msg.setCmd(ChatMessage.FORMAT_TIP);
                                msg.setChatCategory(ChatMessage.CHAT_TIP);
                                msg.setBody(message.getBody());
                                isSaveMsg = false;
                                break;
                            }
                        }
                        helper.putHashMapData(Global.mUser.getUuid() + "ChatRecord", msgs);
                    }
                } else {
                    message.setChatCategory(ChatMessage.CHAT_Left);
                }
                if (!TextUtils.isEmpty(message.getBody())) {
                    message.setBody(message.getBody().replaceAll("\n", ""));
                }
                if (TextUtils.isEmpty(message.getChatId())) {
                    message.setChatId(UUID.randomUUID().toString().replaceAll("-", ""));
                }

                //????????????,????????????
                if (message.getKeyValues() != null && !"1".equals(message.getKeyValues().get("isSingle"))) {
                    if (!TextUtils.isEmpty(message.getGroupIcon())) {
                        message.setAvatar(message.getGroupIcon());
                    }
                }
                if (isSaveMsg) {
                    boolean isShock = true;
                    //???????????????????????????????????????????????????????????????
                    for (GroupSession session : list) {
                        if (session.getChatId().equals(message.getChatId())) {
                            isShock = session.isSetNoInterrupt();
                            break;
                        }
                    }
                    MsgCacheManager.saveMessage(mContext, message, !isShock);
                }
                MsgCacheManager.setMessageRead(message.getId(), message.getChatId());
                SessionCacheManger.saveSession(mContext, "", message);
                refreshData();
                refreshUnreadCount();
            }
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveUnReadMsgEvent(String status) {
        if ("505".equals(status)) {
            if (adapter != null) {
                refreshData();
                refreshUnreadCount();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        refreshData();
        refreshUnreadCount();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            refreshData();
        }
    }


    private void showPop() {
        // ??????????????????
        mPopupWindow.setContentView(LayoutInflater.from(getActivity()).inflate(R.layout.pop_add, null));
        // ????????????????????????????????????????????????????????????????????????
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // ??????pop????????????
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // ??????pop????????????
        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // ??????pop????????????????????????false?????????????????????????????????Activity?????????pop??????Editor?????????focusable????????????true
        mPopupWindow.setFocusable(true);
        // ??????pop???????????????false??????????????????????????????true
        mPopupWindow.setTouchable(true);
        // ????????????pop????????????????????????false??????focusable???true???????????????????????????
        mPopupWindow.setOutsideTouchable(true);
        // ????????? + ??????????????????????????????????????????
        mPopupWindow.showAsDropDown(ivMutiChat, 40, 0);
        // ??????pop??????????????????????????????????????????
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                toggleBright();
            }
        });

        tv_talk_all = mPopupWindow.getContentView().findViewById(R.id.tv_talk_all);//????????????
        tv_talk_only = mPopupWindow.getContentView().findViewById(R.id.tv_talk_only);//????????????

        tv_talk_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName comp = new ComponentName(mContext, "com.biaozhunyuan.tianyi.notice.SelectedNotifierActivity");
                Intent intent = new Intent();
                intent.putExtra("isSingleSelect", false);
                intent.putExtra("intent_tag", "chatActivity");
                List<User> list = new ArrayList<>();
                list.add(Global.mUser);
                //????????????????????? ??????????????????
                intent.putExtra("unClickAbleUsers", (Serializable) list);
                intent.putExtra("title", "????????????");
                intent.setComponent(comp);
                intent.setAction("android.intent.action.VIEW");
                startActivityForResult(intent, REQUEST_SELECT_PARTICIPANT);
                mPopupWindow.dismiss();
            }
        });

        tv_talk_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupSessionActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_PARTICIPANT);
                mPopupWindow.dismiss();
            }
        });

    }

    private void toggleBright() {
        // ????????????????????????????????? ????????? ??????????????????????????????????????????????????????0.5f--1f???
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // ?????????????????????????????????????????????
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
        // ???????????????????????????????????????????????????????????????
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void refreshData() {
        updateLocalSessionList();
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void getSessionList() {
        String url = Global.BASE_JAVA_URL + GlobalMethord.??????????????????;

        StringRequest.getAsyn(url, new StringResponseCallBack() {
            @Override
            public void onResponse(String response) {
                List<GroupSession> groupSessions = JsonUtils.jsonToArrayEntity(JsonUtils.pareseData(response), GroupSession.class);
                if (groupSessions != null) {
                    for (GroupSession groupSession : groupSessions) {
                        for (GroupSession session : list) {
                            if (session.getChatId().equals(groupSession.getUuid())) {
                                if (TextUtils.isEmpty(session.getAvatar())) {
                                    session.setAvatar(groupSession.getIcon());
                                }
                            }
                        }
                    }
                    helper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), list);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
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


    private void setOnEvent() {

        adapter.setOnItemClickListener(new NewsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(GroupSession item) {
                item.setUnreadCount(0);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("chatUser", item);


                //?????????????????????0
                List<GroupSession> sessions = helper.getListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
                for (GroupSession session : sessions) {
                    if (session.getChatId() == null)
                        continue;
                    if (session.getChatId().equals(item.getChatId())) {
                        session.setUnreadCount(0);
                        session.setAtType(0);
                        break;
                    }
                }
                refreshUnreadCount();
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (EventBus.getDefault().isRegistered(ConversationFragment.this)) {
                    EventBus.getDefault().unregister(ConversationFragment.this);
                }
                helper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), sessions);
                startActivity(intent);
            }
        });

        adapter.setOnDeleteItemListener(new NewsAdapter.DeleteItemListener() {
            @Override
            public void onDeleteItem(GroupSession session) {
                list.remove(session);
                HashMap<String, RecentMessage> hashMapData = helper.getHashMapData
                        (Global.mUser.getUuid() + "ChatRecord", RecentMessage.class);
                RecentMessage recentMessage = hashMapData.get(session.getChatId());
                if (recentMessage != null) {
                    hashMapData.remove(session.getChatId());
                }
                helper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), list);
                helper.putHashMapData(Global.mUser.getUuid() + "ChatRecord", hashMapData);
                updateLocalSessionList();
                refreshUnreadCount();
            }
        });

        ivDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ComponentName comp = new ComponentName(mContext, "com.biaozhunyuan.tianyi.dynamic.DynamicActivity");
                Intent intent = new Intent();
                intent.setComponent(comp);
                startActivity(intent);
            }
        });

        ivMutiChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
                toggleBright();
            }
        });

        /**
         * ?????? ??????????????????????????????
         */
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchAllActivity.class));
            }
        });
    }


    UnRedCountChangeListener redCountChangeListener;

    public interface UnRedCountChangeListener {
        void UnRedCountChange(int count);
    }

    public void setOnUnRedCountChangeListener(UnRedCountChangeListener listener) {
        redCountChangeListener = listener;
    }


    //??????????????????
    private void refreshUnreadCount() {
        List<GroupSession> messages = helper.getListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);

        int unreadCount = 0;
        for (GroupSession message : messages) {
            if (message.getUnreadCount() > 0 && !message.isSetNoInterrupt()) {
                unreadCount += message.getUnreadCount();
            }
        }
        if (redCountChangeListener != null)
            redCountChangeListener.UnRedCountChange(unreadCount);
    }


    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     *
     * @param msg
     */
    private void updateMsgStatusRecoverd(ChatMessage msg) {
        HashMap<String, RecentMessage> hashMapData = helper.getHashMapData
                (Global.mUser.getUuid() + "ChatRecord", RecentMessage.class);

        for (Map.Entry<String, RecentMessage> entry : hashMapData.entrySet()) {
            for (ChatMessage message : entry.getValue().getRecentMessages()) {
                if (message.getId().equals(msg.getId())) {
                    message.setSendStatus(MessageSendStatusEnum.????????????.getStatus());
                    helper.putHashMapData(Global.mUser.getUuid() + "ChatRecord", hashMapData);
                    break;
                }
            }
        }
    }


    /**
     * ?????????????????? ???????????????/????????????
     */
    private void updateLocalSessionList() {
        list = helper.getListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), GroupSession.class);
        List<GroupSession> topList = new ArrayList<>(); //?????????????????????
        List<GroupSession> unTopList = new ArrayList<>(); //????????????????????????
        for (GroupSession list1 : list) {
            if (list1.isTop()) {
                topList.add(list1);
            } else {
                unTopList.add(list1);
            }
        }
        //??????????????????????????????????????????
        sortListByTopTime(topList);
        //??????????????????????????????????????????????????????????????????
        sortListByLastSendTime(unTopList);
        list.clear();
        list.addAll(topList);
        list.addAll(unTopList);
//        helper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), lists);

        if (adapter == null) {
            adapter = new NewsAdapter(mContext, list);
            lv.setAdapter(adapter);
        } else {
            adapter.setData(list);
        }
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
}
