package world;

import java.io.Serializable;
import java.util.Collection;
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

    /**
     * Register object as object to serialize.
     * @param obj object to serialize.
     */
    public synchronized void addObjectToSerialize(Serializable obj){
        serializables.add(obj);
    }
    public synchronized void addObjectsToSerialize(Collection<Serializable> obj){
        serializables.addAll(obj);
    }
    /**
     * Return objects registered to serialize
     * @return  objects registered to serialize
     */
    public Set<Serializable> getSerializables(){
        return serializables;
    }

    public synchronized void clear(){
            serializables.clear();
    }
}
