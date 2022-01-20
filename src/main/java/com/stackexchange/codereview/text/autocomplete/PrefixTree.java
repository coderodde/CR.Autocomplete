package com.stackexchange.codereview.text.autocomplete;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class implements a prefix tree (https://en.wikipedia.org/wiki/Trie).
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 19, 2022)
 * @since 1.6 (Jan 19, 2022)
 */
public class PrefixTree {

    private static final class Node {
        Map<Character, Node> childMap;
        Node parent;
        boolean representsString;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        root.childMap = null;
        size = 0;
    }
    
    private final Node root = new Node();
    private int size;
    
    public boolean add(String s) {
        Objects.requireNonNull(s, "The input string is null.");
        
        if (root.childMap == null) {
            Node node = root;
            
            for (char ch : s.toCharArray()) {
                node.childMap = new HashMap<>();
                Node nextNode = new Node();
                node.childMap.put(ch, nextNode);
                nextNode.parent = node;
                node = nextNode;
            }
            
            node.representsString = true;
            size = 1;
            return true;
        }
        
        Node node = root;
        
        // Here, the tree is not empty!
        for (char ch : s.toCharArray()) {
            if (node.childMap == null) {
                node.childMap = new HashMap<>();
            }
            
            if (!node.childMap.containsKey(ch)) {
                Node nextNode = new Node();
                nextNode.parent = node;
                node.childMap.put(ch, nextNode);
                node = nextNode;
            } else {
                // Edge exists. Just traverse it.
                node = node.childMap.get(ch);
            }
        }
        
        if (node.representsString) {
            // The input string is already present in this prefix tree:
            return false;
        }
        
        node.representsString = true;
        size++;
        return true;
    }
    
    public boolean contains(String s) {
        Objects.requireNonNull(s, "The input string is null.");
        Node node = getPrefixNode(s);
        return node != null && node.representsString;
    }
    
    public boolean remove(String s) {
        Objects.requireNonNull(s, "The input string is null.");
        Node node = getPrefixNode(s);
        
        if (node == null) {
            return false;
        }
        
        if (node.representsString) {
            node.representsString = false;
            size--;
            
            int charIndex = s.length() - 1;
            
            if (node.childMap == null) {
                node = node.parent;
                
                while (node != root && node.childMap.size() == 1) {
                    Node nextNode = node.parent;
                    node.childMap = null;
                    node = nextNode;
                    charIndex--;
                }
                
                node.childMap.remove(s.charAt(charIndex));
            }
            
            return true;
        }
        
        return false;
    }
    
    public List<String> autocomplete(String s) {
        Objects.requireNonNull(s, "The input string is null.");
        
        Node prefixNodeEnd = getPrefixNode(s);
        
        if (prefixNodeEnd == null) {
            return Collections.<String>emptyList();
        }
        
        List<String> autocompleteStrings = new ArrayList<>();
        Queue<Node> nodeQueue = new ArrayDeque<>();
        Queue<StringBuilder> substringQueue = new ArrayDeque<>();
        
        nodeQueue.add(prefixNodeEnd);
        substringQueue.add(new StringBuilder(s));
        
        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.remove();
            StringBuilder currentStringBuilder = substringQueue.remove();
            
            if (currentNode.childMap == null) {
                continue;
            }
            
            for (Map.Entry<Character, Node> entry :
                    currentNode.childMap.entrySet()) {
                
                Node node = entry.getValue();
                StringBuilder stringBuilder =
                        new StringBuilder(currentStringBuilder)
                                .append(entry.getKey());
                
                nodeQueue.add(node);
                substringQueue.add(stringBuilder);
                
                if (node.representsString) {
                    autocompleteStrings.add(stringBuilder.toString());
                }
            }
        }
        
        return autocompleteStrings;
    }
    
    private Node getPrefixNode(String s) {
        Node node = root;
        
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (node == null || node.childMap == null) {
                return null;
            }
            
            node = node.childMap.get(s.charAt(i));
        }
        
        return node;
    }
    
    private static final class NodeHolder {
        Node node;
        StringBuilder text;
        
    }
}
