package com.github.coderodde.text.autocomplete;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Demo {
    
    private static final int NUMBER_OF_STRINGS_TO_GENERATE = 1000;
    private static final int MAXIMUM_STRING_LENGTH = 15;
    private static final String AUTOCOMPLETE_STRING = "1";

    public static void main(String[] args) {
        if (args.length > 0 && args[0].trim().equals("benchmark")) {
            benchmark();
        } else {
            runDemo();
        }
    }
    
    private static void runDemo() {
        Scanner scanner = new Scanner(System.in);
        Application application = new Application();
        
        while (true) {
            System.out.print(">>> ");
            String commandString = scanner.nextLine().trim().toLowerCase();
            
            if (commandString.equals("quit")) {
                break;
            }
            
            try {
                application.processCommand(commandString.split("\\s+"));
            } catch (Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }
        }
        
        System.out.println("Bye!");
    }
    
    private static void benchmark() {
        benchmarkImpl(false);
        benchmarkImpl(true);
    }
    
    private static void benchmarkImpl(boolean printStatistics) {
        if (printStatistics) {
            System.out.println("<<< Benchmarking... >>>");
        }
            
        Random random = new Random(1234L);
        
        String[] strings = getStrings(NUMBER_OF_STRINGS_TO_GENERATE, random);
        String[] queryString = getQueryString(strings, random);
        shuffle(queryString, random);
        
        PrefixTree prefixTree = new PrefixTree();
        AutocompleteSystem autocompleteSystem = new AutocompleteSystem();
        
        long prefixTreeDuration = 0L;
        long autocompletionSystemDuration = 0L;
        long start = System.currentTimeMillis();
        
        for (String s : strings) {
            prefixTree.add(s);
        }
        
        long end = System.currentTimeMillis();
        prefixTreeDuration += end - start;
        
        if (printStatistics) {
            System.out.println("PrefixTree.add() in " + (end - start) + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        for (String s : queryString) {
            prefixTree.contains(s);
        }
        
        end = System.currentTimeMillis();
        prefixTreeDuration += end - start;
        
        if (printStatistics) {
            System.out.println("PrefixTree.contains() in " 
                    + (end - start) 
                    + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        for (int i = 0; i < queryString.length / 2; i += 2) {
            prefixTree.remove(queryString[i]);
        }
        
        end = System.currentTimeMillis();
        prefixTreeDuration += end - start;
        
        if (printStatistics) {
            System.out.println("PrefixTree.remove() in " 
                    + (end - start) 
                    + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        List<String> prefixTreeCompletionStrings = 
                prefixTree.autocomplete(AUTOCOMPLETE_STRING);
        
        end = System.currentTimeMillis();
        prefixTreeDuration += end - start;
        
        Collections.sort(prefixTreeCompletionStrings);
        
        if (printStatistics) {
            System.out.println("PrefixTree.autocomplete() in " + (end - start) + 
                    " ms.");
            
            System.out.println("PrefixTree total duration: " + 
                    prefixTreeDuration + " ms.");
        }
        
        ////////////////////////////////////////////////////////////////////////
        System.out.println();
        start = System.currentTimeMillis();
        
        for (String s : strings) {
            autocompleteSystem.add(s);
        }
        
        end = System.currentTimeMillis();
        autocompletionSystemDuration += end - start;
        
        if (printStatistics) {
            System.out.println("AutocompleteSystem.add() in " + (end - start) 
                    + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        for (String s : queryString) {
            autocompleteSystem.contains(s);
        }
        
        end = System.currentTimeMillis();
        autocompletionSystemDuration += end - start;
        
        if (printStatistics) {
            System.out.println("AutocompleteSystem.contains() in " 
                    + (end - start) 
                    + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        for (int i = 0; i < queryString.length / 2; i += 2) {
            autocompleteSystem.remove(queryString[i]);
        }
        
        end = System.currentTimeMillis();
        autocompletionSystemDuration += end - start;
        
        if (printStatistics) {
            System.out.println("AutocompleteSystem.remove() in " 
                    + (end - start) 
                    + " ms.");
        }
        
        start = System.currentTimeMillis();
        
        List<String> autocompleteSystemCompletionStrings = 
                autocompleteSystem.autocomplete(AUTOCOMPLETE_STRING);
        
        end = System.currentTimeMillis();
        autocompletionSystemDuration += end - start;
        
        Collections.sort(autocompleteSystemCompletionStrings);
        
        if (printStatistics) {
            System.out.println("AutocompleteSystem.autocomplete() in " 
                    + (end - start) + " ms.");
            
            System.out.println("AutocompleteSystem total duration: " + 
                    autocompletionSystemDuration + " ms.");
            
            System.out.println();
            
            System.out.println("Data structures agree: " + 
                    prefixTreeCompletionStrings
                            .equals(autocompleteSystemCompletionStrings));
            
            System.out.println("PrefixTree returned " + 
                    prefixTreeCompletionStrings.size() + " strings.");
            
            System.out.println("AutocompleteSystem returned " +
                    autocompleteSystemCompletionStrings.size() + " strings.");
        }
    }
    
    private static String[] getQueryString(String[] strings, Random random) {
        String[] queryStrings = new String[strings.length];
        int index = 0;
        
        for (; index < strings.length / 2; ++index) {
            queryStrings[index] = strings[index];
        }
        
        for (; index < strings.length; ++index) {
            queryStrings[index] = generateString(random);
        }
        
        return queryStrings;
    }
    
    private static String[] 
        getStrings(int numberOfStringsRandom, Random random) {
            
        String[] strings = new String[numberOfStringsRandom];
        
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = generateString(random);
        }
        
        return strings;
    }
    
    private static String generateString(Random random) {
        int stringLength = random.nextInt(MAXIMUM_STRING_LENGTH + 1);
        StringBuilder sb = new StringBuilder(stringLength);
        
        for (int i = 0; i < stringLength; ++i) {
            sb.append(random.nextBoolean() ? '0' : '1');
        }
        
        return sb.toString();
    }
    
    private static void shuffle(String[] arr, Random random) {
        for (int i = arr.length - 1; i > 0; --i) {
            int j = random.nextInt(i);
            String s = arr[i];
            arr[i] = arr[j];
            arr[j] = s;
        }
    }
}
