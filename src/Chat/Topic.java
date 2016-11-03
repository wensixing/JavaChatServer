package Chat;

import java.util.*;

/**
 * Created by sixing.wen on 10/27/16.
 */
public class Topic {
    private String name;
    private List<String> msgs = Collections.synchronizedList(new ArrayList<String>());
    private Map<String, User> users = Collections.synchronizedMap(new HashMap());
    Topic(String tp) {
        name = tp;
    }
    public String getName() {
        return name;
    }
    private void addMsg(String msg) {
        if (msgs.size() >= 10) {
            msgs.remove(0);
        }
        msgs.add(msg);
    }


    public void sendMsg(String msg, String sentBy) {
        for (Map.Entry<String, User> entry: users.entrySet()) {
            User user = entry.getValue();
            if (user.getName().equals(sentBy)) {
                continue;
            }
            if (user.getConnector() == null) {
                continue;
            }
            user.getConnector().respondSingleMsg("Topic [ " + name + " ]" + "[ "+ sentBy + " ] " + msg);
        }
        addMsg("[ "+ sentBy + " ] " + msg);
    }

    public List<String> getRecentMsgs() {
        return msgs;
    }
    public void addUser(User user) {
        users.put(user.getName(), user);
    }
    public void removeUser(User user) {
        users.remove(user.getName());
    }
}
