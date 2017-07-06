package win.i029.ll.languagelisten;


import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by lvh on 6/30/17.
 */

public class FileSelectAdapter  extends BaseAdapter {

    private LayoutInflater mInflater;
    private final File mRootDir;
    private String mCurrentDir = "";

    private ScanFolderTask mScanTask = null;

    private final Context mContext;
    private ArrayList<FileItem> mFilelist = new ArrayList<FileItem>();

    public FileSelectAdapter(Context context , File root) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mRootDir = root;
        mCurrentDir = mRootDir.getAbsolutePath();
        scanFolder(mRootDir);
    }


    @Override
    public int getCount() {
        return mFilelist.size();
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        FileSelectAdapter.ViewHolder holder;

        FileItem fi = mFilelist.get(position);
        if (contentView == null) {
            contentView = mInflater.inflate(R.layout.filelist_item, null);
            holder = new FileSelectAdapter.ViewHolder();

            holder.name = (TextView) contentView.findViewById(R.id.filelist_name);
            holder.select = (CheckBox) contentView.findViewById(R.id.filelist_select);

            contentView.setTag(holder);
        } else {
            holder = (FileSelectAdapter.ViewHolder) contentView.getTag();
        }

        holder.name.setText(fi.getTitle());

        holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if(arg1) {
                    mFilelist.get(position).select();
                } else {
                    mFilelist.get(position).unSelect();
                }
            }
        });


        holder.select.setFocusable(false); // enable ListView OnItemClickListener
        if(fi.isDirectory()) {
            holder.select.setVisibility(View.GONE);
        } else {
            holder.select.setVisibility(View.VISIBLE);
            if(fi.isSelected()) {
                holder.select.setChecked(true);
            } else {
                holder.select.setChecked(false);
            }
        }
        return contentView;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mFilelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public ArrayList<FileItem> getFileList() {
        return mFilelist;
    }

    public int toUp() { // -1 : already in top
        int index = mCurrentDir.lastIndexOf('/');
        if( index > 0) {
            String newDir = mCurrentDir.substring(0, index);
            if (newDir.length() >= mRootDir.getAbsolutePath().length()) {
                scanFolder(new File(newDir));
                return 1;
            } else {
                return -1;
            }
        }

        return 0;
    }

    public void toggle() {
        int selectCount = 0;
        for( FileSelectAdapter.FileItem fitem : mFilelist) {
            if( fitem.isSelected()) {
                selectCount++;
            }
        }
        if(selectCount == mFilelist.size()) {
            for( FileSelectAdapter.FileItem fitem : mFilelist) {
                fitem.unSelect();
            }
        } else {
            for( FileSelectAdapter.FileItem fitem : mFilelist) {
                fitem.select();
            }
        }
        notifyDataSetChanged();
    }

    public final class ViewHolder {
        public TextView name;
        public CheckBox select;
    }


    public final static class FileItem {
        private boolean isDirectory;
        private String mFullPath;
        private String mFileName; // no path , no suffix
        private boolean isSelected;
        public FileItem(File file) {
            mFullPath = file.getAbsolutePath();
            isDirectory = file.isDirectory();
            mFileName = file.getName();
            int offset = mFileName.lastIndexOf(".");
            if(offset > 0) {
                mFileName = mFileName.substring(0,offset);
            }
            isSelected = false;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public String getFullPath() {
            return mFullPath;
        }

        public String getTitle() {
            return mFileName;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void select() {
            isSelected = true;
        }

        public void unSelect() {
            isSelected = false;
        }

    }

    public int scanFolder(File file) {

        if(!file.isDirectory() ) {
            Toast.makeText(mContext, "不是一个目录", Toast.LENGTH_LONG).show();
            return -1;
        }

        if( !file.canRead()) {
            Log.e("ScanFolder" , "can't read " + file.getAbsolutePath());
            Toast.makeText(mContext, "目录不可读", Toast.LENGTH_LONG).show();
            return -2;
        }

        if(mScanTask == null) {
            mScanTask = new ScanFolderTask();



            mScanTask.execute(file);
            return 0;
        } else {
            // in processing
            Log.e("ScanFolder" , "a task in progressing");
            Toast.makeText(mContext, "正在执行中...", Toast.LENGTH_LONG).show();
            return -3;
        }
    }

    private class ScanFolderTask extends AsyncTask<File, Integer, ArrayList<FileItem>> {
        private String mDir = "";
        private ProgressDialog mProgressDialog = null;
        public ScanFolderTask() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(mContext.getString(R.string.converting));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }
        @Override
        protected void onPreExecute() {

            mProgressDialog.show();
        }

        @Override
        protected ArrayList<FileItem> doInBackground(File... params) {

            ArrayList<FileItem> filelist = new ArrayList<FileItem>();
            File file = params[0];
            mDir = file.getPath();

            File[] files = file.listFiles(new Mp3Filter());
            if(files != null) {
                for ( File f : files) {
                    if (!f.isHidden() && f.canRead()) {
                        filelist.add(new FileItem(f));
                    }
                }
            }

            Collections.sort(filelist, new FileItemComparator());
            return filelist;
        }

        @Override
        protected void onPostExecute(ArrayList<FileItem> result) {

            if(result.isEmpty()) {
                Toast.makeText(mContext, "目录为空，保持上次结果", Toast.LENGTH_SHORT).show();
            } else {

                FileSelectAdapter.this.mFilelist = result;
                mCurrentDir = mDir;
                notifyDataSetChanged();
            }
            mScanTask = null;
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        private final class Mp3Filter  implements FilenameFilter{

            public boolean accept(File dir,String fname){
                if(fname.toLowerCase().endsWith(".mp3"))
                    return true;
                File n = new File(dir.getAbsolutePath() + "/" + fname);
                if(n.isDirectory())
                    return true;

                return false;

            }

        }
    }

    private static class FileItemComparator implements Comparator<FileItem> {

        @Override
        public int compare(FileItem first, FileItem second) {
            if (first.isDirectory() != second.isDirectory()) {
                if (first.isDirectory()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if(first.getTitle().length() < second.getTitle().length()) {
                    return -1;
                } else if (first.getTitle().length() == second.getTitle().length()) {
                    return first.getTitle().toLowerCase()
                            .compareTo(second.getTitle().toLowerCase());
                } else {
                    return 1;
                }
            }
        }
    }
}
