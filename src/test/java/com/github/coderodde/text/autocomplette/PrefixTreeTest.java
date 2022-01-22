package com.github.coderodde.text.autocomplette;

import com.github.coderodde.text.autocomplete.PrefixTree;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PrefixTreeTest {
    
    @Test
    public void addAndContainsString() {
        PrefixTree pt = new PrefixTree();
        
        assertFalse(pt.contains("in"));
        assertFalse(pt.contains("inn"));
        assertFalse(pt.contains("ink"));
        
        assertTrue(pt.add("in"));
        assertTrue(pt.add("inn"));
        assertTrue(pt.add("ink"));
        
        assertFalse(pt.add("in"));
        assertFalse(pt.add("inn"));
        assertFalse(pt.add("ink"));
        
        assertTrue(pt.contains("in"));
        assertTrue(pt.contains("inn"));
        assertTrue(pt.contains("ink"));
        
        assertTrue(pt.add("ixxxxx"));
        assertTrue(pt.add("ix"));
        
        assertTrue(pt.contains("ixxxxx"));
        assertFalse(pt.contains("ixxxx"));
        assertFalse(pt.contains("ixxx"));
        assertFalse(pt.contains("ixx"));
        assertTrue(pt.contains("ix"));
    }
    
    @Test
    public void remove() {
        PrefixTree pt = new PrefixTree();
        
        assertFalse(pt.remove("aa"));
        pt.add("aa");
        assertTrue(pt.remove("aa"));
        
        pt.add("aaaaa");
        pt.add("aa");
        
        assertTrue(pt.contains("aaaaa"));
        assertTrue(pt.contains("aa"));
        
        assertFalse(pt.remove("a"));
        assertTrue(pt.remove("aa"));
        assertFalse(pt.remove("aaa"));
        assertFalse(pt.remove("aaaa"));
        assertTrue(pt.remove("aaaaa"));
        
        pt.add("aaa");
        assertFalse(pt.remove("aaaaa"));
        
        pt.clear();
        
        pt.add("a");
        pt.add("abbb");
        pt.add("accc");
        
        pt.remove("abb");
        assertFalse(pt.contains("abb"));
        
        pt.remove("abbb");
        assertFalse(pt.contains("abbb"));
    }
    
    @Test
    public void size() {
        PrefixTree pt = new PrefixTree();
        
        assertEquals(0, pt.size());
        
        pt.add("a");
        pt.add("b");
        pt.add("c");
        
        assertEquals(3, pt.size());
        
        pt.remove("b");
        
        assertEquals(2, pt.size());
        
        pt.remove("b");
        
        assertEquals(2, pt.size());
    }
    
    @Test
    public void testAutocomplete() {
        PrefixTree pt = new PrefixTree();
        
        pt.add("aaaa");
        pt.add("bbbb");
        pt.add("bbxs");
        pt.add("bbda");
        
        List<String> strings = pt.autocomplete("bb");
        Collections.<String>sort(strings);
        
        assertEquals(3, strings.size());
        
        assertEquals("bbbb", strings.get(0));
        assertEquals("bbda", strings.get(1));
        assertEquals("bbxs", strings.get(2));
        
        pt.clear();
        pt.add("aaa");
        strings = pt.autocomplete("aaab");
        assertTrue(strings.isEmpty());
    }
    
    @Test
    public void emptyString() {
        PrefixTree pt = new PrefixTree();
        
        pt.add("aa");
        pt.add("ab");
        
        assertFalse(pt.contains(""));
        assertTrue(pt.add(""));
        assertFalse(pt.add(""));
        assertTrue(pt.contains(""));
        
        pt.remove("");
        assertFalse(pt.contains(""));
        
        List<String> list = pt.autocomplete("");
        
        assertEquals(2, list.size());
        Collections.<String>sort(list);
        
        assertEquals("aa", list.get(0));
        assertEquals("ab", list.get(1));
        
        pt.add("");
        
        list = pt.autocomplete("");
        Collections.<String>sort(list);
        
        assertEquals("", list.get(0));
        assertEquals("aa", list.get(1));
        assertEquals("ab", list.get(2));
    }
    
    public void removeBug2() {
        PrefixTree pt = new PrefixTree();
        
        pt.add("");
        pt.add("000");
        pt.add("0");
        pt.add("00");
        
        pt.remove("00");
        List<String> l = pt.autocomplete("");
        assertEquals(3, l.size());
        
        Collections.sort(l);
        
        assertEquals("", l.get(0));
        assertEquals("0", l.get(1));
        assertEquals("000", l.get(2));
    }
    
    @Test
    public void removeBug1() {
        String[] strings = {"",
                            "10",
                            "1001",
                            "000",
                            "00",
                            "111"};
        
        PrefixTree pt = new PrefixTree();
        Set<String> set = new HashSet();
        
        for (String s : strings) {
            pt.add(s);
            set.add(s);
        }
        
        System.out.println(pt.autocomplete(""));
        
        String[] queryStrings = {"",     // out.
                                 "1",    // in.
                                 "000",  // out. 
                                 "1001", // out.
                                 "1110", // in.
                                 "10"};  // out.
        
        // Removes 00 also!
        for (String s : queryStrings) {
            pt.remove(s);
            set.remove(s);
        }
        
        System.out.println(pt.size());
        System.out.println(pt.autocomplete(""));
        System.out.println(set);
    }
}
