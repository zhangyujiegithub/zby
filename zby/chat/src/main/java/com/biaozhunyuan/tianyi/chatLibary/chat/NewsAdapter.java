package com.biaozhunyuan.tianyi.chatLibary.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.biaozhunyuan.tianyi.chatLibary.chat.model.ChatMessage;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.GroupSession;
import com.biaozhunyuan.tianyi.chatLibary.chat.model.MessageSendStatusEnum;
import com.biaozhunyuan.tianyi.common.global.Global;
import com.biaozhunyuan.tianyi.common.global.GlobalMethord;
import com.biaozhunyuan.tianyi.common.helper.DateDeserializer;
import com.biaozhunyuan.tianyi.common.helper.DictionaryHelper;
import com.biaozhunyuan.tianyi.common.helper.SharedPreferencesHelper;
import com.biaozhunyuan.tianyi.common.helper.ViewHelper;
import com.biaozhunyuan.tianyi.common.http.StringRequest;
import com.biaozhunyuan.tianyi.common.http.StringResponseCallBack;
import com.biaozhunyuan.tianyi.common.utils.ImageUtils;
import com.biaozhunyuan.tianyi.common.view.AvatarImageView;
import com.biaozhunyuan.tianyi.common.view.SwipeLayout;
import com.example.chat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Request;

public class NewsAdapter extends BaseAdapter {
    protected static final String TAG = "NewsAdapter";
    private Context mContext;
    private List<GroupSession> lists;
    private DictionaryHelper helper;
    private SharedPreferencesHelper preferencesHelper;
    private ItemClickListener itemClickListener;
    private DeleteItemListener deleteItemListener;

    public NewsAdapter(Context context, List<GroupSession> lists) {
        this.mContext = context;
        this.lists = lists;
        helper = new DictionaryHelper(context);
        preferencesHelper = new SharedPreferencesHelper(context);
    }

