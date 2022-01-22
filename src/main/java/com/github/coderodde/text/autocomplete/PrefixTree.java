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
            node.representsString = false;
            size--;
            
            if (node.childMap != null) {
                return true;
            }
            
            int charIndex = s.length() - 1;
            node = node.parent;
            
            while (node != null) {
                if (node.representsString) {
                    node.childMap.remove(s.charAt(charIndex));
                    return true;
                }
                
                Node nextNode = node.parent;
                node.childMap.remove(s.charAt(charIndex--));
                
                if (node.childMap.isEmpty()) {
                    node.childMap = null;
                }
                
                node = nextNode;
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
        
        if (prefixNodeEnd.representsString) {
            autocompleteStrings.add(prefix);
        }
        
//        if (prefixNodeEnd == root && root.representsString) {
//            // Special case. The prefix is an empty string:
//            autocompleteStrings.add("");
//        }
        
        nodeQueue.add(prefixNodeEnd);
        substringQueue.add(new StringBuilder(prefix));
        
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
    
    @Override
    public Iterator<String> iterator() {
        return new PrefixTreeIterator();
    }
    
    private final class PrefixTreeIterator implements Iterator<String> {

        private int iterated;
        private final Deque<Character> characterStack = new ArrayDeque<>();
        
        private final Deque<Iterator<Map.Entry<Character, Node>>> 
                mapEntryIteratorDeque = new ArrayDeque<>();
        
        
        PrefixTreeIterator() {
            buildStack();
        }
        
        private void buildStack() {
            Node node = PrefixTree.this.root;
            
            while (node.childMap != null) {
                Map.Entry<Character, Node> nextMapEntry = 
                        node.childMap.entrySet().iterator().next();
                
                characterStack.addLast(nextMapEntry.getKey());
                mapEntryIteratorDeque.addLast(
                        nextMapEntry
                                .getValue()
                                .childMap
                                .entrySet()
                                .iterator());
                
                node = nextMapEntry.getValue();
            }
        }
        
        private void completeStack() {
            
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
            
            Iterator<Map.Entry<Character, Node>> topmostMapEntryIterator = 
                    mapEntryIteratorDeque.getLast();
            
            if (topmostMapEntryIterator.hasNext()) {
                Map.Entry<Character, Node> nextMapEntry = 
                        topmostMapEntryIterator.next();
                
                StringBuilder sb = new StringBuilder(characterStack.size());
                
                for (Character c : characterStack) {
                    sb.append(c);
                }
                
                characterStack.removeLast();
                characterStack.addLast(nextMapEntry.getKey());
                return sb.toString();
            }
            
            while (!mapEntryIteratorDeque.isEmpty() && 
                   !mapEntryIteratorDeque.getLast().hasNext()) {
                
                mapEntryIteratorDeque.removeLast();
                characterStack.removeLast();
            }
            
            if (mapEntryIteratorDeque.isEmpty()) {
                throw new NoSuchElementException("No more strings to iterate.");
            }
            
            buildStack();
            return next();
        }
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
}
