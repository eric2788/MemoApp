package mad.ericlam.memoapp.manager;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataStorageManager {

    private File dir;

    public DataStorageManager(File mobileDir) {
        dir = new File(mobileDir, "Memos");
        dir.mkdirs();
    }

    public boolean saveMemo(Memo memo) throws IOException {
        File memoFile = new File(dir, memo.getTimestamp()+"");
        if (memoFile.exists()) return false;
        if (memoFile.createNewFile()) {
            try (PrintWriter writer = new PrintWriter(new FileOutputStream(memoFile))) {
                writer.println(memo.getTitle());
                for (String line : memo.getContent()) {
                    writer.println(line);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean editMemo(Memo memo) throws IOException {
        File memoFile = new File(dir, memo.getTimestamp()+"");
        if (!memoFile.exists()) return false;
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(memoFile))){
            writer.println(memo.getTitle());
            for (String line : memo.getContent()) {
                writer.println(line);
            }
        }
        return true;
    }

    public boolean deleteMemo(long timestamp) {
        File memoFile = new File(dir, timestamp+"");
        if (!memoFile.exists()) return false;
        return memoFile.delete();
    }

    public List<Memo> listMemo() throws IOException{
        List<Memo> memos = new ArrayList<>();
        if (dir == null || dir.listFiles() == null) return memos;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            List<String> content = new ArrayList<>();
            String title = null;
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                while (reader.ready()){
                    if (title == null) {
                        title = reader.readLine();
                    }else{
                        content.add(reader.readLine());
                    }
                }
            }
           try{
               long timestamp = Long.parseLong(file.getName());
               memos.add(new Memo(title, content, timestamp));
           }catch (NumberFormatException e){
               Toast.makeText(StaticStorage.ACTIVITY_MAIN, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
           }
        }
        return memos;
    }
}
