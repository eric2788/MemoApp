package mad.ericlam.memoapp.manager;

import java.util.List;

public class Memo {
    private String title;
    private List<String> content;
    private long timestamp;

    public Memo(String title, List<String> content, long timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
