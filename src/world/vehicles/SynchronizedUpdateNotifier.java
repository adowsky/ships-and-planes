package world.vehicles;

import gui.VehicleButton;
import world.WorldConstants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Managing and synchronizing updates of Moving Engines.
 */
public enum SynchronizedUpdateNotifier implements Runnable, Serializable{
    INSTANCE;
    private volatile List<Notifiable> notificationList = new LinkedList<>();
    private final double UPDATES_PER_MILLIS = WorldConstants.UPDATES_PER_SEC / 1000.;
    SynchronizedUpdateNotifier(){
        Thread t= new Thread(this);
        t.setDaemon(true);
        t.start();
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
    public List<Notifiable> toSerialize(){
        List<Notifiable> output = new LinkedList<>();
        notificationList.forEach(e ->{
            if(e instanceof VehicleButton){

            }else{
                output.add(e);
            }
        });
        return output;
    }
    public void addNewList(List<Notifiable> x){
        notificationList = x;
    }
}
