package Chat;

import javax.jws.soap.SOAPBinding;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sixing.wen on 10/27/16.
 */
public class UserManager {
    private Map<String, User> users = Collections.synchronizedMap(new HashMap());
    private static UserManager inst = null;
    public static UserManager getInstance() {
        if (inst == null) {
            synchronized (UserManager.class) {
                if (inst == null) {
                    inst = new UserManager();
                }
            }
        }
        return inst;
    }
    public void addUser(User user) {
        users.put(user.getName(), user);
    }
    public User getUser(String name) {
        return users.get(name);
    }

    public boolean containUser(String name) {
        return users.containsKey(name);
    }
}
