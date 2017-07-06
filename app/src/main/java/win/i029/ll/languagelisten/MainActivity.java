package win.i029.ll.languagelisten;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Playlist";

    private final static int ONRESULT_EDIT_COMPLETED = 1;

    public static String mStorePath = "";
    public static String mCachePath = "";
    @BindView(R.id.id_playlist)
    ListView mPlayListView;

    private SharedPreferences mSharedPref;
    private MainActivityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mStorePath = getFilesDir().getPath();
        mCachePath = getCacheDir().getPath();

        mSharedPref = getSharedPreferences("playstatus", Activity.MODE_PRIVATE);
        mAdapter = new MainActivityAdapter(this);
        mPlayListView.setAdapter(mAdapter);
        mPlayListView.setEmptyView(findViewById(R.id.empty_playlistview));

        mPlayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, PlayListEditActivity.class);
            intent.putExtra(PlayControl.KEY_ItemIndex, position);
            intent.putExtra(PlayControl.KEY_ItemOperationType, PlayControl.ItemOperate_Play);
            PlayControl.getInstance().setActiveListPos(position);
            mAdapter.notifyDataSetChanged();
            startActivity(intent);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case ONRESULT_EDIT_COMPLETED:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PlayControl.getInstance().loadHistory(mSharedPref);
        mPlayListView.setSelection(PlayControl.getInstance().getActiveListPos());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayControl.getInstance().saveHistory(mSharedPref);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainmenu_add:

                final EditText edit= new EditText(MainActivity.this);
                new AlertDialog.Builder(this).setTitle(R.string.tips_entername)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setView(edit).setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String title = edit.getText().toString().trim();
                                if(! title.isEmpty()) {
                                    if( PlayControl.getInstance().getPlaylistItem(title) == null) {
                                        //Toast.makeText(MainActivity.this, "输入：" + title, Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent();
                                        intent.setClass(MainActivity.this, PlayListEditActivity.class);
                                        intent.putExtra(PlayControl.KEY_ItemTitle, title);
                                        intent.putExtra(PlayControl.KEY_ItemOperationType, PlayControl.ItemOperate_New);
                                        startActivityForResult(intent, ONRESULT_EDIT_COMPLETED);
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.tips_dupicatename + title, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.tips_nameisempty, Toast.LENGTH_LONG).show();
                                }


                            }

                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
