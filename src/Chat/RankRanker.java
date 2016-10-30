package Chat;

/**
 * Created by sixing.wen on 10/30/16.
 */
public class RankRanker extends Thread {
    int interval = 0;
    RankRanker(int time) {
        interval = time;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(interval);
                RankManager.getInstance().sortScoreList();
            }
        } catch (InterruptedException e) {
        }
    }
}
