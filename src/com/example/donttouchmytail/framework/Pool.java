package com.example.donttouchmytail.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {

    public interface PoolObjectFactory<T> {
        public T createObject();
    }

    private final List<T> _freeObjects;
    private final PoolObjectFactory<T> _factory;
    private final int _maxSize;

    /**
     * The constructor of the Pool class takes a PoolObjectFactory and the maximum number of objects it should store. We
     * store both parameters in the respective members and instantiate a new ArrayList with the capacity set to the
     * maximum number of objects.
     * 
     * @param factory
     * @param maxSize
     */
    public Pool(PoolObjectFactory<T> factory, int maxSize) {
        this._factory = factory;
        this._maxSize = maxSize;
        this._freeObjects = new ArrayList<T>(maxSize);
    }

    /**
     * The newObject() method is responsible for either handing us a brand-new instance of the type held by the Pool,
     * via the PoolObjectFactory.newObject() method, or returning a pooled instance in case there’s one in the
     * freeObjectsArrayList. If we use this method, we get recycled objects as long as the Pool has some stored in the
     * freeObjects list. Otherwise, the method creates a new one via the factory.
     * 
     * @return object
     */
    public T newObject() {

        T object = null;

        if (_freeObjects.isEmpty()) {
            object = _factory.createObject();
        } else {
            object = _freeObjects.remove(_freeObjects.size() - 1);
        }

        return object;
    }

    /**
     * The free() method lets us reinsert objects that we no longer use. It simply inserts the object into the
     * freeObjects list if it is not yet filled to capacity. If the list is full, the object is not added, and it is
     * likely to be consumed by the garbage collector the next time it executes.
     * 
     * @param object
     */
    public void free(T object) {
        if (_freeObjects.size() < _maxSize) {
            _freeObjects.add(object);
        }
    }
}
