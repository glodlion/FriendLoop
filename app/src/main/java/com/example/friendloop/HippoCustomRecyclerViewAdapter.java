package com.example.friendloop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HippoCustomRecyclerViewAdapter extends RecyclerView.Adapter<HippoCustomRecyclerViewAdapter.ViewHolder> {
    public static boolean bIfDebug = true;
    public static String TAG = "HIPPO_DEBUG";
    private static Context mContext; //存GDD01.class的context
    private ArrayList<Friend> mDataSet = new ArrayList<Friend>();

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mFriendName;
        private final ImageView mFriendPicture;
        private final ImageView mMore;

        public ViewHolder(View view)
        {
            super(view);
            //  設計RecyclerView中點選RecyclerView.ViewHolder的項目，以Toast顯示訊息：「你點選的是第 xx 部電影」
            // TO DO
            //因為是在另一個xml，因此findViewById需要前面加上view
            mFriendName = (TextView) view.findViewById(R.id.friendName);
            mFriendPicture = (ImageView) view.findViewById(R.id.friendPicture);
            mMore = (ImageView) view.findViewById(R.id.more);

            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "點擊了更多", Toast.LENGTH_LONG).show();
                }
            });

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String pos = v.getTag().toString();
//                    String msg = String.format("你點選的是第 %s 部電影", pos);
//                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
//                }
//            });
        }

        public TextView getFriendName()
        {
            return mFriendName;
        }

        public ImageView getFriendPicture()
        {
            return mFriendPicture;
        }

        public ImageView getMore()
        {
            return mMore;
        }
    }

    // Constructor
    public HippoCustomRecyclerViewAdapter(Context context, ArrayList<Friend> mDataSet)
    {
        this.mContext = context;
        this.mDataSet = mDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_friend_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        // 指派ViewHolder物件，重複使用，動態載入電影名稱(TextView)及圖片Resource ID(ImageView)
        // TO DO
        viewHolder.itemView.setTag(String.valueOf(position)); //標註Tag
        Friend friend = mDataSet.get(position);
        viewHolder.mFriendName.setText(friend.getName());
        viewHolder.mFriendPicture.setImageURI(friend.getPicture());
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }
}
