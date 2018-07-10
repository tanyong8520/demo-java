package com.tany.demo.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.tany.demo.exception.ExecuteEnum;
import com.tany.demo.exception.TanyException;
import org.springframework.stereotype.Component;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


@Component
public class ZookeeperService implements Watcher{
    //zookeeper地址
    private  String CONNECT_ADDR = "192.168.0.108:2181";
    //session超时时间
    private  int SESSION_OUTTIME = 2000 ; //ms
    //信号量，阻塞程序执行，用于等待zookeeper链接成功，发送成功信号
    private  CountDownLatch countDown = new CountDownLatch(1);

    /** 测试数据根路径  */
    private String root_path = "/watcher";
    /** 册数数据字节点路径 */
    private String children_path = "/watcher/children";

    private AtomicInteger count = new AtomicInteger();//默认从0开始
    private ZooKeeper zoo = null;

    public ZookeeperService(){
        try {
            zoo = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, this
//                    new Watcher() {
//                @Override
//                public void process(WatchedEvent event) {
//                    //获取事件状态
//                    KeeperState state = event.getState();
//                    EventType eventType = event.getType();
//                    if(KeeperState.SyncConnected == state){
//                        if(EventType.None == eventType ){ // 刚链接时，没有任何节点
//                            //如果连接成功，则发送信号量，让后续阻塞程序向下执行
//                            System.out.println("客户端连接服务器成功。。。。");
//                            countDown.countDown();
//                        }
//                    }
//                }
//            }
            );
            //阻塞程序，等待客户端连接成功
            countDown.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建根节点信息
     * @param path 根节点路径
     * @param data 节点数据
     * @param dataMode 数据存储的类型
     * @throws KeeperException
     * @throws InterruptedException
     */
    public  void createRootNode(String path , String data , CreateMode ...dataMode)
            throws KeeperException, InterruptedException{
        Stat stat = zoo.exists(path, false);
        if(stat != null) return;
        //创建父节点节点
        String msg = zoo.create(
                path, // 节点路径，不能递归创建
                data.getBytes(), // 只能是字节数组类型
                Ids.OPEN_ACL_UNSAFE, // 节点权限类型
                dataMode.length == 0 ? CreateMode.PERSISTENT : dataMode[0]// 节点类型，持久化和临时节点+有序节点
        );
        System.out.println(path+"节点创建成功。。。。");
        System.out.println(msg);
    }

    /**
     * 获取到子节点的全路径
     * @param paths
     * @param index
     * @return
     */
    public String getChildrenPath(String[] paths , int index ){
        StringBuffer sb = new StringBuffer();
        for(int i=1 ; i<=index;i++){
            sb.append("/").append(paths[i]);
        }
        return sb.toString();
    }

    /**
     * 递归创建子节点
     * @param path 子节点全路径
     * @param data 子节点内容信息
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createChilrenNode(String path , String data, Boolean isWatch)
            throws KeeperException, InterruptedException{
        String[] paths = path.split("/");
        String rootPath = paths[0]+"/"+paths[1]; //根节点路径
        Stat stat = zoo.exists(rootPath, isWatch);
        if(stat == null){ //如果根节点路径不存在，则创建
            createRootNode(rootPath,"");
        }
        for(int i=2;i<paths.length;i++){
            String chilrenPath = getChildrenPath(paths , i);
            createRootNode(chilrenPath,data);
        }
    }

    /**
     * 取得节点对应的数据，如果节点数据小于节点路径，之前的节点数据为空处理
     * @param paths 节点的全路径
     * @param nodesDate 每个节点对应的节点数据
     * @return 所有节点数据
     */
    public String[] minusNodeDate(String[] paths , String[] nodesDate){
        if(paths.length  == nodesDate.length )
            return nodesDate;
        if(paths.length < nodesDate.length )
            throw new TanyException(ExecuteEnum.ERROR_ZOOKEEPER_EXECUTE);
        String[] tempNode = new String[paths.length];
        int minus = paths.length - nodesDate.length;
        for(int i=0;i<minus;i++){
            tempNode[i] = "";
        }
        for(int i=0 ; i<nodesDate.length;i++){
            tempNode[minus++] = nodesDate[i];
        }
        return tempNode;
    }

    /**
     * 获取节点的数据
     * @param nodePath
     * @throws InterruptedException
     * @throws KeeperException
     * @return
     */
    public byte[] getNodeData(String nodePath, Boolean isWatch)
            throws KeeperException, InterruptedException{
        Stat sata = zoo.exists(nodePath, false);
        if(sata == null ){
            throw new TanyException(ExecuteEnum.ERROR_ZOOKEEPER_EXECUTE);
        }
        byte[] data = zoo.getData(nodePath, isWatch, null);
        return data;
    }

    /**
     * 得到所有节点数据
     * @param nodePath 节点路径
     * @return 所有节点的内容
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Object[] getAllNodeData (String nodePath, Boolean isWatch)
            throws KeeperException, InterruptedException{
        String[] nodePaths = nodePath.split("/");
        Object[] nodeDates = new Object[nodePaths.length-1];
        for(int i=1;i<nodePaths.length;i++){
            String nodeTempPath = getChildrenPath(nodePaths, i);
            nodeDates[i-1] = getNodeData(nodeTempPath,isWatch);
        }
        return nodeDates;
    }

    /**
     * 判断节点是否存在
     * @param nodePath  节点路径
     * @return true为存在该子节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean isExistNode(String nodePath, Boolean isWatch)
            throws KeeperException, InterruptedException{
        Stat stat = zoo.exists(nodePath, isWatch);
        if(stat == null){
            return false;
        }
        return true;
    }

    /**
     * 删除某一个子节点（只能删除子节点）
     * @param nodePath 子节点路径
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleteNode(String nodePath)
            throws KeeperException, InterruptedException{
        if(isExistNode(nodePath,false)){
            List<String> children = zoo.getChildren(nodePath, false);
            if(children != null && !children.isEmpty() ){
                System.out.println("该节点不是子节点，无法删除");
                return;
            }
            String data = new String(getNodeData(nodePath,false));
            zoo.delete(nodePath, -1); // -1删除所有版本
            System.out.println("删除成功:"+nodePath+":"+data);
        }else{
            System.out.println("没有该子节点:"+nodePath);
        }
    }

    /**
     * 删除根节点
     * @param nodePath 根节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleteAllNode(String nodePath)
            throws KeeperException, InterruptedException{
        List<String> nodes = new ArrayList<String>();
        getChildrenNode(nodePath,false,nodes);
        nodes.add(0,nodePath); // 将根节点放入容器中
        Collections.reverse(nodes); // 倒置
        for(String node : nodes){ //递归删除所有子节点
            deleteNode(node);
        }
    }

    /**
     * 得到根节点下的所有子节点全路径
     * @param nodePath 根节点路径
     * @param list 存放数据的集合（如果根节点/a 下有/b、/c这两个子节点，/b下有/b1、b2两个子节点，/c有 /c1子节点）
     *        </br>的值将会有 /a/c/c1,/a/c,/a/b/b2,/a/b/b1,/a/c,/a/b
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void getChildrenNode(String nodePath ,Boolean isWatch,List<String> list)
            throws KeeperException, InterruptedException{
        List<String> nodes = zoo.getChildren(nodePath, isWatch);
        for(int i = 0 ; i<nodes.size();i++){
            String node = nodes.get(i);
            list.add(nodePath+"/"+node);
            getChildrenNode(nodePath+"/"+node,isWatch,list);
        }
        return;
    }

    /**
     * 设置子节点的值
     * @param nodePath 子节点的路径
     * @param data 子节点需要修改的值
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void setNodeData(String nodePath , String data)
            throws KeeperException, InterruptedException{
        zoo.setData(nodePath, data.getBytes(), -1);
    }

    /**
     * 修改所有节点的数据内容
     * @param nodePath 节点全路径
     * @param data 节点对应的数据内容（数组的最后一个值对应，nodePath的最后一个节点）
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void setAllNodeData(String nodePath , String[] data )
            throws KeeperException, InterruptedException{
        String[] nodePaths = nodePath.split("/");
        StringBuffer sb = new StringBuffer();
        if(nodePaths.length == data.length ){
            for(int i=1;i<nodePath.length() ;i++){
                sb.append("/").append(nodePaths[i]);
                setNodeData(sb.toString(),data[i]);
            }
        }
        List<String> list = getNodePath(nodePath);
        Collections.reverse(list);
        for(int i=data.length-1;i>=0;i--){
            setNodeData(list.get(data.length -1 - i ),data[i]);
        }
    }

    /**
     * 得到所有节点的全路径路径
     * @param nodePath  节点路径
     * @return 如果nodePath值为：/a/b/c  将返回：/a /a/b /a/b/c
     */
    public List<String> getNodePath(String nodePath){
        List<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        String temp[] = nodePath.split("/");
        for(int i=1;i<temp.length;i++){
            sb.append("/").append(temp[i]);
            list.add(sb.toString());
        }
        return list;
    }

    /**
     *  异步删除子字节
     * @param nodePath  节点路径
     */
    public void deleteCallBack(String nodePath){
        zoo.delete(nodePath, -1, new VoidCallback() {

            @Override
            public void processResult(int rc, String path, Object ctx) {
                /**rc:为服务端响应码0表示调用成功，-4表示端口连接，-110表示执行节点存在，112表示会话已过期。
                 * path:接口调用时传入API的数据节点的路径参数
                 * ctx：为调用接口传入API的cxt值name：实际在服务器端创建节点的名称
                 **/
                System.out.println(rc);
                System.out.println(path);
                System.out.println(ctx);
            }
        }, "aa");
    }

    /**
     * 释放连接
     * @throws InterruptedException
     */
    public void close() throws InterruptedException{
        zoo.close();
    }

    /**
     * watcher监控的是客户端与服务器端状态和事件类型
     * 点节点发生改变时，收到来之zookeeper服务器端watch的通知。
     * (此时客户端相当于一个watcher(监控zookeeper的字节),
     * 实现Watcher类是为了监听服务器的节点数据是否发生变更(监控的动作为watch，客户端可视为watcher.))
     * 一个客户端可以有多个watcher.
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("开始执行process方法-----event:"+watchedEvent);
        delayMillis(1000);
        if(watchedEvent == null) return;
        //取得连接状态
        KeeperState state = watchedEvent.getState();
        //取得事件类型
        EventType eventType = watchedEvent.getType();
        //哪一个节点路径发生变更
        String nodePath = watchedEvent.getPath();
        String log_process = "Watcher-【  "+count.incrementAndGet()+" 】";
        System.out.println(log_process+"收到Watcher的通知");
        System.out.println(log_process+"连接状态："+state);
        System.out.println(log_process+"事件类型："+eventType);

        connectZookeeperState(state , eventType , log_process , nodePath);
    }

    /**
     * 判断客户端连接zookeeper服务的连接状态
     * @param state 服务器端返回的状态对象
     * @param eventType 事件类型对象
     * @param log_process 日记标识，标识是process方法里执行的日记
     * @param nodePath 发生变化的节点
     */
    public void connectZookeeperState(KeeperState state,
                                      EventType eventType , String log_process ,String nodePath){
        if(KeeperState.SyncConnected == state ){//连接成功
            nodeEventType(eventType, log_process , nodePath);
        }
        else if(KeeperState.Disconnected == state){
            System.out.println(log_process+"客户端连接zookeeper服务器端失败");
        }
        else if(KeeperState.Expired == state){
            System.out.println(log_process+"客户端与zookeeper服务器端会话失败");
        }
        else if(KeeperState.AuthFailed == state){
            System.out.println(log_process+"权限认证失败");
        }
        System.out.println("------------------------------------");
    }

    /**
     * 判断节点的事件类型
     * @param eventType 事件类型对象
     * @param log_process 日记标识，标识是process方法里执行的日记
     */
    public void nodeEventType(EventType eventType,String log_process,String nodePath ){
        // 没有任何节点，表示创建连接成功(客户端与服务器端创建连接成功后没有任何节点信息)
        if(EventType.None == eventType){
            System.out.println(log_process+"成功链接zookeeper服务器");
            countDown.countDown(); // 通知阻塞的线程可以继续执行
        }
        else if(EventType.NodeCreated == eventType){ //当服务器端创建节点的时候触发
            System.out.println(log_process+" zookeeper服务端创建新的节点");
            delayMillis(2000);
            //zookeeper服务端创建一个新的节点后并对其进行监控,创建完后接着对该节点进行监控,没有此代码将不会在监控该节点
            try {
                isExistNode(nodePath,true);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(EventType.NodeDataChanged == eventType){ //被监控该节点的数据发生变更的时候触发
            System.out.println(log_process+"节点的数据更新");
            delayMillis(2000);
            //跟新完后接着对该节点进行监控,没有此代码将不会在监控该节点
            try {
                byte[] updateNodeData = getNodeData(nodePath,true);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(EventType.NodeChildrenChanged == eventType){
            // 对应本代码而言只能监控根节点的一级节点变更。如：在根节点直接创建一级节点，
            //或者删除一级节点时触发。如修改一级节点的数据，不会触发，创建二级节点时也不会触发。
            System.out.println("子节点发生变更");
            delayMillis(2000);
            List<String> list = new ArrayList<>();
            try {
                this.getChildrenNode(root_path, true,list);
                System.out.println(log_process + "子节点列表：" + list);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(EventType.NodeDeleted == eventType){
            System.out.println(log_process+"节点："+nodePath+"被删除");
        }

        System.out.println("-------------------------------------");
    }

    /**
     * 休眠多少毫秒
     * @param millisecond
     */
    public void delayMillis(int millisecond){
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
