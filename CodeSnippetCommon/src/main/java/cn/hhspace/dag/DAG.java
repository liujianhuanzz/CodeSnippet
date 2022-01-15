package cn.hhspace.dag;

import cn.hhspace.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: DAG
 * @Date: 2021/11/23 3:05 下午
 * @Package: cn.hhspace.dag
 */
public class DAG<Node, NodeInfo, EdgeInfo> {

    private static final Logger logger = LoggerFactory.getLogger(DAG.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 节点Map，key为节点， value为节点信息
     */
    private volatile Map<Node, NodeInfo> nodesMap;

    /**
     * 边Map, key为源节点，value是一个Map, key为目标节点、value为边信息组成的Map
     */
    private volatile Map<Node, Map<Node, EdgeInfo>> edgesMap;

    /**
     * 倒排边Map， key为目标节点，value是一个Map， key为源节点，value为边信息组成的Map
     */
    private volatile Map<Node, Map<Node, EdgeInfo>> reverseEdgesMap;

    public DAG() {
        nodesMap = new HashMap<>();
        edgesMap = new HashMap<>();
        reverseEdgesMap = new HashMap<>();
    }

    /**
     * 添加节点
     * @param node
     * @param nodeInfo
     */
    public void addNode(Node node, NodeInfo nodeInfo) {
        lock.writeLock().lock();

        try {
            nodesMap.put(node, nodeInfo);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 添加边
     * @param fromNode
     * @param toNode
     * @return
     */
    public boolean addEdge(Node fromNode, Node toNode) {
        return addEdge(fromNode, toNode, false);
    }

    /**
     * 添加边
     * @param fromNode
     * @param toNode
     * @param createNode
     * @return
     */
    private boolean addEdge(Node fromNode, Node toNode, boolean createNode) {
        return addEdge(fromNode, toNode, null, createNode);
    }

    /**
     * 添加边
     * @param fromNode
     * @param toNode
     * @param edge
     * @param createNode 节点不存在时是否创建
     * @return
     */
    public boolean addEdge(Node fromNode, Node toNode, EdgeInfo edge, boolean createNode) {
        lock.writeLock().lock();

        try {
            if (!isLegalAddEdge(fromNode, toNode, createNode)) {
                logger.error("serious error: add edge({} -> {}) is invalid, cause cycle！", fromNode, toNode);
                return false;
            }

            addNodeIfAbsent(fromNode, null);
            addNodeIfAbsent(toNode, null);

            addEdge(fromNode, toNode, edge, edgesMap);
            addEdge(toNode, fromNode, edge, reverseEdgesMap);

            return true;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 判断节点是否存在
     * @param node
     * @return true/false
     */
    public boolean containsNode(Node node) {
        lock.readLock().lock();

        try {
            return nodesMap.containsKey(node);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 判断边是否存在
     * @param fromNode
     * @param toNode
     * @return true/false
     */
    public boolean containsEdge(Node fromNode, Node toNode) {
        lock.readLock().lock();

        try {
            Map<Node, EdgeInfo> endEdges = edgesMap.get(fromNode);

            if (endEdges == null) {
                return false;
            }

            return endEdges.containsKey(toNode);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 节点不存在，则添加节点
     * @param node
     * @param nodeInfo
     */
    private void addNodeIfAbsent(Node node, NodeInfo nodeInfo) {
        if (!containsNode(node)) {
            addNode(node, nodeInfo);
        }
    }

    /**
     * 添加边
     * @param fromNode 源节点
     * @param toNode 目标节点
     * @param edge 边信息
     * @param edges 边集合
     */
    private void addEdge(Node fromNode, Node toNode, EdgeInfo edge, Map<Node, Map<Node, EdgeInfo>> edges) {
        edges.putIfAbsent(fromNode, new HashMap<>());
        Map<Node, EdgeInfo> toNodeEdges = edges.get(fromNode);
        toNodeEdges.put(toNode, edge);
    }

    /**
     * 获取指定节点的所有邻接点
     * @param node
     * @param edges
     * @return
     */
    private Set<Node> getNeighborNodes(Node node, Map<Node, Map<Node, EdgeInfo>> edges) {
        Map<Node, EdgeInfo> neighborEdges = edges.get(node);

        if (neighborEdges == null) {
            return Collections.EMPTY_MAP.keySet();
        }

        return neighborEdges.keySet();
    }

    /**
     * 确认一条边是否可以被成功的添加，需要判断DAG是否有环
     * @param fromNode
     * @param toNode
     * @param createNode
     * @return
     */
    private boolean isLegalAddEdge(Node fromNode, Node toNode, boolean createNode) {
        if (fromNode.equals(toNode)) {
            logger.error("edge fromNode({}) can't equals toNode({})", fromNode, toNode);
            return false;
        }

        if (!createNode) {
            if (!containsNode(fromNode) || !containsNode(toNode)){
                logger.error("edge fromNode({}) or toNode({}) is not in vertices map", fromNode, toNode);
                return false;
            }
        }

        int verticesCount = getNodesCount();

        Queue<Node> queue = new LinkedList<Node>();

        queue.add(toNode);

        while (!queue.isEmpty() && (--verticesCount > 0)) {
            Node key = queue.poll();

            for (Node subsequentNode : getSubsequentNodes(key)) {
                if (subsequentNode.equals(fromNode)) {
                    return false;
                }

                queue.add(subsequentNode);
            }
        }

        return true;
    }

    /**
     * 获取指定节点的下游节点
     * @param node
     * @return
     */
    public Set<Node> getSubsequentNodes(Node node) {
        lock.readLock().lock();

        try {
            return getNeighborNodes(node, edgesMap);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取节点的数量
     * @return
     */
    public int getNodesCount() {
        lock.readLock().lock();

        try {
            return nodesMap.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取边的数量
     * @return
     */
    public int getEdgeCount() {
        lock.readLock().lock();

        try {
            int count = 0;

            for (Map.Entry<Node, Map<Node, EdgeInfo>> entry : edgesMap.entrySet()) {
                count += entry.getValue().size();
            }

            return count;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取节点详情
     * @param node
     * @return
     */
    public NodeInfo getNode(Node node) {
        lock.readLock().lock();

        try {
            return nodesMap.get(node);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取DAG的开始节点
     * @return
     */
    public Collection<Node> getBeginNodes() {
        lock.readLock().lock();

        try {
            return CollectionUtils.subtract(nodesMap.keySet(), reverseEdgesMap.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取DAG的结束节点
     * @return
     */
    public Collection<Node> getEndNodes() {
        lock.readLock().lock();

        try {
            return CollectionUtils.subtract(nodesMap.keySet(), edgesMap.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取节点的前序节点
     * @param node
     * @return
     */
    public Set<Node> getPreviousNodes(Node node) {
        lock.readLock().lock();

        try {
            return getNeighborNodes(node, reverseEdgesMap);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取节点的入度
     * @param node
     * @return
     */
    public int getIndegree(Node node) {
        lock.readLock().lock();

        try {
            return getPreviousNodes(node).size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
