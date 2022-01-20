package com.github.coderodde.text.autocomplete;

import java.util.Scanner;

public class Demo {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].trim().equals("compare")) {
            compare();
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
    
    private static void compare() {
        System.out.println("<<< Benchmarking... >>>");
    }
}
