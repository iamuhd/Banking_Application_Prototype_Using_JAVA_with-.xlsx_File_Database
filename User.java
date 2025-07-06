package example.SystemFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // For sorting/limiting history

public class User {
    private String name;
    private String cnic;
    private String pin;
    private int accountNumber;
    private double balance;
    private List<Transaction> transactionHistory; // New field

    public User(String name, String cnic, String pin, int accountNumber, double initialBalance) {
        this.name = name;
        this.cnic = cnic;
        this.pin = pin;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>(); // Initialize
    }

    // New constructor for loading from file (which will include transaction history)
    // This constructor will be used by BankSystem.loadUsersFromFile()
    public User(String name, String cnic, String pin, int accountNumber, double balance, List<Transaction> history) {
        this.name = name;
        this.cnic = cnic;
        this.pin = pin;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>(history); // Deep copy
    }

    // Existing constructor (will be adapted or less used if history is always loaded)
    public User(String name, String cnic, String pin, String accountNumberStr, String balanceStr) {
        this.name = name;
        this.cnic = cnic;
        this.pin = pin;
        try {
            this.accountNumber = Integer.parseInt(accountNumberStr);
        } catch (NumberFormatException e) {
            this.accountNumber = 0; // Default or error handling
        }
        try {
            this.balance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            this.balance = 0.0; // Default or error handling
        }
        this.transactionHistory = new ArrayList<>(); // Initialize for new users
    }

    public String getName() {
        return name;
    }

    public String getCnic() {
        return cnic;
    }

    public String getPin() {
        return pin;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        // Return an unmodifiable list to prevent external modification
        return Collections.unmodifiableList(transactionHistory);
    }

    public void addTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }

    // Modified topUp method to accept relatedAccountNumber
    public void topUp(double amount, int funderAccountNumber) {
        this.balance += amount;
        String description = (funderAccountNumber != 0) ? "Top Up from " + funderAccountNumber : "Cash deposit";
        addTransaction(new Transaction("Top Up", amount, description, funderAccountNumber));
    }

    public boolean withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            addTransaction(new Transaction("Withdrawal", amount, "Cash withdrawal"));
            return true;
        } else {
            return false;
        }
    }

    // New methods for handling transfers to record transactions for both sender and receiver
    public boolean transferFunds(User receiver, double amount) {
        if (this.withdraw(amount)) { // This records a withdrawal for sender
            receiver.topUp(amount, this.getAccountNumber()); // Pass sender's account as related for receiver's top up
            // Add specific transfer transaction for sender
            addTransaction(new Transaction("Transfer Out", amount, "Transfer to", receiver.getAccountNumber()));
            // Add specific transfer transaction for receiver (already done in receiver.topUp)
            return true;
        }
        return false;
    }

    public boolean checkPin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public boolean hasFunds() {
        return this.balance > 0;
    }

    // This method will change significantly when saving to Excel
    public String toFileString() {
        // This method will primarily be used by BankSystem for internal logic,
        // but the actual Excel saving logic will be in BankSystem.saveUsersToFile()
        return String.join(",", name, cnic, pin, String.valueOf(accountNumber), String.valueOf(balance));
    }
}