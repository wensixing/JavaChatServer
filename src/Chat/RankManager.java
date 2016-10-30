package Chat;
import com.ea.chat.score.*;

import java.util.*;

/**
 * Created by sixing.wen on 10/30/16.
 */
public class RankManager {
    private static RankManager inst = null;
    private ScorerService scorer;
    /* real time
    Map<String, Integer> userScore = Collections.synchronizedMap(new HashMap());
    Map<Integer, Set<String>> scoreUser = Collections.synchronizedMap(new HashMap());
    */
    // asynchronous
    List<User> userScores = Collections.synchronizedList(new ArrayList<User>());
    public static RankManager getInstance() {
        if (inst == null) {
            synchronized (RankManager.class) {
                if (inst == null) {
                    inst = new RankManager();
                    inst.scorer = new ScorerService();
                    RankRanker ranker = new RankRanker(10 * 1000); // sort every 10s
                    ranker.start();
                }
            }
        }
        return inst;
    }
    public List<User> getUserScores() {
        return userScores;
    }

    public void addUser(User user) {
        userScores.add(user);
    }
    public ScorerService getScorerService() {
        return scorer;
    }
    public void calculate(String message, User user){
        RankScorer rs = new RankScorer(message, user);
        rs.start();
    }

    public void onCalculate(User user, int diff){
        user.addScore(diff);
    }
    public void sortScoreList() {
        System.out.println("call sortScoreList");
        Collections.sort(userScores, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.getScore() - o1.getScore();
            }
        });
    }
}
