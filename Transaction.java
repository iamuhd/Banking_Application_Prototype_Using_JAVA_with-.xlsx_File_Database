
package example.SystemFiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type; // e.g., "Top Up", "Withdrawal", "Transfer In", "Transfer Out"
    private double amount;
    private LocalDateTime timestamp;
    private String description; // e.g., "Funder Bank: ...", "To Account: ..."
    private int relatedAccountNumber; // For transfers

    // Constructor for Top Up/Withdrawal
    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.relatedAccountNumber = 0; // Not applicable or default
    }

    // Constructor for Transfers
    public Transaction(String type, double amount, String description, int relatedAccountNumber) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.relatedAccountNumber = relatedAccountNumber;
    }

    // Constructor for loading from file (with timestamp string)
    public Transaction(String type, String amountStr, String timestampStr, String description, String relatedAccNumStr) {
        this.type = type;
        try {
            this.amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            this.amount = 0.0;
        }
        try {
            this.timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            this.timestamp = LocalDateTime.now(); // Fallback
        }
        this.description = description;
        try {
            this.relatedAccountNumber = Integer.parseInt(relatedAccNumStr);
        } catch (NumberFormatException e) {
            this.relatedAccountNumber = 0;
        }
    }


    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public int getRelatedAccountNumber() {
        return relatedAccountNumber;
    }

    public String toFileString() {
        return String.join(";",
                type,
                String.valueOf(amount),
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), // Format timestamp for saving
                description,
                String.valueOf(relatedAccountNumber)
        );
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f - %s (Acc: %d) on %s",
                (type.contains("Top Up") || type.contains("Transfer In")) ? "+" : "-",
                type, amount, description, relatedAccountNumber, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}