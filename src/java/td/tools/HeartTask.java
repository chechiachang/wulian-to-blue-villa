/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package td.tools;

/**
 *
 * @author OniYY
 */
import java.util.Timer;
import java.util.TimerTask;

import cc.wulian.ihome.wan.NetSDK;

public class HeartTask {

    private final int HEART_TIMER_DELAY = 1000;
    private final int HEART_TIMER_PERIOD = 30000;
    private Timer heartTimer;
    private HeartTimerTask heartTimerTask;

    public void startTimer() {
        if (heartTimer == null) {
            heartTimer = new Timer(true);
        }
        if (heartTimerTask == null) {
            heartTimerTask = new HeartTimerTask();
            heartTimer.schedule(heartTimerTask, HEART_TIMER_DELAY, HEART_TIMER_PERIOD);
        }
    }

    public void endTimer() {
        if (heartTimerTask != null) {
            heartTimerTask.cancel();
            heartTimerTask = null;
        }
        if (heartTimer != null) {
            heartTimer.cancel();
            heartTimer = null;
        }
    }

    private class HeartTimerTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("NetSDK.sendHeartMsg");// LOG
            NetSDK.sendHeartMsg("");
        }
    }
}
