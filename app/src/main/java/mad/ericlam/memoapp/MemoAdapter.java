package mad.ericlam.memoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mad.ericlam.memoapp.manager.Memo;
import mad.ericlam.memoapp.manager.StaticStorage;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private List<Memo> memos;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private boolean deleteMode = false;

    MemoAdapter(Context context, List<Memo> data) {
        this.inflater = LayoutInflater.from(context);
        Collections.sort(data, (o1, o2) -> -Long.compare(o1.getTimestamp(), o2.getTimestamp()));
        this.memos = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.memoview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Memo memo = memos.get(position);
        holder.myTextView.setText(memo.getTitle());
        holder.setTimestamp(memo.getTimestamp());
        holder.deleteView.setVisibility(deleteMode ? View.VISIBLE : View.INVISIBLE);
    }

    void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
        this.notifyDataSetChanged();
    }

    boolean saveMemo(String title, String content){
        Memo memo = new Memo(title, Arrays.asList(content.split("\n")), System.currentTimeMillis());
        try {
            if (StaticStorage.DATA_STORAGE.saveMemo(memo)){
                memos.add(0, memo);
                notifyItemInserted(0);
                notifyDataSetChanged();
                return true;
            }
            Toast.makeText(StaticStorage.CREATE_PAGE, "Failed to save the memo, please try again later", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(StaticStorage.CREATE_PAGE, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    boolean saveMemo(String title, String content, long timestamp){
        Memo memo = new Memo(title, Arrays.asList(content.split("\n")), timestamp);
        int index = indexOf(timestamp);
        if (index == -1){
            Toast.makeText(StaticStorage.CREATE_PAGE,"Unknown index of this memo", Toast.LENGTH_LONG).show();
            return false;
        }
        try{
            if (StaticStorage.DATA_STORAGE.editMemo(memo)){
                memos.remove(index);
                memos.add(index, memo);
                notifyItemChanged(index);
                notifyDataSetChanged();
                return true;
            }
            Toast.makeText(StaticStorage.CREATE_PAGE, "Failed to save the memo, please try again later", Toast.LENGTH_LONG).show();
        }catch (IOException e){
            Toast.makeText(StaticStorage.CREATE_PAGE, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private int indexOf(long timestamp){
        for (int i = 0; i < memos.size(); i++) {
            if (memos.get(i).getTimestamp() == timestamp) return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton deleteView;
        private long timestamp;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.memo_title);
            deleteView = itemView.findViewById(R.id.delete_button);
            itemView.setOnClickListener(this);
            deleteView.setOnClickListener(v -> new AlertDialog.Builder(StaticStorage.ACTIVITY_MAIN)
                    .setTitle("Are you sure to delete?")
                    .setMessage("Once you deleted it could not cannot be restored.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        if (StaticStorage.DATA_STORAGE.deleteMemo(timestamp)){
                            memos.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyDataSetChanged();
                            Toast.makeText(StaticStorage.ACTIVITY_MAIN, "You have successfully deleted memo \"" + myTextView.getText() + "\"", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(StaticStorage.ACTIVITY_MAIN, "Failed to delete memo \"" + myTextView.getText() +"\"", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show());
        }

        @Override
        public void onClick(View view) {
            if (deleteMode) return;
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }

        void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    Memo getItem(int id) {
        return memos.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
