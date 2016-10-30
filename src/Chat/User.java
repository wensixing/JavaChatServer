package Chat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sixing.wen on 10/27/16.
 */
public class User {
    private String name;
    private Connector connector;
    private Map<String, Topic> topics;
    private AtomicInteger score;
    public User(String str, Connector con) {
        name = str;
        connector = con;
        topics = new HashMap();
        score = new AtomicInteger(0);
    }
    public void addScore(int diff) {
        score.addAndGet(diff);
    }
    public int getScore() {
        return score.get();
    }
    public Map<String, Topic> getTopics() {
        return topics;
    }
    public boolean containTopic(String topicName) {
        return topics.containsKey(topicName);
    }
    public void addTopic(Topic tp) {
        topics.put(tp.getName(), tp);
    }
    public void removeTopic(Topic tp) {
        topics.remove(tp.getName());
    }
    String getName() {
        return name;
    }
    Connector getConnector() {
        return connector;
    }
    void setConnector(Connector con) {
        connector = con;
    }
}
