package io.nybbles.progcalc.adt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TrieMap<V> {
    public class Pair {
        public Pair(String key, V value) {
            this.key = key;
            this.value = value;
        }
        public V value;
        public String key;
    }

    public class Node {
        public V data;
        public boolean leaf;
        public HashMap<Character, Node> children = new HashMap<>();
    }

    private Node _root = new Node();

    public TrieMap() {
    }

    private void walk(
            Node node,
            StringBuilder key,
            ArrayList<Pair> pairs) {
        if (node.leaf) {
            pairs.add(new Pair(key.toString(), node.data));
        }
        for (var childKey : node.children.keySet()) {
            key.append(childKey);
            walk(node.children.get(childKey), key, pairs);
            key.setLength(key.length() - 1);
        }
    }

    public ArrayList<Pair> getPairs() {
        var pairs = new ArrayList<Pair>();
        var key = new StringBuilder();

        walk(_root, key, pairs);
        pairs.sort(Comparator.comparing(lhs -> lhs.key));

        return pairs;
    }

    public V search(String key) {
        Node currentNode = null;
        for (int i = 0; i < key.length(); i++) {
            currentNode = find(currentNode, key.charAt(i));
            if (currentNode == null)
                break;
        }
        return currentNode == null ? null : currentNode.data;
    }

    public Node find(Node node, char c) {
        var currentNode = node != null ? node : _root;
        return currentNode.children.getOrDefault(c, null);
    }

    public void insert(String key, V value) {
        var currentNode = _root;

        for (int i = 0; i < key.length(); i++) {
            var c = key.charAt(i);
            var node = currentNode.children.getOrDefault(c, null);
            if (node == null) {
                var newNode = new Node();
                currentNode.children.put(c, newNode);
                currentNode = newNode;
            } else {
                currentNode = node;
            }
        }

        currentNode.leaf = true;
        currentNode.data = value;
    }

}
