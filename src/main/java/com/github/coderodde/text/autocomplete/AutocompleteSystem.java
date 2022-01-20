package com.github.coderodde.text.autocomplete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 ()
 * @since 1.6 ()
 */
public class AutocompleteSystem implements Iterable<String> {

    private final SortedSet<String> stringSet = new TreeSet<>();
    
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

    @Override
    public Iterator<String> iterator() {
        return stringSet.iterator();
    }
}
