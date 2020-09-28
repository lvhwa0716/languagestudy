package win.i029.ll.languagelisten;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FileSelectActivity extends AppCompatActivity {

    ListView mFileSelectListView;

    private int mType = 1;
    private FileSelectAdapter mAdapter;

    private File mRoot = Environment.getExternalStorageDirectory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        mFileSelectListView = findViewById(R.id.id_fileselectlist);
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
        //super.onBackPressed();
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
                Collections.sort(fl, new Comparator<String>() {

                    @Override
                    public int compare(String f1, String f2) {
                        if(f1.length() < f2.length())
                            return -1;
                        else if (f1.length() > f2.length())
                            return 1;
                        else
                            return f1.compareToIgnoreCase(f2);
                    }
                });
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
