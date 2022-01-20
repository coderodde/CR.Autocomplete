package com.stackexchange.codereview.text.autocomplete;

import java.util.Collections;
import java.util.List;
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
}
