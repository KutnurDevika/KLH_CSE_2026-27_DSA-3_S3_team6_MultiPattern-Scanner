package multipatternscanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printHeader();

        String text = chooseInput(scanner);

        if (text == null || text.isBlank()) {
            System.out.println("\nNo readable text was provided.");
            return;
        }

        System.out.println("\nText loaded successfully.");
        System.out.println("Characters extracted: " + text.length());

        int patternCount = readNonNegativeInt(
                scanner,
                "\nEnter number of patterns: "
        );

        if (patternCount == 0) {
            System.out.println("No patterns were provided.");
            return;
        }

        AhoCorasick automaton = new AhoCorasick();

        System.out.println("\nEnter the patterns:");

        for (int i = 1; i <= patternCount; i++) {
            String pattern;

            do {
                System.out.print("Pattern " + i + ": ");
                pattern = scanner.nextLine();
            } while (pattern.isBlank());

            automaton.insertPattern(pattern);
        }

        long buildStart = System.nanoTime();
        automaton.buildFailureLinks();
        long buildTime = System.nanoTime() - buildStart;

        long searchStart = System.nanoTime();
        List<MatchResult> matches = automaton.search(text);
        long searchTime = System.nanoTime() - searchStart;

        displayResults(automaton.getPatterns(), matches);

        System.out.println("\n========== PERFORMANCE ==========");
        System.out.printf("Patterns             : %d%n", patternCount);
        System.out.printf("Text length          : %d Java char(s)%n", text.length());
        System.out.printf("Total matches        : %d%n", matches.size());
        System.out.printf("Failure-link build   : %.3f ms%n",
                buildTime / 1_000_000.0);
        System.out.printf("Search time          : %.3f ms%n",
                searchTime / 1_000_000.0);
        System.out.println("=================================");
    }

    private static String chooseInput(Scanner scanner) {
        System.out.println("Choose input type:");
        System.out.println("1. Enter text manually");
        System.out.println("2. Load TXT file");
        System.out.println("3. Load PDF file");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    System.out.print("\nEnter text: ");
                    return scanner.nextLine();

                case "2":
                case "3":
                    System.out.print("\nEnter full file path: ");
                    String rawPath = scanner.nextLine().trim();
                    Path path = Path.of(rawPath);

                    if (choice.equals("2") &&
                        !path.toString().toLowerCase().endsWith(".txt")) {
                        System.out.println("Please provide a .txt file.");
                        return null;
                    }

                    if (choice.equals("3") &&
                        !path.toString().toLowerCase().endsWith(".pdf")) {
                        System.out.println("Please provide a .pdf file.");
                        return null;
                    }

                    return DocumentLoader.load(path);

                default:
                    System.out.println("Invalid choice.");
                    return null;
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("\nCould not read the file.");
            System.out.println("Reason: " + e.getMessage());
            return null;
        }
    }

    private static int readNonNegativeInt(
            Scanner scanner,
            String prompt) {

        while (true) {
            System.out.print(prompt);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Enter a valid non-negative integer.");
        }
    }

    private static void displayResults(
            List<String> patterns,
            List<MatchResult> matches) {

        System.out.println("\n========== SEARCH RESULTS ==========");

        Map<String, List<Integer>> positions =
                new LinkedHashMap<>();

        for (String pattern : patterns) {
            positions.putIfAbsent(pattern, new ArrayList<>());
        }

        for (MatchResult match : matches) {
            positions.get(match.pattern())
                     .add(match.startPosition());
        }

        boolean found = false;

        for (Map.Entry<String, List<Integer>> entry : positions.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                found = true;

                System.out.println("\nPattern: " + entry.getKey());
                System.out.println("Occurrences: " + entry.getValue().size());
                System.out.println("Positions (0-based): " + entry.getValue());
            }
        }

        if (!found) {
            System.out.println("No patterns were found.");
        }

        System.out.println("\n=====================================");
    }

    private static void printHeader() {
        System.out.println("==============================================");
        System.out.println("          MULTIPATTERN SCANNER");
        System.out.println("       Aho-Corasick Search Engine");
        System.out.println("==============================================");
    }
}
