package mad.ericlam.memoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mad.ericlam.memoapp.manager.StaticStorage;

public class MemoCreateActivity extends AppCompatActivity {

    private boolean editMode = false;
    private long timestamp = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_create);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        editMode = getIntent().getBooleanExtra("editMode", false);
        timestamp = getIntent().getLongExtra("timestamp", -1);
        if (editMode && timestamp == -1){
            Toast.makeText(StaticStorage.ACTIVITY_MAIN, "ERROR: please contact the app developer", Toast.LENGTH_LONG).show();
            finish();
        }else if (editMode){
            String text = getIntent().getStringExtra("title");
            List<String> preContent = getIntent().getStringArrayListExtra("content");
            List<String> content = preContent == null ? new ArrayList<String>() : preContent;
            StringBuilder builder = new StringBuilder();
            for (String line : content) {
                builder.append(line).append("\n");
            }
            EditText title = findViewById(R.id.edit_title);
            title.setText(text);
            EditText para = findViewById(R.id.edit_para);
            para.setText(builder.toString());
        }
    }

    public void onSaveMemo(View view) {
        EditText text = findViewById(R.id.edit_title);
        String title = text.getText().toString();
        if (title.length() > 30){
            Toast.makeText(this, "Title length should not be more than 30 characters", Toast.LENGTH_LONG).show();
            return;
        }
        EditText para = findViewById(R.id.edit_para);
        String content = para.getText().toString();
        boolean result = editMode ? StaticStorage.MEMO_ADAPTER.saveMemo(title, content, timestamp) : StaticStorage.MEMO_ADAPTER.saveMemo(title, content);
        if (result){
            Toast.makeText(this, "Successfully save memo \""+title+"\"", Toast.LENGTH_LONG).show();
            if (editMode){
                Intent intent = new Intent(this, MemoViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title", title);
                intent.putExtra("content", new ArrayList<>(Arrays.asList(content.split("\n"))));
                intent.putExtra("timestamp", timestamp);
                startActivity(intent);
            }else{
                finish();
            }
        }else{
            Toast.makeText(this, "Error: cannot save memo \""+title+"\"", Toast.LENGTH_LONG).show();
        }
    }
}
