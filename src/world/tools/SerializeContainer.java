package world.tools;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for objects to serialization.
 */
public enum SerializeContainer {
    INSTANCE;

    private Set<Serializable> serializables;
    SerializeContainer(){
        serializables = new HashSet<>();
    }

    /**
     * Register object as object to serialize.
     * @param obj object to serialize.
     */
    public synchronized void addToSerialization(Serializable obj){
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
