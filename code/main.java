import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class BankAccount {
    int accountNumber;
    String accountHolder;
    float balance;
    int pin;
    BankAccount next;

    // Constructor
    public BankAccount(int accountNumber, String accountHolder, float balance, int pin) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.pin = pin;
        this.next = null;
    }
}

public class main {

    static BankAccount accounts; // Declare accounts as a class-level variable

    // Function to get the last account number from the file
    static int getLastAccountNumber() {
        File file = new File("account_data.txt");
        if (!file.exists()) {
            return 1000; // Default starting point if the file doesn't exist
        }

        try {
            Scanner scanner = new Scanner(file);
            int accountNumber = scanner.nextInt();
            scanner.close();
            return accountNumber;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 1000; // Default starting point if the file is empty
    }

    static BankAccount createAccount(String accountHolder, float initialBalance, int pin) {
        int accountNumberCounter = getLastAccountNumber() + 1;

        if (pin < 1000 || pin > 9999) {
            System.out.println("Invalid PIN. Please enter a 4-digit PIN.");
            return null;
        }

        return new BankAccount(accountNumberCounter, accountHolder, initialBalance, pin);
    }

    static void displaycheckBalance(BankAccount account) {
        System.out.printf("Account Number: %d\nAccount Holder: %s\nCurrent Balance: $%.2f\n",
                account.accountNumber, account.accountHolder, account.balance);
    }

    static void withdrawCash(BankAccount account, int pin, float amount) {
        if (pin != account.pin) {
            System.out.println("Invalid PIN. Withdrawal not allowed.");
            return;
        }

        if (amount > 0 && amount <= account.balance) {
            account.balance -= amount;
            System.out.printf("Withdrawn $%.2f. New balance: $%.2f\n", amount, account.balance);
            saveAccountData(); // Update account data
        } else {
            System.out.println("Invalid withdrawal amount or insufficient balance.");
        }
    }

    static void depositCash(BankAccount account, int pin, float amount) {
        if (pin != account.pin) {
            System.out.println("Invalid PIN. Deposit not allowed.");
            return;
        }

        if (amount > 0) {
            account.balance += amount;
            System.out.printf("Deposited $%.2f. New balance: $%.2f\n", amount, account.balance);
            saveAccountData(); // Update account data
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    static void deleteAccount(int accountNumber, int pin) {
        BankAccount prev = null;
        BankAccount current = accounts;

        while (current != null) {
            if (current.accountNumber == accountNumber) {
                if (pin != current.pin) {
                    System.out.println("Invalid PIN. Account not deleted.");
                    return;
                }
                if (prev != null) {
                    prev.next = current.next;
                } else {
                    accounts = current.next;
                }

                saveAccountData(); // Update account data

                System.out.println("Account deleted successfully.");
                return;
            }
            prev = current;
            current = current.next;
        }

        System.out.println("Account not found.");
    }

    static void saveAccountData() {
        try {
            FileWriter writer = new FileWriter("account_data.txt");
            BankAccount current = accounts;
            while (current != null) {
                writer.write(String.format("%d,%s,%.2f,%d\n", current.accountNumber, current.accountHolder,
                        current.balance, current.pin));
                current = current.next;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not open the file for saving data.");
        }
    }

    static BankAccount loadAccountData() {
        File file = new File("account_data.txt");
        if (!file.exists()) {
            return null;
        }

        BankAccount accounts = null;

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                int accountNumber = Integer.parseInt(data[0]);
                String accountHolder = data[1];
                float balance = Float.parseFloat(data[2]);
                int pin = Integer.parseInt(data[3]);
                BankAccount newAccount = createAccount(accountHolder, balance, pin);
                if (newAccount != null) {
                    newAccount.accountNumber = accountNumber;
                    newAccount.next = accounts;
                    accounts = newAccount;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    public static void main(String[] args) {
        accounts = loadAccountData(); // Load account data
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("Welcome to the TitanVault Bank!");

        do {
            System.out.println("\nMain Menu:\n1. Open Account\n2. Withdraw Cash\n3. Deposit Cash\n4. Check Balance\n5. Delete Account\n6. Exit\nEnter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1: {
                    System.out.println("Enter Account Holder Name: ");
                    scanner.nextLine(); // Consume newline left-over
                    String accountHolder = scanner.nextLine();
                    System.out.println("Enter Initial Balance: $");
                    float initialBalance = scanner.nextFloat();
                    System.out.println("Enter a 4-digit PIN: ");
                    int pin = scanner.nextInt();
                    BankAccount newAccount = createAccount(accountHolder, initialBalance, pin);
                    if (newAccount != null) {
                        newAccount.next = accounts;
                        accounts = newAccount;
                        saveAccountData(); // Save account data before exiting
                        System.out.println("Account created successfully. Account Number: " + newAccount.accountNumber);
                    } else {
                        System.out.println("Failed to create the account.");
                    }
                    break;
                }
                case 2: {
                    System.out.println("Enter Account Number: ");
                    int accountNumber = scanner.nextInt();
                    BankAccount account = accounts;
                    while (account != null) {
                        if (account.accountNumber == accountNumber) {
                            System.out.println("Enter PIN: ");
                            int pin = scanner.nextInt();
                            System.out.println("Enter Amount to Withdraw: $");
                            float amount = scanner.nextFloat();
                            withdrawCash(account, pin, amount);
                            break;
                        }
                        account = account.next;
                    }
                    if (account == null) {
                        System.out.println("Account not found.");
                    }
                    break;
                }
                case 3: {
                    System.out.println("Enter Account Number: ");
                    int accountNumber = scanner.nextInt();
                    BankAccount account = accounts;
                    while (account != null) {
                        if (account.accountNumber == accountNumber) {
                            System.out.println("Enter PIN: ");
                            int pin = scanner.nextInt();
                            System.out.println("Enter Amount to Deposit: $");
                            float amount = scanner.nextFloat();
                            depositCash(account, pin, amount);
                            break;
                        }
                        account = account.next;
                    }
                    if (account == null) {
                        System.out.println("Account not found.");
                    }
                    break;
                }
                case 4: {
                    System.out.println("Enter Account Number: ");
                    int accountNumber = scanner.nextInt();
                    BankAccount account = accounts;
                    while (account != null) {
                        if (account.accountNumber == accountNumber) {
                            displaycheckBalance(account);
                            break;
                        }
                        account = account.next;
                    }
                    if (account == null) {
                        System.out.println("Account not found.");
                    }
                    break;
                }
                case 5: {
                    System.out.println("Enter Account Number: ");
                    int accountNumber = scanner.nextInt();
                    BankAccount account = accounts;
                    while (account != null) {
                        if (account.accountNumber == accountNumber) {
                            System.out.println("Enter PIN: ");
                            int pin = scanner.nextInt();
                            deleteAccount(accountNumber, pin);
                            break;
                        }
                        account = account.next;
                    }
                    if (account == null) {
                        System.out.println("Account not found.");
                    }
                    break;
                }
                case 6:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
