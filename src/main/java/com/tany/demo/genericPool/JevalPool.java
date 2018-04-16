package com.tany.demo.genericPool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.ConcurrentHashMap;

public class JevalPool implements PooledObjectFactory {
    //创建一个实例到对象池
    @Override
    public PooledObject makeObject() throws Exception {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        return new DefaultPooledObject(concurrentHashMap);
    }

    @Override
    public void destroyObject(PooledObject pooledObject) throws Exception {
        Object evaluator = pooledObject.getObject();
        if(evaluator != null){
            evaluator = null;
        }
    }

    @Override
    public boolean validateObject(PooledObject pooledObject) {
        return false;
    }

    @Override
    public void activateObject(PooledObject pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject pooledObject) throws Exception {

    }
}
