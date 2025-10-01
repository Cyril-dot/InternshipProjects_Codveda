package bankingApplication;

import java.util.Scanner;

public class BankAccount {
    // handle deposits, withdrawals, and checking balance
    private double deposits;
    private double withdrawals;
    private double balance = 0.0;

    // accept the owner reference (do NOT new an AccountUser here)
    private AccountUser user;

    public BankAccount() {
    }

    public BankAccount(AccountUser user) {
        this.user = user;
    }

    public AccountUser getUser() {
        return user;
    }

    public void setUser(AccountUser user) {
        this.user = user;
    }

    public double getDeposits() {
        return deposits;
    }

    public void setDeposits(double deposits) {
        this.deposits = deposits;
    }

    public double getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(double withdrawals) {
        this.withdrawals = withdrawals;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public BankAccount(double deposits, double withdrawals, double balance) {
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "deposits=" + deposits +
                ", withdrawals=" + withdrawals +
                ", balance=" + balance +
                '}';
    }

    // deposit into a found user's bank account
    public void depositMoney() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number: ");
        String accNumber = scanner.nextLine().trim();

        AccountUser found = AccountUser.getUserByAccountNumber(accNumber);

        if (found != null) {
            BankAccount target = found.getBank();
            if (target == null) {
                target = new BankAccount(found);
                found.setBank(target);
            }

            double amount;
            do {
                System.out.print("Enter deposit amount (₵): ");
                while (!scanner.hasNextDouble()) {
                    System.out.print("Invalid input. Enter deposit amount (₵): ");
                    scanner.next();
                }
                amount = scanner.nextDouble();
                scanner.nextLine();

                if (amount <= 0) {
                    System.out.println("❌ Failed to deposit. Invalid amount. Try again");
                }
            } while (amount <= 0);

            target.balance += amount;
            System.out.println("Deposit of (₵) " + amount + " accepted.");
            System.out.println("New balance: (₵) " + target.balance);

        } else {
            System.out.println("❌ No user with that account number.");
            System.out.println("Create an account to be able to deposit");
            System.out.print("Yes / No : ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("Yes")) {
                AccountUser newUser = new AccountUser();
                newUser.createAccount(); // will create and link a BankAccount
                System.out.println("Account Created.\nRestart Process to deposit into newly created account");
            } else {
                System.out.println("Failed to deposit");
                System.out.println("Thanks for using Bank-It");
            }
        }
    }

    // withdraw money from a found user's bank account
    public void withDraw() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number to proceed : ");
        String accNumber = scanner.nextLine().trim();

        AccountUser found = AccountUser.getUserByAccountNumber(accNumber);

        if (found != null) {
            BankAccount target = found.getBank();
            if (target == null) {
                System.out.println("Bank account missing for this user.");
                return;
            }

            System.out.println("✅✅✅ : " + found);

            System.out.print("Enter amount to withdraw : ");
            while (!scanner.hasNextDouble()) {
                System.out.print("Invalid input. Enter amount to withdraw : ");
                scanner.next();
            }
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount > target.balance) {
                System.out.println("Invalid amount (₵): " + amount + ". Only (₵) " + target.balance + " available.");
                System.out.println("Deposit : (₵) " + ((amount - target.balance) + 100) + " to withdraw again");
                System.out.println("Thanks for using Bank-It");
            } else {
                target.balance -= amount;
                System.out.println("Withdrawal of (₵) " + amount + " successful");
                System.out.println("Current account balance : (₵) " + target.balance);
            }
        } else {
            System.out.println("❌ No user with that account number.");
            System.out.println("Withdrawal unsuccessful");
            System.out.println("Create an account to access our withdrawal services, Thank You");
        }
    }

    // show account balance for a found user's bank account
    public void accountBalance() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter account number: ");
        String accNumber = scanner.nextLine().trim();

        AccountUser found = AccountUser.getUserByAccountNumber(accNumber);

        if (found != null) {
            BankAccount target = found.getBank();
            if (target == null) {
                System.out.println("Bank account missing for this user.");
                return;
            }

            System.out.println("✅✅✅ : " + found);
            System.out.println("Account Balance : ");
            System.out.println("(₵) " + target.balance);
        } else {
            System.out.println("❌❌❌ User not found");
            System.out.println("Fetching account balance failed");
        }
    }
}
