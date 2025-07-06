package example.App;

import example.SystemFiles.BankSystem;
import example.SystemFiles.User;
import example.SystemFiles.Transaction; // Import Transaction

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections; // For ListView

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private Stage primaryStage;
    private BankSystem bankSystem;
    private User currentUser;

    private Label welcomeLabel;
    private Label balanceLabel;
    private Label accountNoLabel;
    private Label cnicLabel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.bankSystem = new BankSystem();
        this.currentUser = null;

        primaryStage.setTitle("Banking Application");
        primaryStage.setResizable(false);

        showLoginCreateAccountScene();

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> bankSystem.saveUsersToFile());
    }

    private void showLoginCreateAccountScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        Label title = new Label("Welcome to the Banking\nApplication");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        grid.add(title, 0, 0, 2, 1);

        Label accNoLabel = new Label("Account Number:");
        TextField accNoField = new TextField();
        accNoField.setPromptText("Enter Account Number");
        accNoField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(accNoLabel, 0, 1);
        grid.add(accNoField, 1, 1);

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Enter PIN");
        pinField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(pinLabel, 0, 2);
        grid.add(pinField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(120);
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));

        Button createAccountButton = new Button("Create New Account");
        createAccountButton.setPrefWidth(180);
        createAccountButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;");
        createAccountButton.setOnMouseEntered(e -> createAccountButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));
        createAccountButton.setOnMouseExited(e -> createAccountButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));

        HBox hbButtons = new HBox(10);
        hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
        hbButtons.getChildren().addAll(loginButton, createAccountButton);
        grid.add(hbButtons, 1, 3);

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);
        grid.add(messageLabel, 1, 4);

        loginButton.setOnAction(e -> {
            String accNumStr = accNoField.getText().trim();
            String pin = pinField.getText().trim();
            if (accNumStr.isEmpty() || pin.isEmpty()) {
                messageLabel.setText("Please enter both account number and PIN.");
                return;
            }
            try {
                int accNum = Integer.parseInt(accNumStr);
                currentUser = bankSystem.authenticateUser(accNum, pin);
                if (currentUser != null) {
                    messageLabel.setText("");
                    showBankingMenuScene();
                } else {
                    messageLabel.setText("Invalid Account Number or PIN.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Account Number must be a number.");
            }
        });

        createAccountButton.setOnAction(e -> showCreateAccountScene());

        Scene scene = new Scene(grid, 450, 300);
        primaryStage.setScene(scene);
    }

    private void showCreateAccountScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        Label title = new Label("Create New Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        grid.add(title, 0, 0, 2, 1);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your Name");
        nameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);

        Label cnicLabel = new Label("CNIC No.:");
        TextField cnicField = new TextField();
        cnicField.setPromptText("Enter your CNIC Number");
        cnicField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(cnicLabel, 0, 2);
        grid.add(cnicField, 1, 2);

        Label pinLabel = new Label("Set 4-digit PIN:");
        PasswordField pinField1 = new PasswordField();
        pinField1.setPromptText("Set PIN");
        pinField1.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(pinLabel, 0, 3);
        grid.add(pinField1, 1, 3);

        Label confirmPinLabel = new Label("Confirm PIN:");
        PasswordField pinField2 = new PasswordField();
        pinField2.setPromptText("Confirm PIN");
        pinField2.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(confirmPinLabel, 0, 4);
        grid.add(pinField2, 1, 4);

        Button createButton = new Button("Create Account");
        createButton.setPrefWidth(150);
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;");
        createButton.setOnMouseEntered(e -> createButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));
        createButton.setOnMouseExited(e -> createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));

        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(120);
        backButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8;"));

        HBox hbButtons = new HBox(10);
        hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
        hbButtons.getChildren().addAll(backButton, createButton);
        grid.add(hbButtons, 1, 5);

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);
        grid.add(messageLabel, 1, 6);

        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String cnic = cnicField.getText().trim();
            String pin1 = pinField1.getText().trim();
            String pin2 = pinField2.getText().trim();

            if (name.isEmpty() || cnic.isEmpty() || pin1.isEmpty() || pin2.isEmpty()) {
                messageLabel.setText("All fields are required.");
                return;
            }
            if (!pin1.matches("\\d{4}")) {
                messageLabel.setText("PIN must be exactly 4 digits.");
                return;
            }
            if (!pin1.equals(pin2)) {
                messageLabel.setText("PINs do not match.");
                return;
            }

            User newUser = bankSystem.createUser(name, cnic, pin1);
            showAlert(Alert.AlertType.INFORMATION, "Account Created", "Account created successfully!\nYour Account Number is: " + newUser.getAccountNumber());
            showLoginCreateAccountScene();
        });

        backButton.setOnAction(e -> showLoginCreateAccountScene());

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
    }

    private void showBankingMenuScene() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        welcomeLabel = new Label("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        accountNoLabel = new Label("Account No: " + currentUser.getAccountNumber());
        accountNoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        cnicLabel = new Label("CNIC: " + currentUser.getCnic());
        cnicLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        balanceLabel = new Label(String.format("Current Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        updateBankingMenuLabels();

        Button topUpButton = createStyledButton("Top Up Account", "#4CAF50");
        Button transferButton = createStyledButton("Transfer Funds", "#2196F3");
        Button checkHistoryButton = createStyledButton("Check History", "#FFC107"); // New button
        Button logoutButton = createStyledButton("Logout", "#F44336");
        Button deleteAccountButton = createStyledButton("Delete Account", "#9E9E9E");

        topUpButton.setOnAction(e -> showTopUpScene());
        transferButton.setOnAction(e -> showTransferScene());
        checkHistoryButton.setOnAction(e -> showHistoryScene()); // New action
        logoutButton.setOnAction(e -> {
            currentUser = null;
            showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out.");
            showLoginCreateAccountScene();
        });
        deleteAccountButton.setOnAction(e -> handleDeleteAccountGUI());

        vbox.getChildren().addAll(welcomeLabel, accountNoLabel, cnicLabel, balanceLabel,
                topUpButton, transferButton, checkHistoryButton, logoutButton, deleteAccountButton); // Add checkHistoryButton

        Scene scene = new Scene(vbox, 500, 600); // Increased height to accommodate new button
        primaryStage.setScene(scene);
    }

    private void updateBankingMenuLabels() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
            accountNoLabel.setText("Account No: " + currentUser.getAccountNumber());
            cnicLabel.setText("CNIC: " + currentUser.getCnic());
            balanceLabel.setText(String.format("Current Balance: $%.2f", currentUser.getBalance()));
        }
    }

    private void showTopUpScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        Label title = new Label("Top Up Account");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        grid.add(title, 0, 0, 2, 1);

        Label funderTypeLabel = new Label("Funder Account Type:");
        ComboBox<String> funderTypeComboBox = new ComboBox<>();
        funderTypeComboBox.getItems().addAll("Bank", "Microfinance Bank");
        funderTypeComboBox.setValue("Bank");
        funderTypeComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(funderTypeLabel, 0, 1);
        grid.add(funderTypeComboBox, 1, 1);

        Label funderAccNoLabel = new Label("Funder Account No.:");
        TextField funderAccNoField = new TextField();
        funderAccNoField.setPromptText("Enter Funder Account Number (optional)");
        funderAccNoField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(funderAccNoLabel, 0, 2);
        grid.add(funderAccNoField, 1, 2);

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to top up");
        amountField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(amountLabel, 0, 3);
        grid.add(amountField, 1, 3);

        Button topUpButton = createStyledButton("Top Up", "#4CAF50");
        Button backButton = createStyledButton("Back", "#F44336");

        HBox hbButtons = new HBox(10);
        hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
        hbButtons.getChildren().addAll(backButton, topUpButton);
        grid.add(hbButtons, 1, 4);

        topUpButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Amount must be positive.");
                    return;
                }

                int funderAccNum = 0; // Default to 0 if not provided or invalid
                String funderAccNumStr = funderAccNoField.getText().trim();
                if (!funderAccNumStr.isEmpty()) {
                    try {
                        funderAccNum = Integer.parseInt(funderAccNumStr);
                    } catch (NumberFormatException ex) {
                        // Removed: showAlert(Alert.AlertType.WARNING, "Invalid Funder Account", "Funder account number is invalid, proceeding without it.");
                        // funderAccNum remains 0, as intended for external/unspecified funder
                    }
                }

                currentUser.topUp(amount, funderAccNum); // Pass funderAccNum
                bankSystem.saveUsersToFile();
                updateBankingMenuLabels();
                showAlert(Alert.AlertType.INFORMATION, "Top Up Successful", String.format("Successfully topped up $%.2f.", amount));
                showBankingMenuScene();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for amount.");
            }
        });

        backButton.setOnAction(e -> showBankingMenuScene());

        Scene scene = new Scene(grid, 500, 350);
        primaryStage.setScene(scene);
    }

    private void showTransferScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        Label title = new Label("Transfer Funds");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        grid.add(title, 0, 0, 2, 1);

        Label receiverTypeLabel = new Label("Receiver Account Type:");
        ComboBox<String> receiverTypeComboBox = new ComboBox<>();
        receiverTypeComboBox.getItems().addAll("Bank", "Microfinance Bank");
        receiverTypeComboBox.setValue("Bank");
        receiverTypeComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(receiverTypeLabel, 0, 1);
        grid.add(receiverTypeComboBox, 1, 1);

        Label receiverAccNoLabel = new Label("Receiver Account No.:");
        TextField receiverAccNoField = new TextField();
        receiverAccNoField.setPromptText("Enter Receiver Account Number");
        receiverAccNoField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(receiverAccNoLabel, 0, 2);
        grid.add(receiverAccNoField, 1, 2);

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to transfer");
        amountField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        grid.add(amountLabel, 0, 3);
        grid.add(amountField, 1, 3);

        Button transferButton = createStyledButton("Transfer", "#2196F3");
        Button backButton = createStyledButton("Back", "#F44336");

        HBox hbButtons = new HBox(10);
        hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
        hbButtons.getChildren().addAll(backButton, transferButton);
        grid.add(hbButtons, 1, 4);

        transferButton.setOnAction(e -> {
            String receiverAccNumStr = receiverAccNoField.getText().trim();
            String receiverType = receiverTypeComboBox.getValue();

            if (receiverAccNumStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Receiver Account Number cannot be empty.");
                return;
            }

            try {
                int receiverAccNum = Integer.parseInt(receiverAccNumStr);
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Transfer amount must be positive.");
                    return;
                }
                if (currentUser.getBalance() < amount) {
                    showAlert(Alert.AlertType.ERROR, "Transfer Failed", "Insufficient funds in your account.");
                    return;
                }

                // Call the centralized transfer method in BankSystem
                boolean transferSuccessful = bankSystem.processFundTransfer(currentUser, receiverAccNum, receiverType, amount);

                if (transferSuccessful) {
                    updateBankingMenuLabels();
                    showAlert(Alert.AlertType.INFORMATION, "Transfer Successful", "Funds transferred successfully.");
                    showBankingMenuScene();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Transfer Failed", "An unexpected error occurred during transfer or insufficient funds.");
                }

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers for account and amount.");
            }
        });

        backButton.setOnAction(e -> showBankingMenuScene());

        Scene scene = new Scene(grid, 500, 350);
        primaryStage.setScene(scene);
    }

    // New method to show transaction history
    private void showHistoryScene() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.TOP_LEFT); // Align to top-left for better list viewing
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: #f0f2f5; -fx-font-family: 'Inter';");

        Label title = new Label("Recent Transactions for " + currentUser.getName());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        VBox.setMargin(title, new Insets(0, 0, 15, 0)); // Add some bottom margin

        ListView<Transaction> historyListView = new ListView<>();
        historyListView.setPrefHeight(250); // Set a preferred height for the list
        historyListView.setItems(FXCollections.observableArrayList(currentUser.getTransactionHistory()));
        historyListView.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ccc;");


        Button backButton = createStyledButton("Back to Menu", "#F44336");
        backButton.setOnAction(e -> showBankingMenuScene());

        vbox.getChildren().addAll(title, historyListView, backButton);

        Scene scene = new Scene(vbox, 550, 450); // Adjust scene size
        primaryStage.setScene(scene);
    }


    private void handleDeleteAccountGUI() {
        if (currentUser.hasFunds()) {
            showAlert(Alert.AlertType.WARNING, "Account Has Funds",
                    String.format("You have $%.2f in your account. Please transfer all funds before deletion.", currentUser.getBalance()));
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Account?");
        confirmationAlert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);
        if (result == ButtonType.OK) {
            bankSystem.deleteUser(currentUser);
            currentUser = null;
            showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been successfully deleted.");
            showLoginCreateAccountScene();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Deletion Cancelled", "Account deletion cancelled.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createStyledButton(String text, String colorHex) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + deriveColor(colorHex, 0.8) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 3);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"));
        return button;
    }

    private String deriveColor(String hexColor, double factor) {
        Color color = Color.web(hexColor);
        return color.darker().deriveColor(0, 1.0, factor, 1.0).toString().replace("0x", "#");
    }

    public static void main(String[] args) {
        launch(args);
    }
}