package Chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by sixing.wen on 10/27/16.
 */
public class TopicManager {
    Map<String, Topic> topics = Collections.synchronizedMap(new HashMap());
    private static TopicManager inst = null;
    public static TopicManager getInstance() {
        if (inst == null) {
            synchronized (TopicManager.class) {
                if (inst == null) {
                    inst = new TopicManager();
                }
            }
        }
        return inst;
    }
    public void createTopic(String topicName) {
        Topic tp = new Topic(topicName);
        topics.put(topicName, tp);
    }
    public Map<String, Topic> getTopics() {
        return topics;
    }
    public Topic getTopic(String topicName) {
        return topics.get(topicName);
    }
    public boolean contains(String topicName) {
        return topics.containsKey(topicName);
    }
}
