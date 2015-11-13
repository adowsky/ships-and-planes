package world.vehicles;

import world.WorldConstants;

public interface MovingEngine<T> extends Runnable{
    void hitTheRoad(T c);
    void setCanMove();
    @Override
    default void run(){
        long lastTime = System.currentTimeMillis();
        long currentTime = 0;
        double updatePerMillis = WorldConstants.UPDATES_PER_SEC/1000;
        while(true){
            if(currentTime-lastTime>updatePerMillis){
                setCanMove();
                lastTime = currentTime;
                currentTime = System.currentTimeMillis();
            }else{
                try{
                    Thread.sleep(1);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
