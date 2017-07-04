package win.i029.ll.languagelisten;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Created by lvh on 6/28/17.
 */

public class PlayControl {
    private static String PLAYCONTROL_FILE;

    public final static String KEY_FileSelected = "FileSelected";
    public final static String KEY_ItemOperationType = "ItemOperate";
    public final static int ItemOperate_Edit = 1;
    public final static int ItemOperate_Play = 2;
    public final static int ItemOperate_New = 3;

    public final static String KEY_ItemTitle = "ItemTitle";
    public final static String KEY_ItemIndex = "ItemIndex";
    public final static String KEY_SelectType = "SelectType";
    public final static int SelectType_Files = 1;
    public final static int SelectType_Folds = 2;

    PlayControlInternal mPlayControlInternal = null;

    private int mActiveListPos = 0;

    private static final PlayControl mInstance = new PlayControl();



    public static PlayControl getInstance() {
        return mInstance;
    }

    private ArrayList<PlaylistItem> getPL() {
        return mPlayControlInternal.getPlayList();
    }
    private PlayControl() {
        // Load history for SharedPreference
        PLAYCONTROL_FILE = MainActivity.mStorePath + "/PlayControl.json";

        mPlayControlInternal = PlayControlInternal.load( PLAYCONTROL_FILE);
    }

    public int getActiveListPos() {
        return mActiveListPos;
    }
    public void setActiveListPos(int index) {
        mActiveListPos = index;
    }

    public PlaylistItem getPlaylistItem(String title) {
        for(PlaylistItem tmp:getPL()){
            if( title.equals(tmp.getTitle())) {
                return tmp;
            }
        }
        return null;
    }



    public int getPlaylistCount() {
        return getPL().size();
    }

    public void delPlaylistItem(int pos) {
        if( (pos >= 0) && (pos < getPL().size()) ) {
            getPL().remove(pos);
            mPlayControlInternal.save(PLAYCONTROL_FILE);
        }
    }

    public void addPlaylistItem(PlaylistItem pi) {
        getPL().add(pi);
        mPlayControlInternal.save(PLAYCONTROL_FILE);
    }

    public void updatePlaylistItem(PlaylistItem pi) {
        Log.e("PlayControl" , " updatePlaylistItem do nothing");
        mPlayControlInternal.save(PLAYCONTROL_FILE);
    }

    public PlaylistItem getPlaylistItem(int pos) {
        return getPL().get(pos);
    }




    public void loadHistory(SharedPreferences history) {
        mActiveListPos = history.getInt("ActiveListPos" , 0);
    }

    public void saveHistory(SharedPreferences history) {

        SharedPreferences.Editor mEditor = history.edit();
        mEditor.putInt("ActiveListPos", mActiveListPos);
        mEditor.commit();

        for(PlaylistItem pi : getPL()) {
            if(pi.needSave() ) {
                mPlayControlInternal.save(PLAYCONTROL_FILE);
                break;
            }
        }
    }
    public static class PlayControlInternal {

        private ArrayList<PlaylistItem> mPlayList = new ArrayList<PlaylistItem>();
        public PlayControlInternal() {
        }
        public ArrayList<PlaylistItem> getPlayList() {
            return mPlayList;
        }
        public static PlayControlInternal load(String path) {

            try{
                Reader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
                Gson gson = new GsonBuilder().create();
                PlayControlInternal p = gson.fromJson(reader, PlayControlInternal.class);
                reader.close();
                return p;
            } catch (Exception e) {
                Log.e("PlayControl", e.toString());
                return new PlayControlInternal();
            }

        }

        public void save( String path) {
            try{
                Writer writer = new OutputStreamWriter(new FileOutputStream(path) , "UTF-8");
                Gson gson = new GsonBuilder().create();
                gson.toJson(this,writer);
                writer.flush();
                writer.close();
                for(PlaylistItem pi : mPlayList) {
                    pi.needSave(); // clear all flags
                }
            } catch (Exception e) {
                Log.e("PlayControl" , "save error");
            }
        }
    }
    public static class PlaylistItem {
        private String mTitle = "";
        private int mActiveItemPos = 0;
        private ArrayList<String> mItemList = new ArrayList<>();
        private boolean isChanged = false;
        public PlaylistItem() {

        }
        public void setActiveItemPos(int index) {
            mActiveItemPos = index;
            isChanged = true;
        }
        public int getActiveItemPos() {
            if((mActiveItemPos >= 0) && (mActiveItemPos < mItemList.size() ) ) {
                return mActiveItemPos;
            } else {
                return 0;
            }
        }

        public void setTitle(String title) {
            mTitle = title;
            isChanged = true;
        }

        public String getTitle() {
            return mTitle;
        }

        public int getCount() {
            return mItemList.size();
        }

        public String getItem(int pos) {
            if(pos >= mItemList.size() || (pos < 0))
                return "";
            return mItemList.get(pos);
        }

        public String getShowText(int pos) {

            String n = mItemList.get(pos);
            if(n.isEmpty() ) {
                return n;
            }
            int index = n.lastIndexOf('/');
            if(index > 0) {
                if(index < n.length() - 1) {
                    return n.substring(index + 1);
                } else {
                    Log.e("getShowText", " File Name Error");
                }
            }
            return n;


        }


        public void append(ArrayList<String> items) {
            mItemList.addAll(items);
            isChanged = true;
        }

        public void append(String item) {
            mItemList.add(item);
            isChanged = true;
        }

        public void remove(int pos) {
            mItemList.remove(pos);
            isChanged = true;
        }

        public boolean moveup(int pos) {
            if(pos > 0) {
                String item = mItemList.get(pos);
                mItemList.remove(pos);
                mItemList.add(pos -1 , item);
                isChanged = true;
                return true;
            }
            return false;
        }

        public boolean movedown(int pos) {
            if(pos < mItemList.size() - 1 ) {
                String item = mItemList.get(pos);
                mItemList.remove(pos);
                mItemList.add(pos + 1 , item);
                isChanged = true;
                return true;
            }
            return false;
        }

        public boolean needSave() {
            boolean b = isChanged;
            isChanged = false;
            return b;
        }
    }
}
