package com.example.friendloop;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HippoCustomRecyclerViewAdapter extends RecyclerView.Adapter<HippoCustomRecyclerViewAdapter.ViewHolder> {
    private static Context mContext; //存GDD01.class的context
    private ArrayList<Friend> mDataSet = new ArrayList<Friend>();

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView mFriendName, mFriendIntimacy;
        private final ImageView mFriendPicture;
        private final ImageView mMore;

        public ViewHolder(View view, HippoCustomRecyclerViewAdapter adapter)
        {
            super(view);
            //因為是在另一個xml，因此findViewById需要前面加上view
            mFriendName = (TextView) view.findViewById(R.id.friendName);
            mFriendIntimacy = (TextView) view.findViewById(R.id.friendIntimacy);
            mFriendPicture = (ImageView) view.findViewById(R.id.friendPicture);
            mMore = (ImageView) view.findViewById(R.id.more);

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
                                // 建立 AlertDialog.Builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                // 載入自訂的佈局
                                LayoutInflater inflater = LayoutInflater.from(mContext);
                                View customView = inflater.inflate(R.layout.breakornot, null);

                                // 設置自訂佈局到 AlertDialog
                                builder.setView(customView);

                                // 取得自訂佈局的元件
                                // 建立對話框
                                AlertDialog dialog = builder.create();
                                TextView alertTitle = customView.findViewById(R.id.alertTitle);
                                ImageButton positiveButton = customView.findViewById(R.id.positiveButton);
                                ImageButton negativeButton = customView.findViewById(R.id.negativeButton);

                                // 設置按鈕行為
                                positiveButton.setOnClickListener(v -> {
                                    // 執行刪除好友的操作
                                    SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(mContext);
                                    dbHelper.deleteContactAndResort(position + 1);
                                    adapter.removeItem(position);
                                    ((MainActivity) mContext).initRecyclerView();
                                    ((MainActivity) mContext).initRecycleView();

                                    // 關閉對話框
                                    dialog.dismiss();
                                });

                                negativeButton.setOnClickListener(v -> {
                                    // 按下 No 時關閉對話框
                                    dialog.dismiss();
                                });

                                dialog.show();

                            } else if(item.getItemId() == R.id.option3){
                                SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(mContext);
                                Cursor cursor = dbHelper.getContactById(position + 1);
                                if (cursor != null && cursor.moveToFirst()) {
                                    int nameIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_NAME);
                                    int phoneIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PHONE);
                                    int birthdayIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_BIRTHDAY);
                                    int preferenceIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_PREFERENCES);
                                    int pictureIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_IMAGE_URI);
                                    int intimacyIndex = cursor.getColumnIndex(SqlDataBaseHelper.COLUMN_INTIMACY);
                                    String name = nameIndex < 0 ? "Default Value" : cursor.getString(nameIndex);
                                    String phone = phoneIndex < 0 ? "Default Value" : cursor.getString(phoneIndex);
                                    String birthday = birthdayIndex < 0 ? "Default Value" : cursor.getString(birthdayIndex);
                                    String preference = preferenceIndex < 0 ? "Default Value" : cursor.getString(preferenceIndex);
                                    String picture = pictureIndex < 0 ? "Default Value" : cursor.getString(pictureIndex);
                                    String intimacy = intimacyIndex < 0 ? "Default Value" : cursor.getString(intimacyIndex);
                                    int intimacyValue = Integer.valueOf(intimacy);

                                    if (intimacyValue < 100){
                                        intimacyValue += 10;
                                    }

                                    if(intimacyValue > 100){
                                        intimacyValue = 100;
                                    }

                                    Friend friend = new Friend();
                                    friend.setName(name);
                                    friend.setPhone(phone);
                                    friend.setBirthday(birthday);
                                    friend.setPreferences(preference);
                                    friend.setPicture(picture);
                                    friend.setIntimacy(String.valueOf(intimacyValue));

                                    dbHelper.updateContact(position+1, friend);
                                }
                                ((MainActivity) mContext).initRecyclerView();
                                ((MainActivity) mContext).initRecycleView();
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
    }

    // Constructor
    public HippoCustomRecyclerViewAdapter(MainActivity context, ArrayList<Friend> mDataSet)
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
        viewHolder.itemView.setTag(String.valueOf(position)); //標註Tag
        Friend friend = mDataSet.get(position);
        viewHolder.mFriendName.setText(friend.getName());
        Glide.with(viewHolder.itemView.getContext())
                .load(Uri.parse(friend.getPicture()))  // 這是圖片的 URI
                .override(500, 500)
                .into(viewHolder.mFriendPicture);  // 設置圖片到 ImageView
        viewHolder.mFriendIntimacy.setText(friend.getIntimacy());
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mDataSet.size()) {
            // 從資料集中刪除指定位置的項目
            mDataSet.remove(position);

            // 通知 RecyclerView 項目已被刪除
            notifyItemRemoved(position);

            // 重新整理其後的所有項目索引
            notifyItemRangeChanged(position, mDataSet.size());
        }
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }
}
