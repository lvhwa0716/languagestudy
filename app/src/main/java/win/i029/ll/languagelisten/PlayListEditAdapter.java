package win.i029.ll.languagelisten;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lvh on 6/28/17.
 */

public class PlayListEditAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private PlayControl.PlaylistItem mPlaylistItem;
    private int mOperateType;

    public PlayListEditAdapter(Context context , PlayControl.PlaylistItem pli, int type) {
        this.mInflater = LayoutInflater.from(context);
        mPlaylistItem = pli;
        mOperateType = type;
    }


    @Override
    public int getCount() {
        return mPlaylistItem.getCount();
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            contentView = mInflater.inflate(R.layout.playlist_edit, null);
            holder = new ViewHolder();

            holder.title = (TextView) contentView.findViewById(R.id.playlist_edit_ItemTitle);
            holder.remove = (ImageButton) contentView.findViewById(R.id.playlist_edit_Remove);
            holder.up = (ImageButton) contentView.findViewById(R.id.playlist_edit_up);
            holder.down = (ImageButton) contentView.findViewById(R.id.playlist_edit_down);

            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        holder.title.setText(mPlaylistItem.getShowText(position));

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaylistItem.remove(position);
                PlayListEditAdapter.this.notifyDataSetChanged();
            }
        });

        holder.up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaylistItem.moveup(position);
                PlayListEditAdapter.this.notifyDataSetChanged();
            }
        });

        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaylistItem.movedown(position);
                PlayListEditAdapter.this.notifyDataSetChanged();
            }
        });

        if( mPlaylistItem.getActiveItemPos() == position ) {
            holder.title.setTextColor(0xFF99CC33);
        } else {
            holder.title.setTextColor(0xFF000000);
        }


        holder.remove.setFocusable(false); // enable ListView OnItemClickListener
        switch(mOperateType) {
            case PlayControl.ItemOperate_Play:
                holder.remove.setVisibility(View.GONE);
                holder.up.setVisibility(View.GONE);
                holder.down.setVisibility(View.GONE);
                break;
            default:
                holder.remove.setVisibility(View.VISIBLE);
                holder.up.setVisibility(View.VISIBLE);
                holder.down.setVisibility(View.VISIBLE);
                break;
        }
        return contentView;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public final class ViewHolder {
        public TextView title;
        public ImageButton up;
        public ImageButton down;
        public ImageButton remove;
    }

    public void addItems(ArrayList<String> fl) {
        mPlaylistItem.append(fl);
        notifyDataSetChanged();
    }

    public PlayControl.PlaylistItem getPlaylistItem() {
        return mPlaylistItem;
    }
}

