package win.i029.ll.languagelisten;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileSelectActivity extends AppCompatActivity {

    @BindView(R.id.id_fileselectlist)
    ListView mFileSelectListView;

    private int mType = 1;
    private FileSelectAdapter mAdapter;

    private File mRoot = Environment.getExternalStorageDirectory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        ButterKnife.bind(this);
        mType = getIntent().getIntExtra(PlayControl.KEY_SelectType,PlayControl.SelectType_Files);
        switch(mType) {
            case PlayControl.SelectType_Files :
                setTitle(R.string.addfiles);
                break;
            case PlayControl.SelectType_Folds:
                setTitle(R.string.addfold);
                break;
            default:
                break;

        }

        mAdapter = new FileSelectAdapter(this,mRoot);

        mFileSelectListView.setAdapter(mAdapter);

        mFileSelectListView.setEmptyView(findViewById(R.id.empty_playlistview));
        mFileSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FileSelectAdapter.FileItem fi = (FileSelectAdapter.FileItem)mAdapter.getItem(position);
                if(fi.isDirectory()) {
                    mAdapter.scanFolder(new File(fi.getFullPath()));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mAdapter.toUp() < 0) {
            FileSelectActivity.this.setResult(RESULT_CANCELED);
            FileSelectActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.fileselect_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fileselect_save:{

                ArrayList<String> fl = new ArrayList<>();
                for( FileSelectAdapter.FileItem fitem : mAdapter.getFileList()) {
                    if( fitem.isSelected() && (!fitem.isDirectory())) {
                        fl.add(fitem.getFullPath());
                    }
                }
                Intent n = new Intent();
                n.putStringArrayListExtra(PlayControl.KEY_FileSelected, fl);

                FileSelectActivity.this.setResult(RESULT_OK,n);
                FileSelectActivity.this.finish();
            }
                break;
            case R.id.fileselect_back:
                mAdapter.toUp();
                break;

            case R.id.fileselect_alltoggle:
                mAdapter.toggle();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
