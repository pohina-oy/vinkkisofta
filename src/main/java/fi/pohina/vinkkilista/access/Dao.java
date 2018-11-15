
package fi.pohina.vinkkilista.access;

import java.util.List;

public interface Dao<T, K> {
    
    T findOne(K key);
    
    List<T> findAll();
    
    void create(K key, T t);
}
