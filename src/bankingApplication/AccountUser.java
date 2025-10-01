package bankingApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AccountUser {
    // fields
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String address;
    private String phoneNumber;
    private String accountNumber;

    // one static map to store users. Keys: accountNumber and email both map to the same object.
    private static final Map<String, AccountUser> userMap = new HashMap<>();

    // DO NOT instantiate BankAccount here to avoid recursion
    private BankAccount bank;

    // ----- constructors -----
    public AccountUser() {
        // no-op, we'll fill fields in createAccount()
    }

    public AccountUser(String firstName,
                       String lastName,
                       String email,
                       int age,
                       String address,
                       String phoneNumber,
                       String accountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
    }

    // bank getter/setter
    public BankAccount getBank() {
        return bank;
    }

    public void setBank(BankAccount bank) {
        this.bank = bank;
    }

    // ----- static lookup helpers -----
    public static AccountUser getUserByAccountAndEmail(String accountNumber, String email) {
        AccountUser u = userMap.get(accountNumber); // get by account number
        if (u != null && u.email != null && u.email.equalsIgnoreCase(email)) {
            return u; // both match
        }
        return null; // not found or email mismatch
    }

    public static AccountUser getUser(String key) {
        return userMap.get(key); // generic lookup by either email or account number
    }

    public static AccountUser getUserByAccountNumber(String accountNumber) {
        return userMap.get(accountNumber);
    }

    // optional helper to remove a user (removes both keys)
    public static boolean removeUser(AccountUser u) {
        if (u == null) return false;
        boolean removed = false;
        if (u.accountNumber != null && userMap.remove(u.accountNumber) != null) removed = true;
        if (u.email != null && userMap.remove(u.email) != null) removed = true;
        return removed;
    }

    // ----- account creation (interactive) -----
    public void createAccount() {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine().trim();

            if (firstName.isEmpty()) {
                System.out.println("❌ Error: First name cannot be empty.");
            } else if (hasDigit(firstName)) {
                System.out.println("❌ Error: First name cannot contain numbers.");
            }

        } while (firstName.isEmpty() || hasDigit(firstName));
        System.out.println("✅");

        do {
            System.out.print("Enter last name: ");
            lastName = scanner.nextLine().trim();

            if (lastName.isEmpty()) {
                System.out.println("❌ Error: Last name cannot be empty.");
            } else if (hasDigit(lastName)) {
                System.out.println("❌ Error: Last name cannot contain numbers.");
            }

        } while (lastName.isEmpty() || hasDigit(lastName));
        System.out.println("✅");

        // email validation
        do {
            System.out.print("Enter email address: ");
            email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("❌ Error: Email cannot be empty.");
            } else if (!isValidEmail(email)) {
                System.out.println("❌ Error: Email invalid. Example: user@example.com");
            } else if (userMap.containsKey(email)) {
                System.out.println("❌ Error: Email already registered.");
            } else {
                break;
            }
        } while (true);

        // age input
        do {
            System.out.print("Enter age: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Enter age: ");
                scanner.next();
            }
            age = scanner.nextInt();
            scanner.nextLine();
            if (age < 18) {
                System.out.println("❌ Error: Must be 18 or older.");
            }
        } while (age < 18);

        // address
        do {
            System.out.print("Enter address: ");
            address = scanner.nextLine().trim();
            if (address.isEmpty()) {
                System.out.println("❌ Error: Address cannot be empty.");
            }
        } while (address.isEmpty());

        // phone number
        do {
            System.out.print("Enter phone number: ");
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.isEmpty()) {
                System.out.println("❌ Error: Phone number cannot be empty.");
            } else if (isOnlyLetters(phoneNumber)) {
                System.out.println("❌ Error: Phone number cannot contain only letters.");
            }
        } while (phoneNumber.isEmpty() || isOnlyLetters(phoneNumber));

        // now generate unique account number (ensure not already used)
        do {
            accountNumber = generateAccountNumber();
        } while (userMap.containsKey(accountNumber));

        // create and link the bank account for this user
        BankAccount bankAccount = new BankAccount(this);
        this.setBank(bankAccount);

        // register the user under both keys: accountNumber and email
        userMap.put(accountNumber, this);
        userMap.put(email, this);

        System.out.println("\nACCOUNT CREATED ✅");
        System.out.println("YOUR ACCOUNT NUMBER IS: " + accountNumber);
        System.out.println("DO NOT SHARE THIS CODE WITH ANYONE.\n");
    }

    public void loginUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter account number: ");
        String acc = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String em = scanner.nextLine().trim();

        AccountUser found = getUserByAccountAndEmail(acc, em);
        if (found != null) {
            System.out.println("Login successful. Welcome, " + found.firstName + "!");
        } else {
            System.out.println("Login failed: account + email do not match.");
        }
    }

    // to display user data
    public void userData() {
        System.out.println("\n================= 📋 ACCOUNT INFORMATION =================");
        System.out.println("👤  Name          : " + firstName + " " + lastName);
        System.out.println("📧  Email         : " + email);
        System.out.println("🎂  Age           : " + age + " years");
        System.out.println("🏠  Address       : " + address);
        System.out.println("📞  Phone Number  : " + phoneNumber);
        System.out.println("🔢  Account Number: " + accountNumber);
        double bal = (bank != null) ? bank.getBalance() : 0.0;
        System.out.printf("💰  Current Balance: ₵%.2f%n", bal);
        System.out.println("==========================================================\n");
    }

    @Override
    public String toString() {
        return "AccountUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }

    // ------- helper/regEx methods -------
    public static boolean isOnlyLetters(String input) {
        return input != null && input.matches("^[A-Za-z]+$");
    }

    public static boolean hasDigit(String input) {
        return input != null && input.matches(".*\\d.*");
    }

    public static boolean containsLetter(String input) {
        return input != null && input.matches(".*[A-Za-z].*");
    }

    public static boolean isValidEmail(String input) {
        if (input == null) return false;
        return input.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean containsLetterNumberSymbol(String input) {
        if (input == null) return false;
        boolean hasLetter = input.matches(".*[A-Za-z].*");
        boolean hasNumber = input.matches(".*\\d.*");
        boolean hasSymbol = input.matches(".*[^A-Za-z0-9].*");
        return hasLetter && hasNumber && hasSymbol;
    }

    // generate 8-char account number (alphanumeric)
    public static String generateAccountNumber() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int idx = (int) (Math.random() * chars.length());
            accountNumber.append(chars.charAt(idx));
        }
        return accountNumber.toString();
    }
}
