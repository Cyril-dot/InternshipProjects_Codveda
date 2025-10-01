package bankingApplication;

import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        System.out.println("--------------------");
        System.out.println("Welcome to BANK-IT");
        System.out.println("--------------------");

        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;
        int choice;

        // helper object to call bank operations (they look up accounts by account number)
        BankAccount bankOps = new BankAccount();

        while (isRunning) {
            System.out.println("-----------------------------------------------------");
            System.out.println("\uD83C\uDFE6 Banking Program Menu ");
            System.out.println("-----------------------------------------------------");

            System.out.println("Options: ");
            System.out.println("📋 1. Create Account");
            System.out.println("💰 2. SHOW BALANCE 💰");
            System.out.println("💵 3. DEPOSIT 💵");
            System.out.println("💸 4. WITHDRAW 💸");
            System.out.println("📋 5. Account Information");
            System.out.println("🚪 6. EXIT 🚪");

            System.out.println("----------------------------------------------------------");
            System.out.print("Select an option: ");

            String line = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid numeric choice.");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    AccountUser creator = new AccountUser();
                    creator.createAccount(); // this links a BankAccount internally
                }
                case 2 -> bankOps.accountBalance();
                case 3 -> bankOps.depositMoney();
                case 4 -> bankOps.withDraw();
                case 5 -> {
                    System.out.print("Enter account number to view info: ");
                    String acc = scanner.nextLine().trim();
                    AccountUser found = AccountUser.getUserByAccountNumber(acc);
                    if (found != null) {
                        found.userData();
                    } else {
                        System.out.println("❌ No user with that account number.");
                    }
                }
                case 6 -> {
                    System.out.println("Thanks for using Bank-It");
                    isRunning = false;
                }
                default -> System.out.println("Enter a valid choice: ");
            }
        }

        scanner.close();
    }
}
