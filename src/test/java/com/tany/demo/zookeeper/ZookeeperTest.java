package com.tany.demo.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperTest {

    @Test
    public void zookeeperTest(){

        ZookeeperService zooBase = new ZookeeperService();
        try {
//            zooBase.createRootNode("/hello", "hello world");
//            zooBase.createChilrenNode("/hello/test1", "hello adai b",false);
            zooBase.setNodeData("/hello/test1", "hello adai c");
//            System.out.println(new String(zooBase.getNodeData("/hello/adai/b",false)));
//
//            zooBase.createChilrenNode("/hello/adai/c", "hello adai c",false);
//            System.out.println(new String(zooBase.getNodeData("/hello/adai/c",false)));
//
//            zooBase.getAllNodeData("/hello/adai/c", false);
//
//            zooBase.deleteNode("/hello/adai/b");
//            List<String> list = new ArrayList<String>();
//            zooBase.getChildrenNode("/hello",false, list);
//            zooBase.setNodeData("/hello", "aac");
//            zooBase.deleteAllNode("/hello/adai");
//            zooBase.deleteCallBack("/hello");
            zooBase.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                zooBase.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void watchTest(){
        ZookeeperService zooBase = new ZookeeperService();
        try {
            System.out.println("创建跟节点");
            zooBase.createRootNode("/hello", "hello world");
            List<String> list = new ArrayList<String>();
            System.out.println("跟节点创建geichildnode监听");
            zooBase.getChildrenNode("/hello",true, list);
            zooBase.delayMillis(1*1000);
            System.out.println("跟节点创建isExist监听");
            zooBase.isExistNode("/hello/test1", true);
            zooBase.delayMillis(1*1000);
            System.out.println("创建子节点");
            zooBase.createChilrenNode("/hello/test1", "hello test1 b",false);
            System.out.println("创建子getnodedata监听");
            zooBase.getNodeData("/hello/test1",true);
            System.out.println("子节点保存更新数据");
            zooBase.setNodeData("/hello/test1", "hello test1 c");
            zooBase.delayMillis(1*1000);
//            zooBase.setNodeData("/hello/test1","hello test1 c");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zooBase.delayMillis(60*30*1000);
    }
}
