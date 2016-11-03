package Chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sixing.wen on 10/27/16.
 */
class Connector extends Thread {

    // Connection socket
    private final Socket socket;
    // Stream input
    private final BufferedReader reader;
    // Stream output
    private final PrintWriter sender;
    // The connected user
    private User user;

    private String state;
    private String targetTopicName;

    public Connector(Socket soc) throws IOException {
        state = "INIT";
        socket = soc;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sender = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        targetTopicName = "";
    }

    @Override
    public void run() {
        try {
            String line;
            List<String> msgs = new ArrayList<String>();
            msgs.add("Welcome to chat server!");
            msgs.add("Please input your name");
            respondMsgs(msgs);
            while (true) {
                line = reader.readLine();
                if (line.equalsIgnoreCase("/exit")) {
                    break;
                }
                if (state.equals("INIT")) {
                    registerUser(line);
                } else if (state.equals("CMD")) {
                    execute(line);
                } else if (state.equals("MSG")) {
                    handleMsg(line);
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } finally {
            try {
                sender.close();
                reader.close();
                socket.close();
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            } finally {
                if(user != null) {
                    user.setConnector(null);
                }
            }
        }
    }

    private void execute(String str) {
        List<String> cmds = spliteComand(str);
        if (cmds.get(0).equalsIgnoreCase("/send")) {
            if (cmds.get(1).length() == 0) {
                respondSingleMsg("[System] Wrong commands");
                return;
            }
            state = "MSG";
            targetTopicName = cmds.get(1);
            respondSingleMsg("[System] Please input your comment below : ");
        } else if (cmds.get(0).equalsIgnoreCase("/topics")) {
            getTopicList();
        } else if (cmds.get(0).equalsIgnoreCase("/sub")) {
            String topicName = cmds.get(1);
            subscribe(topicName);
        } else if (cmds.get(0).equalsIgnoreCase("/unsub")) {
            String topicName = cmds.get(1);
            unSubscribe(topicName);
        } else if (cmds.get(0).equalsIgnoreCase("/read")) {
            String topicName = cmds.get(1);
            readTopic(topicName);
        } else if (cmds.get(0).equalsIgnoreCase("/rank")) {
            getRank();
        } else if (cmds.get(0).equalsIgnoreCase("/help")) {
            help();
        } else {
            respondSingleMsg("[System] Wrong commands ! Try again");
        }
    }

    private void help() {
        List<String> msgs = new ArrayList<String>();
        msgs.add("--------- Manual ----------");
        msgs.add("/rank              -- Check top 10 user.");
        msgs.add("/send  [TOPIC]     -- Comment on one topic. If topic doesn't exist, system will create it for you.");
        msgs.add("/sub   [TOPIC]     -- Subscribe one topic. ");
        msgs.add("/unsub [TOPIC]     -- UnSubscribe one topic.");
        msgs.add("/read  [TOPIC]     -- Read latest 10 comments from one topic.");
        msgs.add("/help");
        msgs.add("/exit");
        msgs.add("---------------------------");
        respondMsgs(msgs);
    }

    private void getRank() {
        List<User> list = RankManager.getInstance().getUserScores();
        List<String> msgs = new ArrayList<String>();
        msgs.add("[System] The top 10 users (total : " + list.size() + ") are below : ");
        for (int i = 0; i < Math.min(list.size(), 10); i ++) {
            User cur = list.get(i);
            msgs.add("[ " + cur.getName() + " ]" + " (" + cur.getScore() + ")");
        }
        msgs.add("End of list");
        respondMsgs(msgs);
    }

    private void readTopic(String topicName) {
        if (!TopicManager.getInstance().contains(topicName)) {
            respondSingleMsg("[System] No this topic !");
            return;
        }
        Topic inst = TopicManager.getInstance().getTopic(topicName);
        List<String> cur = inst.getRecentMsgs();
        List<String> msgs = new ArrayList<String>();
        msgs.add("[System] \"" + inst.getName() + "\" Msg List :");
        for (String str : cur) {
            msgs.add("[System] " + str);
        }
        msgs.add("[System] End of list");
        respondMsgs(msgs);
    }

    private void getTopicList() {
        Map<String, Topic> topics = TopicManager.getInstance().getTopics();
        if (topics.size() == 0) {
            respondSingleMsg("[System] No topic here");
            return;
        }
        List<String> msgs = new ArrayList<String>();
        msgs.add("[System] Topic List :");
        for (Map.Entry<String, Topic> entry : topics.entrySet()) {
            msgs.add("[System] * " + entry.getKey());
        }
        msgs.add("[System] End of list");
        respondMsgs(msgs);
    }

    private void subscribe(String topicName) {
        if (!TopicManager.getInstance().contains(topicName)) {
            respondSingleMsg("[System] No this topic !");
            return;
        }
        if (user.containTopic(topicName)) {
            respondSingleMsg("[System] Already subscribed before !");
            return;
        }
        Topic inst = TopicManager.getInstance().getTopic(topicName);
        user.addTopic(inst);
        inst.addUser(user);
        respondSingleMsg("[System] Subscribed !");
    }

    private void unSubscribe(String topicName) {
        if (! TopicManager.getInstance().contains(topicName)) {
            respondSingleMsg("[System] No this topic !");
            return;
        }
        if (! user.containTopic(topicName)) {
            respondSingleMsg("[System] You didn't subscribe this topic !");
            return;
        }
        Topic inst = TopicManager.getInstance().getTopic(topicName);
        user.removeTopic(inst);
        inst.removeUser(user);
        respondSingleMsg("[System] Unsubscribed !");
    }

    private void handleMsg(String message) {
        if (message.length() == 0) {
            respondSingleMsg("[System] No message found !");
            return;
        }
        state = "CMD";
        if (targetTopicName.length() == 0) {
            respondSingleMsg("[System] No topic found ! please try it again");
            return;
        }
        if (!TopicManager.getInstance().contains(targetTopicName)) {
            TopicManager.getInstance().createTopic(targetTopicName);
            respondSingleMsg("[System] Topic \"" + targetTopicName + "\" created");
        }
        if (!user.getTopics().containsKey(targetTopicName)) {
            subscribe(targetTopicName);
        }
        Topic inst = TopicManager.getInstance().getTopic(targetTopicName);
        inst.sendMsg(message, user.getName());
        RankManager.getInstance().calculate(message, user);
        respondSingleMsg("[System] Msg sent");
    }

    private List<String> spliteComand(String str) {
        List<String> res = new ArrayList<String>();
        int index = str.indexOf(' ');
        if (index == -1) {
            res.add(str);
        } else {
            res.add(str.substring(0, index).trim());
            res.add(str.substring(index + 1).trim());
        }
        return res;
    }

    private void registerUser(String name) {
        if (UserManager.getInstance().containUser(name) && UserManager.getInstance().getUser(name).getConnector() != null) {
            respondSingleMsg("[System] Name has been taken, please try another one");
            return;
        }
        state = "CMD";
        if (UserManager.getInstance().containUser(name)) {
            user = UserManager.getInstance().getUser(name);
            user.setConnector(this);
            respondSingleMsg("[System] Welcome back");
        } else {
            user = new User(name, this);
            UserManager.getInstance().addUser(user);
            RankManager.getInstance().addUser(user);
            respondSingleMsg("[System] Thank you ! Register complete !");
            help();
        }
    }

    public synchronized void respondSingleMsg(String msg) {
        sender.println(msg);
        sender.flush();
    }

    public synchronized void respondMsgs(List<String> text) {
        for (String line : text) {
            sender.println(line);
        }
        sender.flush();
    }
}
