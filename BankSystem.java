package example.SystemFiles;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BankSystem {
    private List<User> users;
    private final String filePath = "bank_data.xlsx"; // Changed to .xlsx
    private int nextAccountNumber = 1001;

    public BankSystem() {
        this.users = new ArrayList<>();
        loadUsersFromFile();
    }

    private int generateAccountNumber() {
        int maxAccNum = 1000;
        for (User user : users) {
            if (user.getAccountNumber() > maxAccNum) {
                maxAccNum = user.getAccountNumber();
            }
        }
        nextAccountNumber = maxAccNum + 1;
        return nextAccountNumber;
    }

    public User createUser(String name, String cnic, String pin) {
        int newAccountNumber = generateAccountNumber();
        // Initial balance is 0, and no transactions yet
        User newUser = new User(name, cnic, pin, newAccountNumber, 0.0);
        users.add(newUser);
        saveUsersToFile(); // Save immediately after creating
        return newUser;
    }

    public User authenticateUser(int accountNumber, String pin) {
        for (User user : users) {
            if (user.getAccountNumber() == accountNumber && user.checkPin(pin)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByAccountNumber(int accountNumber) {
        for (User user : users) {
            if (user.getAccountNumber() == accountNumber) {
                return user;
            }
        }
        return null;
    }

    public boolean deleteUser(User userToDelete) {
        if (users.remove(userToDelete)) {
            saveUsersToFile(); // Save after deletion
            return true;
        }
        return false;
    }

    /**
     * Processes a fund transfer, handling both internal and external accounts.
     *
     * @param sender           The user initiating the transfer.
     * @param receiverAccNum   The account number of the receiver.
     * @param receiverType     The type of receiver account ("Bank" or "Microfinance Bank").
     * @param amount           The amount to transfer.
     * @return true if the transfer was successful, false otherwise.
     */
    public boolean processFundTransfer(User sender, int receiverAccNum, String receiverType, double amount) {
        if (sender == null || amount <= 0 || sender.getBalance() < amount) {
            return false; // Basic validation for sender and amount
        }

        boolean success = false;
        if ("Bank".equals(receiverType)) {
            User receiverUser = findUserByAccountNumber(receiverAccNum);
            if (receiverUser != null) {
                // Internal Bank Transfer: Account exists in the system
                success = sender.transferFunds(receiverUser, amount);
            } else {
                // External Bank Transfer: Bank account not found in system, treat as external
                if (sender.withdraw(amount)) {
                    sender.addTransaction(new Transaction("Transfer Out (External)", amount, "Transfer to External Bank", receiverAccNum));
                    success = true;
                }
            }
        } else if ("Microfinance Bank".equals(receiverType)) {
            // Microfinance Bank Transfer: Always treated as external
            if (sender.withdraw(amount)) {
                sender.addTransaction(new Transaction("Transfer Out (External)", amount, "Transfer to Microfinance Bank", receiverAccNum));
                success = true;
            }
        }

        if (success) {
            saveUsersToFile(); // Save changes after successful transfer
        }
        return success;
    }

    // --- Excel Specific Load/Save Methods ---

    public void loadUsersFromFile() {
        users.clear(); // Clear existing users
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No existing bank data Excel file found. Starting fresh.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Load Users from "Users" sheet
            Sheet userSheet = workbook.getSheet("Users");
            if (userSheet == null) {
                System.err.println("User sheet not found in Excel file. Cannot load users.");
                return;
            }

            Iterator<Row> rowIterator = userSheet.iterator();
            if (rowIterator.hasNext()) { // Skip header row
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getCell(0) == null) continue; // Skip empty rows

                String name = row.getCell(0).getStringCellValue();
                String cnic = row.getCell(1).getStringCellValue();
                String pin = row.getCell(2).getStringCellValue();
                int accountNumber = (int) row.getCell(3).getNumericCellValue();
                double balance = row.getCell(4).getNumericCellValue();

                // Load transactions for this user
                List<Transaction> userTransactions = loadTransactionsForUser(workbook, accountNumber);

                User user = new User(name, cnic, pin, accountNumber, balance, userTransactions);
                users.add(user);

                if (user.getAccountNumber() >= nextAccountNumber) {
                    nextAccountNumber = user.getAccountNumber() + 1;
                }
            }
            System.out.println("Bank data loaded successfully from " + filePath);

        } catch (IOException | NumberFormatException | IllegalStateException e) {
            System.err.println("Error loading bank data from Excel: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    private List<Transaction> loadTransactionsForUser(Workbook workbook, int accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        Sheet transactionSheet = workbook.getSheet("Transactions_" + accountNumber);
        if (transactionSheet == null) {
            return transactions; // No transaction sheet for this user
        }

        Iterator<Row> rowIterator = transactionSheet.iterator();
        if (rowIterator.hasNext()) { // Skip header row
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getCell(0) == null) continue; // Skip empty rows

            try {
                String type = row.getCell(0).getStringCellValue();
                double amount = row.getCell(1).getNumericCellValue();
                String timestampStr = row.getCell(2).getStringCellValue();
                String description = row.getCell(3).getStringCellValue();
                // Handle potential missing related account number cell
                int relatedAccountNumber = 0;
                Cell relatedAccCell = row.getCell(4);
                if (relatedAccCell != null && relatedAccCell.getCellType() == CellType.NUMERIC) {
                    relatedAccountNumber = (int) relatedAccCell.getNumericCellValue();
                } else if (relatedAccCell != null && relatedAccCell.getCellType() == CellType.STRING) {
                    try {
                        relatedAccountNumber = Integer.parseInt(relatedAccCell.getStringCellValue());
                    } catch (NumberFormatException e) { /* default to 0 */ }
                }


                transactions.add(new Transaction(type, String.valueOf(amount), timestampStr, description, String.valueOf(relatedAccountNumber)));
            } catch (Exception e) {
                System.err.println("Error reading transaction for account " + accountNumber + ": " + e.getMessage());
            }
        }
        return transactions;
    }


    public void saveUsersToFile() {
        try (Workbook workbook = new XSSFWorkbook(); // Create a new workbook
             FileOutputStream fos = new FileOutputStream(filePath)) {

            // --- Save Users Sheet ---
            Sheet userSheet = workbook.createSheet("Users");
            Row userHeaderRow = userSheet.createRow(0);
            userHeaderRow.createCell(0).setCellValue("Name");
            userHeaderRow.createCell(1).setCellValue("CNIC");
            userHeaderRow.createCell(2).setCellValue("PIN");
            userHeaderRow.createCell(3).setCellValue("Account Number");
            userHeaderRow.createCell(4).setCellValue("Balance");

            int userRowNum = 1;
            for (User user : users) {
                Row row = userSheet.createRow(userRowNum++);
                row.createCell(0).setCellValue(user.getName());
                row.createCell(1).setCellValue(user.getCnic());
                row.createCell(2).setCellValue(user.getPin());
                row.createCell(3).setCellValue(user.getAccountNumber());
                row.createCell(4).setCellValue(user.getBalance());

                // Auto-size columns for user sheet
                for(int i = 0; i < 5; i++) {
                    userSheet.autoSizeColumn(i);
                }
            }

            // --- Save Transactions for each User in separate sheets ---
            for (User user : users) {
                Sheet transactionSheet = workbook.createSheet("Transactions_" + user.getAccountNumber());
                Row transHeaderRow = transactionSheet.createRow(0);
                transHeaderRow.createCell(0).setCellValue("Type");
                transHeaderRow.createCell(1).setCellValue("Amount");
                transHeaderRow.createCell(2).setCellValue("Timestamp");
                transHeaderRow.createCell(3).setCellValue("Description");
                transHeaderRow.createCell(4).setCellValue("Related Account");

                int transRowNum = 1;
                for (Transaction transaction : user.getTransactionHistory()) { // Get the potentially limited history
                    Row row = transactionSheet.createRow(transRowNum++);
                    row.createCell(0).setCellValue(transaction.getType());
                    row.createCell(1).setCellValue(transaction.getAmount());
                    row.createCell(2).setCellValue(transaction.getTimestamp().toString()); // Use ISO_LOCAL_DATE_TIME
                    row.createCell(3).setCellValue(transaction.getDescription());
                    row.createCell(4).setCellValue(transaction.getRelatedAccountNumber());
                }

                // Auto-size columns for transaction sheet
                for(int i = 0; i < 5; i++) {
                    transactionSheet.autoSizeColumn(i);
                }
            }


            workbook.write(fos);
            System.out.println("Bank data saved successfully to " + filePath);

        } catch (IOException e) {
            System.err.println("Error saving bank data to Excel: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}