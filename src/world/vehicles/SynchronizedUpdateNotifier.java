package world.vehicles;

import world.WorldConstants;

import java.util.LinkedList;
import java.util.List;

/**
 * Managing and synchronizing updates of Moving Engines.
 */
public class SynchronizedUpdateNotifier implements Runnable{
    private volatile List<MovingEngine> notificationList;
    private final double UPDATES_PER_MILLIS;
    private static SynchronizedUpdateNotifier instance;
    private SynchronizedUpdateNotifier(){
        notificationList = new LinkedList<>();
        UPDATES_PER_MILLIS = WorldConstants.UPDATES_PER_SEC / 1000.0;
        Thread t= new Thread(this);
        t.setDaemon(true);
        t.start();
    }
    public static synchronized  SynchronizedUpdateNotifier getInstance(){
        if(instance == null)
            instance = new SynchronizedUpdateNotifier();

        return instance;
    }
    public synchronized void addToList(MovingEngine e){
        notificationList.add(e);
    }

    @Override
    public void run() {
            long lastTime = System.currentTimeMillis();
            long currentTime;
            while (true) {
                currentTime = System.currentTimeMillis();
                if (currentTime - lastTime > UPDATES_PER_MILLIS) {
                    lastTime = currentTime;
                    notifyAboutUpdate();
                }
                trySleep();
            }
    }
    private void trySleep(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    private synchronized void notifyAboutUpdate(){
        notificationList.forEach((e)->e.setCanMove());
    }

}
