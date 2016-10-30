package Chat;

import com.ea.chat.score.exceptions.ServiceUnavailableException;

/**
 * Created by sixing.wen on 10/30/16.
 */
public class RankScorer extends Thread {
    User currentUser;
    String message;
    RankScorer(String msg, User user) {
        currentUser = user;
        message = msg;
    }
    @Override
    public void run() {
        int score = 0;
        try {
            score = RankManager.getInstance().getScorerService().getScorer().score(message);
        } catch (ServiceUnavailableException e) {
            System.out.println("[Log] score error");
            return;
        }
        RankManager.getInstance().onCalculate(currentUser, score);
    }
}
