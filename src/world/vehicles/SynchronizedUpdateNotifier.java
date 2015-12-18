package world.vehicles;

import world.WorldConstants;

import java.util.LinkedList;
import java.util.List;

/**
 * Managing and synchronizing updates of Moving Engines.
 */
public class SynchronizedUpdateNotifier implements Runnable{
    private volatile List<Notifiable> notificationList;
    private final double UPDATES_PER_MILLIS;
    private static SynchronizedUpdateNotifier instance;
    private SynchronizedUpdateNotifier(){
        notificationList = new LinkedList<>();
        UPDATES_PER_MILLIS = WorldConstants.UPDATES_PER_SEC / 1000.0;
        Thread t= new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Return instance of class.
     * @return singleton instance of class
     */
    public static synchronized  SynchronizedUpdateNotifier getInstance(){
        if(instance == null)
            instance = new SynchronizedUpdateNotifier();

        return instance;
    }

    /**
     * Adds to list of objects that are notified when specific time elapsed.
     * @param e object that wants to bo notified.
     */
    public synchronized void addToList(Notifiable e){
        notificationList.add(e);
    }

    /**
     * Remove object from notification list.
     * @param e object to remove
     */
    public synchronized void removeFromList(Notifiable e){
        notificationList.remove(e);
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

    /**
     * Sleeps thread.
     */
    private void trySleep(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Notifies all objects on list about event.
     */
    private synchronized void notifyAboutUpdate(){
        notificationList.forEach((e)->e.tick());
    }


}