    public void setData(List<GroupSession> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (lists != null) {
            return lists.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final GroupSession chat = lists.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.fragment_news_item,
                    null);
            holder = new Holder();
            holder.iv = (AvatarImageView) convertView.findViewById(R.id.user_picture);
            holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            holder.ivNoDisturb = (ImageView) convertView.findViewById(R.id.iv_no_disturb);
            holder.tv_name = (TextView) convertView
                    .findViewById(R.id.user_name);
            holder.swipeLayout = convertView.findViewById(R.id.swipe_view);
            holder.tv_tag = convertView.findViewById(R.id.tv_tag);
            holder.tv_count_unread_no_disturb = convertView.findViewById(R.id.tv_count_unread_no_disturb);
            holder.redNoDisturb = convertView.findViewById(R.id.view_red_no_disturb);
            holder.tv_feel = (TextView) convertView
                    .findViewById(R.id.user_feel);
            holder.tv_unRead = convertView
                    .findViewById(R.id.red_point_attendance);
            holder.tv_time = (TextView) convertView
                    .findViewById(R.id.user_time);
            holder.tvAt = (TextView) convertView
                    .findViewById(R.id.tv_at);
            holder.tv_top = (TextView) convertView
                    .findViewById(R.id.tv_top);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (chat.isQuite()) {
            holder.tv_name.setText(chat.getName() + "(?????????)");
        } else {
            holder.tv_name.setText(chat.getName());
        }
        if (!TextUtils.isEmpty(chat.getAvatar())) {
            if (chat.isSingle() == 1) {
                ImageUtils.displyUserPhotoById(mContext, chat.getAvatar(), holder.iv);
            } else {
                ImageUtils.displyImageById(chat.getAvatar(), holder.iv);
            }
        } else {
            holder.iv.setImageResource(R.drawable.icon_head_group);
        }


        //???????????????
        if (chat.isSetNoInterrupt()) {
            holder.tv_unRead.setVisibility(View.INVISIBLE);
            holder.ivNoDisturb.setVisibility(View.VISIBLE);
            if (chat.getUnreadCount() > 0) {
                holder.redNoDisturb.setVisibility(View.VISIBLE);
                holder.tv_count_unread_no_disturb.setVisibility(View.VISIBLE);
                holder.tv_count_unread_no_disturb.setText("[" + chat.getUnreadCount() + "???]");
            } else {
                holder.redNoDisturb.setVisibility(View.GONE);
                holder.tv_count_unread_no_disturb.setVisibility(View.GONE);
            }
        } else {
            holder.redNoDisturb.setVisibility(View.GONE);
            holder.ivNoDisturb.setVisibility(View.GONE);
            if (chat.getUnreadCount() > 0) {
                holder.tv_unRead.setVisibility(View.VISIBLE);
                holder.tv_unRead.setText(chat.getUnreadCount() + "");
                if (chat.getUnreadCount() > 99) {
                    holder.tv_unRead.setText("99+");
                }
            } else {
                holder.tv_unRead.setVisibility(View.INVISIBLE);
            }
        }


        //????????????
        if (chat.isDepartMent() == 1) {
            holder.tv_tag.setVisibility(View.VISIBLE);
        } else {
            holder.tv_tag.setVisibility(View.GONE);
        }


        //????????????
        if (chat.isTop()) {
            holder.tv_top.setText("????????????");
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.chat_back));
        } else {
            holder.tv_top.setText("??????");
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_list_item));
        }

        if (chat.getSendStatus() == MessageSendStatusEnum.?????????.getStatus()) {
            holder.iv_status.setVisibility(View.VISIBLE);
            holder.iv_status.setImageResource(R.drawable.icon_sending);
        } else if (chat.getSendStatus() == MessageSendStatusEnum.????????????.getStatus()) {
            holder.iv_status.setVisibility(View.VISIBLE);
            holder.iv_status.setImageResource(R.drawable.icon_warnning);
        } else {
            holder.iv_status.setVisibility(View.GONE);
        }
        if ("img".equalsIgnoreCase(chat.getLastMessageFormat())) {
            holder.tv_feel.setText("[??????]");
        } else if (ChatMessage.FORMAT_VOICE.equalsIgnoreCase(chat.getLastMessageFormat())) {
            holder.tv_feel.setText("[??????]");
        } else if (ChatMessage.FORMAT_FILE.equalsIgnoreCase(chat.getLastMessageFormat())) {
            holder.tv_feel.setText("[??????]");
        } else {
            if (ChatMessage.FORMAT_TIP.equals(chat.getLastMessageFormat())) {
                String body = chat.getLastMessage();
                if (!TextUtils.isEmpty(body) && body.contains(Global.mUser.getName())) {
                    body = body.replace(Global.mUser.getName(), "???");
                }
                holder.tv_feel.setText(body);
            } else {
                holder.tv_feel.setText(chat.getLastMessage());
            }
        }
        holder.tv_time.setText(DateDeserializer.LongFormatTime(chat.getLastMessageSendTime()));


        if (chat.getAtType() != 0) {
            holder.tvAt.setVisibility(View.VISIBLE);
            if (chat.getAtType() == 1) {
                holder.tvAt.setText("[??????@???]");
            } else if (chat.getAtType() == 2) {
                holder.tvAt.setText("[??????@?????????]");
            }
        } else {
            holder.tvAt.setVisibility(View.GONE);
        }


        /**
         * ??????????????????
         */
        holder.swipeLayout.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(chat);
                }
            }
        });


        //??????/????????????
        holder.swipeLayout.getDeleteView().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat.isTop()) {
                    cancleTopChat(chat.getChatId());
                    chat.setTop(false);
                    chat.setTopTime("");
                    updateLocalSessionList(chat);
                } else {
                    setTopChat(chat.getChatId());
                    chat.setTop(true);
                    chat.setTopTime(ViewHelper.getCurrentFullTime());
                    updateLocalSessionList(chat);
                }
            }
        });

        holder.swipeLayout.getDeleteView().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteItemListener != null) {
                    deleteItemListener.onDeleteItem(chat);
                }
            }
        });
        return convertView;
    }

    class Holder {
        AvatarImageView iv;
        TextView tv_name;
        TextView tv_feel;
        TextView tv_time;
        TextView tvAt;
        TextView tv_top;
        TextView tv_unRead;
        TextView tv_count_unread_no_disturb;
        ImageView iv_status;
        ImageView ivNoDisturb;
        TextView tv_tag;
        View redNoDisturb;
        SwipeLayout swipeLayout;
    }


    /**
     * ????????????
     */
    interface ItemClickListener {
        void onItemClick(GroupSession session);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * ????????????
     */
    interface DeleteItemListener {
        void onDeleteItem(GroupSession session);
    }

    public void setOnDeleteItemListener(DeleteItemListener deleteItemListener) {
        this.deleteItemListener = deleteItemListener;
    }


    /**
     * ??????????????????
     *
     * @param groupId ??????id
     */
    private void setTopChat(String groupId) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.???????????? + "?groupId=" + groupId;

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
     *
     * @param groupId ??????id
     */
    private void cancleTopChat(String groupId) {
        String url = Global.BASE_JAVA_URL + GlobalMethord.?????????????????? + "?groupId=" + groupId;

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
     * ?????????????????? ???????????????/????????????
     *
     * @param session ?????????/?????????????????????
     */
    private void updateLocalSessionList(GroupSession session) {
        List<GroupSession> topList = new ArrayList<>(); //?????????????????????
        List<GroupSession> unTopList = new ArrayList<>(); //????????????????????????
        for (GroupSession list : lists) {
            if (list.isTop()) {
                topList.add(list);
            } else {
                unTopList.add(list);
            }
        }
        //??????????????????????????????????????????
        sortListByTopTime(topList);
        //??????????????????????????????????????????????????????????????????
        sortListByLastSendTime(unTopList);
        lists.clear();
        lists.addAll(topList);
        lists.addAll(unTopList);
        notifyDataSetChanged();
        preferencesHelper.putListData(Global.mUser.getCorpId() + Global.mUser.getUuid(), lists);
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
