package com.company;

import java.io.*;
import java.util.*;

public class Main {
    /** Количество рёбер графа * 2 */
    private static final int SIZE = 71 * 2;
    /** Номер стартового узла */
    private static final int startI = 2;
    /** Номер конечного узла */
    private static final int endI = 4;

    public static void main(String[] args) {
        int[][] array = new int[SIZE][3];

        HashMap<Integer, Node> graph = createGraph(readFile(array));

        Node start = graph.get(startI);
        Node end = graph.get(endI);
        LinkedList<Node> linkedList = getShortestPath(graph,
               start,
                end);

        if (linkedList != null){
            getNode(linkedList);
        }else{
            System.out.println("Пути не существует");
        }
    }

    /** Вывод пути */
    public static void getNode(LinkedList<Node> path) {
        int sum = 0;
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).value);
            if (i != path.size()-1)
            sum += path.get(i).parents.get(path.get(i+1)).weight;
        }
        System.out.println("Длина всего пути " + sum);
    }

    /** Дейкстра */
    public static LinkedList<Node> getShortestPath(HashMap<Integer, Node> graph, Node start, Node end) {
        HashSet<Node> unprocessedNodes = new HashSet<>();
        HashMap<Node, Integer> timeToNodes = new HashMap<>();
        initHashTables(start, graph, unprocessedNodes, timeToNodes);
        calculateTimeToEachNode(unprocessedNodes, timeToNodes);
        if (timeToNodes.get(end) == null) return null;
        return getShortestPath(start, end, timeToNodes);
    }

    public static LinkedList<Node> getShortestPath(Node start, Node end, HashMap<Node, Integer> timeToNodes) {
        LinkedList<Node> path = new LinkedList<>();
        Node node = end;
        while (node != start) {
            int minTimeToNode = timeToNodes.get(node);
            path.addFirst(node);
            for (Map.Entry<Node, Edge> parentAndEdge : node.parents.entrySet()) {
                Node parent = parentAndEdge.getKey();
                Edge parentEdge = parentAndEdge.getValue();
                if (!timeToNodes.containsKey(parent)) continue;
                boolean prevNodeFound = (parentEdge.weight + timeToNodes.get(parent)) == minTimeToNode;
                if (prevNodeFound) {
                    timeToNodes.remove(node);
                    node = parent;
                    break;
                }
            }
        }
        path.addFirst(node);
        return path;
    }

    public static void calculateTimeToEachNode(HashSet<Node> unprocessedNodes,
                                               HashMap<Node, Integer> timeToNodes) {
        while (!unprocessedNodes.isEmpty()) {
            Node node = getNodeWithMinTimeTolt(unprocessedNodes, timeToNodes);
            if (node == null) return;
            if (timeToNodes.get(node) == Integer.MAX_VALUE) return;
            for (Edge edge : node.edges) {
                Node adjacentNode = edge.adjacentNode;
                if (unprocessedNodes.contains(adjacentNode)) {
                    int timeToCheck = timeToNodes.get(node) + edge.weight;
                    if (timeToCheck < timeToNodes.get(adjacentNode))
                        timeToNodes.put(adjacentNode, timeToCheck);
                }
            }
            unprocessedNodes.remove(node);
        }
    }

    public static Node getNodeWithMinTimeTolt(HashSet<Node> unprocessedNodes,
                                              HashMap<Node, Integer> timeToNodes) {
        Node nodeWithMinTimeTolt = null;

        int minTime = Integer.MAX_VALUE;
        for (Node node : unprocessedNodes) {
            int time = timeToNodes.get(node);
            if (time < minTime) {
                minTime = time;
                nodeWithMinTimeTolt = node;
            }
        }
        return nodeWithMinTimeTolt;
    }

    /** Стартовые данные */
    public static void initHashTables(Node start, HashMap<Integer, Node> graph, HashSet<Node> unprocessedNode,
                                      HashMap<Node, Integer> timeToNodes) {
        for (Map.Entry<Integer, Node> mapEntry : graph.entrySet()) {
            Node node = mapEntry.getValue();
            unprocessedNode.add(node);
            timeToNodes.put(node, Integer.MAX_VALUE);
        }
        timeToNodes.put(start, 0);
    }

    /** Получение или добавление */
    public static Node addOrGetNode(HashMap<Integer, Node> graph, int value) {
        if (value == -1) return null;
        if (graph.containsKey(value)) return graph.get(value);

        Node node = new Node(value);
        graph.put(value, node);
        return node;
    }

    /** Генерация графа */
    public static HashMap<Integer, Node> createGraph(int[][] graphData) {
        HashMap<Integer, Node> graph = new HashMap<>();
        for (int[] row : graphData) {
            Node node = addOrGetNode(graph, row[0]);
            Node adjacentNode = addOrGetNode(graph, row[1]);
            if (adjacentNode == null) continue;

            Edge edge = new Edge(adjacentNode, row[2]);
            node.edges.add(edge);
            adjacentNode.parents.put(node, edge);

        }

        return graph;
    }

    public static int[][] readFile(int[][] array) {
        try {
            File file = new File("C:\\Users\\Max\\navigation\\src\\com\\company\\GraphList");
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();

            int i = 0;
            while (line != null) {
                String[] str = line.replace("--","")
                        .replace("  " , " ")
                        .replace(".","")
                        .split(" ");

                if (Objects.equals(str[0], str[1])){
                    System.out.println("Неправильные входные данные");break;
                }

                array[i][0] = Integer.parseInt(str[0]);
                array[i+1][0] = Integer.parseInt(str[1]);

                array[i][1] = Integer.parseInt(str[1]);
                array[i+1][1] = Integer.parseInt(str[0]);

                array[i][2] = Integer.parseInt(str[2]);
                array[i+1][2] = Integer.parseInt(str[2]);
                i = i + 2;
                line = reader.readLine();
            }
            return array;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
