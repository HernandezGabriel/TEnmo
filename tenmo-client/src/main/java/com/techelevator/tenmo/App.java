package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;



public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL, accountService);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
//        BasicLogger.log("APP RUNNING");

        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        try{
            System.out.println("Please register a new user account");
            UserCredentials credentials = consoleService.promptForCredentials();
            if (authenticationService.register(credentials)) {
                System.out.println("Registration successful. You can now login.");
            } else {
                consoleService.printErrorMessage();
            }
        }catch (Exception e){
            System.out.println("ERROR: Account not created");
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        try {
            currentUser = authenticationService.login(credentials);
            if (currentUser == null) {
                consoleService.printErrorMessage();
                handleLogin();
            }
        }
        catch (Exception e){
            System.out.println("ERROR LOGGING IN");
        }

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) { //view balance
                viewCurrentBalance();
            } else if (menuSelection == 2) { //View Detailed Transfer & View History
                viewTransferHistory();
                viewDetailedTransfer();
            } else if (menuSelection == 3) { //View History, approve or dny transfer
                viewTransferHistory();
                approveOrDenyTransfer();
                viewCurrentBalance();
            } else if (menuSelection == 4) {//send bucks
                sendBucks();
                viewCurrentBalance();
            } else if (menuSelection == 5) {//request bucks
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    //Uses getMyBalance from account service to display an updated balance to the user
    private void viewCurrentBalance() {
        System.out.println("Your Current Balance is : " + accountService.getMyBalance(currentUser));
    }

    //Uses transferService to Display a formatted string of transfers from & to the user,
    // followed up by either viewDetailedTransfer() or ApproveOrDenyTransfer() in the main menu
    private void viewTransferHistory() {
        System.out.println(transferService.getMyTransferHistoryAsFormattedString(currentUser));
    }

    //Prompts the user to select an id from their list of transfers,
    //Displays a detailed view of a single transfer using transferService class
    private void viewDetailedTransfer(){
        try {
            int selection = consoleService.promptForInt("Enter a Transfer Id from the list above to view details\nor select 0 to continue");
            if (selection==0){  return;}
            else{
                System.out.println(transferService.getTransferDetails(currentUser, selection));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    // Prompts the user to select an id from list of requests
    // and prompts the user for a boolean
    // then uses transferService to handle the rest of the logic and validation.
    private void approveOrDenyTransfer(){
        try {
            int selection = consoleService.promptForInt("To APPROVE OR DENY, enter a an ID from REQUESTS RECEIVED\nOr enter 0 to continue");
            if (selection==0){  return;}
            else{
                boolean TorF = consoleService.promptForBoolean("Please enter either True [Approve] or False [Deny]");
                System.out.println("\n"+transferService.approveOrDenyTransfer(currentUser,selection,TorF)+"\n");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            //approveOrDenyTransfer();
        }
    }

    // display list of users using UserService
    // prompts user to select a user ID to send money to using getValidUserId()
    // prompts user to enter an amount using getValidAmount()
    // Uses account service to get toAccount and fromAccount
    // creates corresponding transferStatus and Transfer type objects.
    // finally, creates a new Transfer and uses transferService to postTransfer()
    private void sendBucks() {
        System.out.println(userService.getListOfUsersAsString(currentUser));

        int selectedUserId = getValidUserId();
        long selectedAmount = getValidAmount();

        Account fromAccount = accountService.getMyAccount(currentUser);
        Account toAccount = accountService.findAccountFromUserId(selectedUserId,currentUser);
        TransferStatus ts = new TransferStatus(1);//pending
        TransferType tt = new TransferType(2); //send

        Transfer newTransfer = new Transfer(0,ts,tt,fromAccount,toAccount,selectedAmount);
        //post transfer!
        System.out.println("\n"+transferService.postTransfer(currentUser,newTransfer)+"\n");

    }

    //Uses ConsoleService to prompt for an ID/integer from the user
    //checks that ID isn't the same as current user
    //uses UserService to check ID exist in the DB
    private int getValidUserId(){
        //set to false if selection invalid
        boolean validSelection;
        int accountSelection =0;
        do {
            validSelection=true; //t by default
            accountSelection = consoleService.promptForInt("Please enter a user ID from the list above: ");

            if(currentUser.getUser().getUserId()==((long)(accountSelection))){
                System.out.println("ID can't be your own");
                validSelection=false;
            }

            if(!userService.idExists(accountSelection)){
                System.out.println("ID doesn't exist");
                validSelection=false;
            }
        }while(!validSelection);
        System.out.println("You Selected: "+ accountSelection + " | "+ userService.getUsernameById(accountSelection));
        return accountSelection;
    }

    //Prompts user for an amount greater than 0 using console service
    //then checks users balance for enoughFunds using AccountService
    private long getValidAmount(){
        boolean validSelection;
        long amount=0L;
        do{
            validSelection=true;
            amount = consoleService.promptForLong("How much TE Bucks would you like to send? ");
            if(amount<=0){
                validSelection=false;
                System.out.println("Amount cannot be 0 or negative");
            }
            if(validSelection==true){
                if(!accountService.hasEnoughFunds(amount, currentUser)){
                    validSelection=false;
                    System.out.println("Insufficient funds");
                }
            }
        }while(!validSelection);

        return amount;
    }

    // display list of users using userService
    // Uses account service to get toAccount and fromAccount
    // creates corresponding transferStatus and Transfer type objects.
    // finally, creates a new Transfer and uses transferService to postTransfer()
    private void requestBucks() {
        System.out.println(userService.getListOfUsersAsString(currentUser));
        int selectedUserId = getValidUserId();
        boolean validSelection;
        long amount=0L;
        do{
            validSelection=true;
            amount = consoleService.promptForLong("How much TE Bucks would you like to Request? ");
            if(amount<=0) {
                validSelection = false;
                System.out.println("Amount cannot be 0 or negative");
            }}
        while(!validSelection);

        System.out.println("\n"+
                transferService.postTransfer(currentUser,
                        new Transfer(0,
                                new TransferStatus(1), // pending
                                new TransferType(1), //request
                                accountService.findAccountFromUserId(selectedUserId, currentUser),
                                accountService.getMyAccount(currentUser),
                                amount)) +"\n" ) ;

    }





}
