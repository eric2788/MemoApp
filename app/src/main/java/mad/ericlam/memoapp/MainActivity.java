package mad.ericlam.memoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mad.ericlam.memoapp.manager.DataStorageManager;
import mad.ericlam.memoapp.manager.Memo;
import mad.ericlam.memoapp.manager.StaticStorage;

public class MainActivity extends AppCompatActivity implements MemoAdapter.ItemClickListener {

    private MemoAdapter adapter;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StaticStorage.ACTIVITY_MAIN = this;
        setSupportActionBar(findViewById(R.id.toolbar));
        StaticStorage.DATA_STORAGE = new DataStorageManager(getFilesDir());
        RecyclerView recyclerView = findViewById(R.id.recycler_memo);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<Memo> memos;
        try{
            memos = StaticStorage.DATA_STORAGE.listMemo();
        }catch (IOException e){
            Toast.makeText(this, "We got error on accessing memo directory, please make sure you have enough permission", Toast.LENGTH_LONG).show();
            memos = new ArrayList<>();
        }
        adapter = new MemoAdapter(this, memos);
        StaticStorage.MEMO_ADAPTER = adapter;
        adapter.setClickListener(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                boolean empty = adapter.getItemCount() == 0;
                StaticStorage.ACTIVITY_MAIN.findViewById(R.id.empty_text).setVisibility(empty ? View.VISIBLE : View.GONE);
            }
        });
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onItemClick(View view, int position) {
        Memo memo = adapter.getItem(position);
        Intent intent = new Intent(this, MemoViewActivity.class);
        intent.putExtra("title", memo.getTitle());
        intent.putExtra("content", new ArrayList<>(memo.getContent()));
        intent.putExtra("timestamp", memo.getTimestamp());
        startActivity(intent);
    }

    public void onAddMemo(View view) {
        Intent intent = new Intent(this, MemoCreateActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        this.menu = menu;
        return true;
    }

    public void onListMenuClick(MenuItem item) {
        if (menu == null){
            Toast.makeText(this, "Error: NO MENU INSTANCE", Toast.LENGTH_LONG).show();
            return;
        }
        switch (item.getTitle().toString().toLowerCase()){
            case "edit":
                if (adapter.getItemCount() == 0){
                    Toast.makeText(this, "the list is empty.", Toast.LENGTH_LONG).show();
                    break;
                }
                changeEditMode(true);
                break;
            case "about":
                Intent intent = new Intent(this, AboutMeActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void onEditDone(MenuItem item) {
        changeEditMode(false);
        Toast.makeText(this, "You quit the edit mode.", Toast.LENGTH_LONG).show();
    }


    private void changeEditMode(final boolean on){
        runOnUiThread(() -> {
            adapter.setDeleteMode(on);
            menu.findItem(R.id.menu_about).setVisible(!on);
            menu.findItem(R.id.menu_edit).setVisible(!on);
            menu.findItem(R.id.done_button).setVisible(on);
            findViewById(R.id.add_button).setVisibility(on ? View.INVISIBLE : View.VISIBLE);
        });
    }
}
