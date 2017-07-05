package win.i029.ll.languagelisten;

import android.content.Context;
import android.media.MediaPlayer;


import java.io.IOException;

/**
 * Created by lvh on 6/30/17.
 */

public class VoicePlay {
    private final static String TAG = "VoicePlay";
    public static final int ERROR_OK = 0;
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_PLAYING = -2;




    private Context mContext;

    private MediaPlayer mMediaPlayer;
    private boolean mIsPaused = false;
    private OnEventListener mOnEventListener;


    private boolean mStoped = false;

    private String lastPath = "";
    public interface OnEventListener
    {
        void onCompletion();

    }


    public VoicePlay(Context context, OnEventListener listener) {

        mContext = context;
        mOnEventListener = listener;
        mStoped = false;
    }

    public boolean isChanged(String path) {
        if(path.equals(lastPath)) {
           return false;
        }
        return true;
    }
    public int load(String path) {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            int lastPos = 0;
            if(path.equals(lastPath)) {
                lastPos = mMediaPlayer.getCurrentPosition();
            }
            lastPath = path;

            mMediaPlayer.reset();

            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(false);
            mMediaPlayer.seekTo(lastPos);
            mStoped = false;
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    if(mOnEventListener != null) {
                        mOnEventListener.onCompletion();
                        mStoped = true;
                    }
                }
            });
            return ERROR_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ERROR_UNKNOWN;
    }

    public int getDuration() { // ms
        if( mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() { // ms
        if( mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    public int play() {
        mMediaPlayer.start();
        return ERROR_UNKNOWN;
    }

    public int seekTo(int ms) {
        mMediaPlayer.seekTo(ms);
        return ERROR_OK;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }
    public int pause() {
        try {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mIsPaused = true;
            }
        } catch (Exception e) {
            return ERROR_UNKNOWN;
        }

        return ERROR_OK;
    }

    public int resume() {
        if(mIsPaused ) {
            mMediaPlayer.start();
        }
        return ERROR_OK;
    }


    public int stop() {
        if( mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        return ERROR_OK;
    }

    public void release() {
        if( mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


}
