package world;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for objects to serialization.
 */
public class SerializeContainer {
    private Set<Serializable> serializables;
    private static SerializeContainer instance;
    private SerializeContainer(){
        serializables = new HashSet<>();
    }
    public synchronized static SerializeContainer getInstance(){
        if(instance == null)
            instance = new SerializeContainer();
        return instance;
    }
    public synchronized void addObjectToSerialize(Serializable obj){
        serializables.add(obj);
    }
    public Set<Serializable> getSerializables(){
        return serializables;
    }
}
