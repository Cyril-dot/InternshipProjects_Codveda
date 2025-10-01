package FileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileProcessor {
    // Storage directory on Desktop (created at program start)
    private static final File STORAGE_DIR = createStorageDir();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("----------------------------------");
        System.out.println("Welcome to the File Reader System");
        System.out.println("----------------------------------");
        System.out.println();
        System.out.println("All created/appended result files will be stored in:");
        System.out.println(STORAGE_DIR.getAbsolutePath());
        System.out.println();

        // Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1) Read & analyze a file (count words/lines, search, most frequent)");
            System.out.println("2) Create a new file (write content)  <-- saved to Storage folder");
            System.out.println("3) Append to an existing file        <-- file in Storage folder");
            System.out.println("4) Exit");
            System.out.print("Enter choice (1-4): ");
            String menuChoice = scanner.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    analyzeFile(scanner);
                    break;
                case "2":
                    writeNewFile(scanner, false);
                    break;
                case "3":
                    writeNewFile(scanner, true); // append mode
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        System.out.println("Exiting. Goodbye!");
        scanner.close();
    }

    // Create (or return existing) Storage directory on Desktop
    private static File createStorageDir() {
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        File storage = new File(desktopPath, "storage");
        if (!storage.exists()) {
            boolean ok = storage.mkdirs();
            if (!ok) {
                System.err.println("Warning: could not create storage directory at " + storage.getAbsolutePath());
            }
        }
        return storage;
    }

    // --- Option 1: analyze file (reading, counting, searching, frequency) ---
    private static void analyzeFile(Scanner scanner) {
        System.out.print("Enter file path to analyze: ");
        String path = scanner.nextLine();
        File myFile = new File(path);

        if (!myFile.exists() || !myFile.isFile()) {
            System.out.println("File not found: " + path);
            return;
        }

        int lineCount = 0;
        int wordCount = 0;
        String searchWord = "";
        boolean doSearch = false;
        boolean found = false;
        int foundLine = -1;
        int foundPositionInLine = -1;

        String frequentWord = "";
        int maxCount = 0;
        Map<String, Integer> map = new HashMap<>();

        System.out.print("Do you wish to search for a word in the file (yes / no): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("yes")) {
            System.out.print("Enter word to search for: ");
            searchWord = scanner.nextLine().toLowerCase().trim().replaceAll("[^a-z0-9']", "");
            doSearch = true;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(myFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] words = trimmed.split("\\s+");
                    for (int i = 0; i < words.length; i++) {
                        String cleaned = words[i].replaceAll("[^a-zA-Z0-9']", "").toLowerCase();
                        if (cleaned.isEmpty()) continue;
                        wordCount++;
                        map.put(cleaned, map.getOrDefault(cleaned, 0) + 1);

                        if (doSearch && !found && cleaned.equals(searchWord)) {
                            found = true;
                            foundLine = lineCount;
                            foundPositionInLine = i + 1;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + path);
            return;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // find most frequent word
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                frequentWord = entry.getKey();
            }
        }

        // show results and optionally write to a results file (in Storage)
        System.out.println("\n=== Analysis Results ===");
        System.out.println("File: " + path);
        System.out.println("Number of lines: " + lineCount);
        System.out.println("Number of words: " + wordCount);
        if (doSearch) {
            if (found) {
                System.out.println("Searched word: '" + searchWord + "' found at line " + foundLine + ", position " + foundPositionInLine);
            } else {
                System.out.println("Searched word: '" + searchWord + "' not found.");
            }
        } else {
            System.out.println("No search requested.");
        }
        if (!frequentWord.isEmpty()) {
            System.out.println("Most frequent word: '" + frequentWord + "' appears " + maxCount + " times.");
        } else {
            System.out.println("No words found to compute frequency.");
        }

        System.out.print("\nWould you like to save these results to a file in the Storage folder? (yes / no): ");
        String save = scanner.nextLine();
        if (save.equalsIgnoreCase("yes")) {
            System.out.print("Enter output file name (without extension): ");
            String baseName = scanner.nextLine().trim();
            File outputFile = new File(STORAGE_DIR, baseName + ".txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write("File: " + path); writer.newLine();
                writer.write("Number of lines: " + lineCount); writer.newLine();
                writer.write("Number of words: " + wordCount); writer.newLine();
                if (doSearch) {
                    if (found) {
                        writer.write("Searched word: '" + searchWord + "' found at line " + foundLine + ", position " + foundPositionInLine); writer.newLine();
                    } else {
                        writer.write("Searched word: '" + searchWord + "' not found."); writer.newLine();
                    }
                } else {
                    writer.write("No search requested."); writer.newLine();
                }
                if (!frequentWord.isEmpty()) {
                    writer.write("Most frequent word: '" + frequentWord + "' appears " + maxCount + " times."); writer.newLine();
                } else {
                    writer.write("No words found to compute frequency."); writer.newLine();
                }
                System.out.println("Results written to " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error writing output file: " + e.getMessage());
            }
        }
    }

    // --- Option 2 & 3: create or append to a file (saved to Storage folder) ---
    private static void writeNewFile(Scanner scanner, boolean append) {
        String mode = append ? "append" : "create";
        System.out.print("Enter file name (without extension) to " + mode + " in Storage folder: ");
        String baseName = scanner.nextLine().trim();
        if (baseName.isEmpty()) {
            System.out.println("Invalid name.");
            return;
        }
        String finalName = baseName.toLowerCase().endsWith(".txt") ? baseName : baseName + ".txt";
        File file = new File(STORAGE_DIR, finalName);

        if (!append && file.exists()) {
            System.out.println("File already exists in Storage. Choose append (option 3) if you want to add to it, or pick a different name.");
            return;
        }
        if (append && !file.exists()) {
            System.out.println("File does not exist in Storage to append. Choose create (option 2) or use a different name.");
            return;
        }

        System.out.println("Enter the text to " + (append ? "append (type '::end' on a new line to finish)" : "write (type '::end' on a new line to finish)'):"));
        StringBuilder content = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("::end")) break;
            content.append(line).append(System.lineSeparator());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            bw.write(content.toString());
            System.out.println("Successfully " + (append ? "appended to " : "wrote ") + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
