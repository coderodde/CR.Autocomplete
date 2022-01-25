package com.github.coderodde.text.autocomplete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 ()
 * @since 1.6 ()
 */
public class AutocompleteSystem implements Iterable<String> {

    private final Set<String> stringSet = new HashSet<>();
    
    public boolean add(String s) {
        return stringSet.add(s);
    }

    public boolean contains(String s) {
        return stringSet.contains(s);
    }
    
    public boolean remove(String s) {
        return stringSet.remove(s);
    }
    
    public List<String> autocomplete(String prefix) {
        List<String> list = new ArrayList<>();
        
        for (String s : stringSet) {
            if (s.startsWith(prefix)) {
                list.add(s);
            }
        }
        
        return list;
    }
    
    public int size() {
        return stringSet.size();
    }

    @Override
    public Iterator<String> iterator() {
        return stringSet.iterator();
    }
}
