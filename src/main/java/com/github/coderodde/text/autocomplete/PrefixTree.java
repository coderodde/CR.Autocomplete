package com.github.coderodde.text.autocomplete;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;

/**
 * This class implements a prefix tree (https://en.wikipedia.org/wiki/Trie).
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 19, 2022)
 * @since 1.6 (Jan 19, 2022)
 */
public class PrefixTree implements Iterable<String> {

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
        Node node = root;
       
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
            size--;
            
            if (node.childMap != null) {
                node.representsString = false;
                return true;
            }
            
            int charIndex = s.length() - 1;
            node = node.parent;
            
            while (node != null) {
                node.childMap.remove(s.charAt(charIndex));
                
                if (node.childMap.isEmpty()) {
                    node.childMap = null;
                }
                
                if (node.representsString) {
                    return true;
                }
                
                charIndex--;
                node = node.parent;
                return true;
            }
            
            return true;
        }
        
        return false;
    }
    
    public List<String> autocomplete(String prefix) {
        Objects.requireNonNull(prefix, "The input string is null.");
        
        Node prefixNodeEnd = getPrefixNode(prefix);
        
        if (prefixNodeEnd == null) {
            return Collections.<String>emptyList();
        }
        
        List<String> autocompleteStrings = new ArrayList<>();
        Queue<Node> nodeQueue = new ArrayDeque<>();
        Queue<StringBuilder> substringQueue = new ArrayDeque<>();
        
        nodeQueue.add(prefixNodeEnd);
        substringQueue.add(new StringBuilder(prefix));
        
        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.remove();
            StringBuilder currentStringBuilder = substringQueue.remove();
            
            if (currentNode.representsString) {
                autocompleteStrings.add(currentStringBuilder.toString());
            }
            
            if (currentNode.childMap == null) {
                // No need to expand 'currentNode':
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
            }
        }
        
        return autocompleteStrings;
    }
    
    @Override
    public Iterator<String> iterator() {
        return new PrefixTreeIterator();
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
    
    private final class PrefixTreeIterator implements Iterator<String> {

        private int iterated;
        private final Deque<Node> nodeDeque = new ArrayDeque<>();
        private final Deque<Character> characterDeque = new ArrayDeque<>();
        private final Deque<Iterator<Map.Entry<Character, Node>>> 
                mapEntryIteratorDeque = new ArrayDeque<>();
        
        private PrefixTreeIterator() {
            if (!PrefixTree.this.isEmpty()) {
                initializeStacks();
            }
        }
        
        @Override
        public boolean hasNext() {
            return iterated < PrefixTree.this.size;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more strings to iterate.");
            }
            
            while (true) {
                if (!mapEntryIteratorDeque.getLast().hasNext()) {
                    completeStack();
                }

                Iterator<Map.Entry<Character, Node>> topmostIterator = 
                        mapEntryIteratorDeque.getLast();

                Map.Entry<Character, Node> mapEntry = topmostIterator.next();
                
                if (mapEntry.getValue().representsString) {
                    iterated++;
                    
                    StringBuilder stringBuilder = 
                            new StringBuilder(characterDeque.size());

                    for (Character character : characterDeque) {
                        stringBuilder.append(character);
                    }

                    return stringBuilder.toString();
                }
            }
        }
        
        private void initializeStacks() {
            Node node = root;
            
            while (node.childMap != null) {
                Iterator<Map.Entry<Character, Node>> iterator = 
                        node
                        .childMap
                        .entrySet()
                        .iterator();
                
                Map.Entry<Character, Node> mapEntry = iterator.next();
                mapEntryIteratorDeque.addLast(iterator);
                characterDeque.addLast(mapEntry.getKey());
                node = mapEntry.getValue();
            }
        }
        
        private void completeStack() {
            while (!mapEntryIteratorDeque.isEmpty() 
                    && !mapEntryIteratorDeque.getLast().hasNext()) {
                mapEntryIteratorDeque.removeLast();
                characterDeque.removeLast();
            }
            
            if (mapEntryIteratorDeque.isEmpty()) {
                return;
            }
            
            
            
            Map.Entry<Character, Node> mapEntry = 
                    mapEntryIteratorDeque.getLast().next();
            
            if (mapEntry.getValue() == null) {
                return;
            }
            
            mapEntryIteratorDeque.add(
                    mapEntry
                            .getValue()
                            .childMap
                            .entrySet()
                            .iterator());
            
            characterDeque.add(mapEntry.getKey());
            
            Node node = mapEntry.getValue();
            
            while (node.childMap != null) {
                Iterator<Map.Entry<Character, Node>> iterator = 
                        node.childMap.entrySet().iterator();
                
                mapEntry = iterator.next();
                mapEntryIteratorDeque.addLast(iterator);
                characterDeque.addLast(mapEntry.getKey());
                node = mapEntry.getValue();
            }
        }
    }
}
