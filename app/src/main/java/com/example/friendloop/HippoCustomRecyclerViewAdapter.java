package com.example.friendloop;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
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
//        private final LinearLayout mListItem;

        public ViewHolder(View view, HippoCustomRecyclerViewAdapter adapter)
        {
            super(view);
            //  設計RecyclerView中點選RecyclerView.ViewHolder的項目，以Toast顯示訊息：「你點選的是第 xx 部電影」
            // TO DO
            //因為是在另一個xml，因此findViewById需要前面加上view
            mFriendName = (TextView) view.findViewById(R.id.friendName);
            mFriendPicture = (ImageView) view.findViewById(R.id.friendPicture);
            mMore = (ImageView) view.findViewById(R.id.more);
//            mListItem = (LinearLayout) view.findViewById(R.id.list_item);

            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (position == RecyclerView.NO_POSITION) {
                        // 如果位置無效，直接返回
                        return;
                    }

                    // 創建 PopupMenu，將其與當前的mMore ImageView綁定
                    PopupMenu popup = new PopupMenu(mContext, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    // 為PopupMenu加載菜單
                    inflater.inflate(R.menu.dot_menu, popup.getMenu());

                    // 設置菜單項目的點擊事件
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Log.d("DEBUG", "TEST: " +item.getItemId());
                            if(item.getItemId() == R.id.option1){
                                Toast.makeText(mContext, "查看好友資訊", Toast.LENGTH_LONG).show();
                                SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("state", 2);
                                editor.apply();
                                String pos = String.valueOf(position);
                                Bundle bundle = new Bundle();
                                bundle.putInt("pos", Integer.parseInt(pos));
                                Intent intent = new Intent(mContext, PersonalActivity.class);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            } else if(item.getItemId() == R.id.option2){
                                Toast.makeText(mContext, "Break", Toast.LENGTH_LONG).show();
                                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                        .setTitle("你確定要\"Break\"好友嗎?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(mContext);
                                                dbHelper.deleteContactAndResort(position+1);
                                                adapter.removeItem(position);
                                                ((MainActivity) mContext).initRecyclerView();
                                                ((MainActivity) mContext).initRecycleView();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }

                            return false;
                        }
                    });

                    // 顯示PopupMenu
                    popup.show();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("state", 2);
                    editor.apply();
                    String pos = v.getTag().toString();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", Integer.parseInt(pos));
                    Intent intent = new Intent(mContext, PersonalActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

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
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        // 指派ViewHolder物件，重複使用，動態載入電影名稱(TextView)及圖片Resource ID(ImageView)
        // TO DO
        viewHolder.itemView.setTag(String.valueOf(position)); //標註Tag
        Friend friend = mDataSet.get(position);
        viewHolder.mFriendName.setText(friend.getName());
        Glide.with(viewHolder.mFriendPicture.getContext())
                .load(Uri.parse(friend.getPicture()))  // 這是圖片的 URI
                .override(500, 500)
                .into(viewHolder.mFriendPicture);  // 設置圖片到 ImageView
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mDataSet.size()) {
            // 從資料集中刪除指定位置的項目
            mDataSet.remove(position);

            // 通知 RecyclerView 項目已被刪除
            notifyItemRemoved(position);

            // 如果你希望 RecyclerView 重新整理其後的所有項目索引
            notifyItemRangeChanged(position, mDataSet.size());
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }
}
