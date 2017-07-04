package win.i029.ll.languagelisten;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.SeekBar;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayListEditActivity extends AppCompatActivity {
    private final String TAG = "PlayListEditActivity";

    private final static int ONRESULT_SELECT_FILES = 1;
    private final static int ONRESULT_SELECT_FOLDS = 2;

    private final static int UPDATE_PROGRESS = 1000;

    @BindView(R.id.id_playlistedit)
    ListView mPlayListEditView;

    @BindView(R.id.id_playlist_control)
    LinearLayout mVoiceControlPanel;

    @BindView(R.id.id_voice_progress)
    SeekBar mVoiceSeekBar;
    @BindView(R.id.id_voice_prev)
    ImageButton mBtnVoicePrev;
    @BindView(R.id.id_voice_play)
    ImageButton mBtnVoicePlay;
    @BindView(R.id.id_voice_next)
    ImageButton mBtnVoiceNext;
    @BindView((R.id.id_voice_info_name))
    TextView mVoiceTitle;

    @BindView((R.id.id_voice_info_time))
    TextView mVoiceTime;


    private PlayControl mPlayControl;
    private PlayListEditAdapter mAdapter;

    private int mOperateType = PlayControl.ItemOperate_Play;

    private VoicePlay mVoicePlay = null;

    PlayControl.PlaylistItem pli = null;

    private int mDuration = 0; // ms

    private int mPosition = 0;

    private float mRate = 1.0f;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;

    private boolean isSeeking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_edit);
        ButterKnife.bind(this);

        mOperateType = getIntent().getIntExtra(PlayControl.KEY_ItemOperationType, PlayControl.ItemOperate_Play);
        switch(mOperateType) {
            case PlayControl.ItemOperate_Play: {
                int index = getIntent().getIntExtra(PlayControl.KEY_ItemIndex, 0);
                pli = PlayControl.getInstance().getPlaylistItem(index);
                mVoicePlay = new VoicePlay(this, mPlayerEventListener);
                mPosition = pli.getActiveItemPos();
                load(mPosition);
                mBtnVoicePlay.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( mDuration != 0) {
                            if (mVoicePlay.isPlaying()) {
                                pause(false, -1);
                            } else {
                                play();

                            }
                        }
                    }
                });

                mBtnVoicePrev.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = load(mPosition - 1);
                        if(position >= 0) {
                            mPosition = position;
                            mAdapter.notifyDataSetChanged();
                            mPlayListEditView.smoothScrollToPosition(mPosition);
                            play();
                        }
                    }
                });

                mBtnVoiceNext.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = load(mPosition + 1);
                        if(position >= 0) {
                            mPosition = position;
                            mAdapter.notifyDataSetChanged();
                            mPlayListEditView.smoothScrollToPosition(mPosition);
                            play();
                        }
                    }
                });

                mVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        mVoiceTime.setText(String.format("%d / %d", seekBar.getProgress() / 1000, mDuration / 1000));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isSeeking = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        mVoicePlay.seekTo(seekBar.getProgress());
                        isSeeking = false;
                    }
                });
            }
                break;
            case PlayControl.ItemOperate_Edit: {
                int index = getIntent().getIntExtra(PlayControl.KEY_ItemIndex, 0);
                pli = PlayControl.getInstance().getPlaylistItem(index);
                mVoiceControlPanel.setVisibility(View.GONE);
            }
                break;
            case PlayControl.ItemOperate_New: {
                pli = new PlayControl.PlaylistItem();
                String title = getIntent().getStringExtra(PlayControl.KEY_ItemTitle);
                pli.setTitle(title);
                mVoiceControlPanel.setVisibility(View.GONE);
            }
                break;
            default:
                finish();
                break;
        }
        setTitle(pli.getTitle());
        mAdapter = new PlayListEditAdapter(this, pli , mOperateType);
        mPlayListEditView.setAdapter(mAdapter);
        mPlayListEditView.setEmptyView(findViewById(R.id.empty_playlistview));
        mPlayListEditView.setSelection(mPosition);
        mPlayListEditView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(mOperateType == PlayControl.ItemOperate_Play ) {

                    position = load(position);
                    if(position >= 0) {
                        mPosition = position;
                        mAdapter.notifyDataSetChanged();
                        play();
                    }

                }
            }
        });
        //android.R.attr.progressBarStyleSmallTitle
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_PROGRESS:
                        if(isSeeking==true) {
                            return;
                        }
                        if(mVoicePlay != null ) {
                            int cur = mVoicePlay.getCurrentPosition();
                            mVoiceSeekBar.setProgress(cur);
                            mVoiceTime.setText(String.format("%d / %d", cur / 1000, mDuration / 1000));
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch(mOperateType) {

            case PlayControl.ItemOperate_New:
            case PlayControl.ItemOperate_Edit:
                getMenuInflater().inflate(R.menu.playlistedit_menu, menu);
                break;
            case PlayControl.ItemOperate_Play:
                getMenuInflater().inflate(R.menu.playlistedit_play_menu, menu);
                break;
            default:

                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playlistmenu_add:
                {
                    Intent intent = new Intent();
                    intent.setClass(PlayListEditActivity.this, FileSelectActivity.class);
                    intent.putExtra(PlayControl.KEY_SelectType, PlayControl.SelectType_Files);
                    startActivityForResult(intent, ONRESULT_SELECT_FILES);
                }

            break;
            case R.id.playlistmenu_addfold:

            {
                Intent intent = new Intent();
                intent.setClass(PlayListEditActivity.this, FileSelectActivity.class);
                intent.putExtra(PlayControl.KEY_SelectType, PlayControl.SelectType_Folds);
                startActivityForResult(intent, ONRESULT_SELECT_FILES);
            }


                break;
            case R.id.playlistmenu_save:
                PlayControl.getInstance().addPlaylistItem(mAdapter.getPlaylistItem());
                setResult(RESULT_OK);
                finish();

                break;

            case R.id.playlistedit_play_back:
                finish();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ONRESULT_SELECT_FILES:
                    mAdapter.addItems(data.getStringArrayListExtra(PlayControl.KEY_FileSelected));
                    break;
                case ONRESULT_SELECT_FOLDS:
                    mAdapter.addItems(data.getStringArrayListExtra(PlayControl.KEY_FileSelected));

                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int load(int pos) { // if success , return pos , or return -1
        String path = pli.getItem(pos);
        if(path.isEmpty())
            return -1;

        mVoicePlay.load(path);
        mDuration = mVoicePlay.getDuration();
        mVoiceTitle.setText(pli.getShowText(pos));

        pli.setActiveItemPos(pos);
        return pos;
    }
    private void pause(boolean justUI, int currentPos) {

        mBtnVoicePlay.setImageResource(android.R.drawable.ic_media_play);
        if( currentPos >= 0 ) {
            mVoiceTime.setText(String.format("%d / %d", currentPos, mDuration / 1000));
            mVoiceSeekBar.setProgress(currentPos);
        }
        if( ! justUI ) {
            mVoicePlay.pause();
        }

    }
    private void play() {

        mVoiceSeekBar.setMax(mDuration);
        mVoicePlay.play(mRate);
        mBtnVoicePlay.setImageResource(android.R.drawable.ic_media_pause);

    }
    private VoicePlay.OnEventListener mPlayerEventListener = new VoicePlay.OnEventListener() {
        public void onCompletion() {
            Log.i(TAG, " Completed");
            pause(true,0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        switch(mOperateType) {
            case PlayControl.ItemOperate_Play:
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = Message.obtain(mHandler, UPDATE_PROGRESS);
                            mHandler.sendMessage(message);
                        }
                    };
                    mTimer.schedule(mTimerTask, 0, 200);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mVoicePlay != null) {
            mVoicePlay.release();
            mVoicePlay = null;
        }
    }

}
