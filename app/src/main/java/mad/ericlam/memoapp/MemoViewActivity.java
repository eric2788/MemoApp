package mad.ericlam.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mad.ericlam.memoapp.manager.Memo;
import mad.ericlam.memoapp.manager.StaticStorage;

public class MemoViewActivity extends AppCompatActivity {

    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            Toast.makeText(StaticStorage.ACTIVITY_MAIN, "Error: no values there", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String title = bundle.getString("title");
        List<String> content = bundle.getStringArrayList("content");
        if (title == null || content == null){
            Toast.makeText(StaticStorage.ACTIVITY_MAIN, "Error: no values there", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        long timestamp = bundle.getLong("timestamp");
        this.memo = new Memo(title, content, timestamp);
        TextView titleView = findViewById(R.id.title_show);
        TextView contentView = findViewById(R.id.content_show);
        titleView.setText(title);
        StringBuilder builder = new StringBuilder();
        for (String line : content) {
            builder.append(line).append("\n");
        }
        contentView.setText(builder.toString());
    }


    public void onClickEdit(View view) {
        Intent intent = new Intent(this, MemoCreateActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("timestamp", memo.getTimestamp());
        intent.putExtra("title", memo.getTitle());
        intent.putExtra("content", new ArrayList<>(memo.getContent()));
        startActivity(intent);
    }
}
