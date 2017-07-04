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

    private float mRate = 1.0f;
    private float mVolume = 1.0f;

    private boolean mStoped = false;

    public interface OnEventListener
    {
        void onCompletion();

    }


    public VoicePlay(Context context, OnEventListener listener) {

        mContext = context;
        mOnEventListener = listener;
        mStoped = false;
    }

    public int load(String path) {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            mMediaPlayer.reset();

            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(false);
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


    public void setVolume(float volume) {
        mMediaPlayer.setVolume(volume, volume);
        mVolume = volume;

    }
    public int play(float rate) {
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
