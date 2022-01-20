package com.github.coderodde.text.autocomplete;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 ()
 * @since 1.6 ()
 */
public class Application {

    private static final class CommandNames {
        static final String ADD_STRING      = "add";
        static final String CONTAINS_STRING = "contains";
        static final String REMOVE_STRING   = "remove";
        static final String AUTOCOMPLETE    = "complete";
        static final String PRINT           = "print";
    }
    
    protected final PrefixTree prefixTree = new PrefixTree();
    
    public void addString(String s) {
        checkInputStringNotNull(s);
        prefixTree.add(s);
        System.out.println(getAllStrings());
    }
    
    public void removeString(String s) {
        checkInputStringNotNull(s);
        prefixTree.remove(s);
        System.out.println(getAllStrings());
    }
    
    public void containsString(String s) {
        checkInputStringNotNull(s);
        System.out.println(prefixTree.contains(s));
    }
    
    public void autocompletePrefix(String prefix) {
        checkPrefixNotNull(prefix);
        List<String> list = prefixTree.autocomplete(prefix);
        Collections.<String>sort(list);
        System.out.println(list);
    }
    
    public void printAll() {
        autocompletePrefix("");
    }
    
    public void processCommand(String[] tokens) {
        switch (tokens.length) {
            case 1:
                processSingleTokenCommand(tokens);
                return;
                
            case 2:
                processDoubleTokenCommand(tokens);
                return;
                
            default:
                String cmd = String.join(" ", tokens);
                throw new IllegalArgumentException(
                    "Bad command: \"" + cmd + "\"");
        }
    }
    
    private List<String> getAllStrings() {
        List<String> list = prefixTree.autocomplete("");
        Collections.<String>sort(list);
        return list;
    }
    
    private void processSingleTokenCommand(String[] tokens) {
        assert tokens.length == 1;
        
        switch (tokens[0]) {
            case CommandNames.PRINT:
                printAll();
                return;
                
            default:
                throw new IllegalArgumentException(
                    "Unknown command: " + String.join(" ", tokens));
        }
    }
    
    private void processDoubleTokenCommand(String[] tokens) {
        assert tokens.length == 2;
        
        switch (tokens[0]) {
            case CommandNames.ADD_STRING:
                addString(tokens[1]);
                return;
                
            case CommandNames.AUTOCOMPLETE:
                autocompletePrefix(tokens[1]);
                return;
                
            case CommandNames.CONTAINS_STRING:
                containsString(tokens[1]);
                return;
                
            case CommandNames.REMOVE_STRING:
                removeString(tokens[1]);
                return;
                
            default:
                throw new IllegalArgumentException(
                    "Unknown command: " + String.join(" ", tokens));
        }
    }
    
    private void checkInputStringNotNull(String s) {
        Objects.requireNonNull(s, "The input string is null.");
    }
    
    private void checkPrefixNotNull(String s) {
        Objects.requireNonNull(s, "The prefix is null.");
    }
}
