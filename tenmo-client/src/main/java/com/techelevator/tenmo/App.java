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
    //modified with try catch
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
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
                viewDetailedTransfer();
            } else if (menuSelection == 3) {
                viewPendingRequests();
                approveOrDenyTransfer();
                viewCurrentBalance();
            } else if (menuSelection == 4) {
                sendBucks();
                viewCurrentBalance();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        System.out.println("Your Current Balance is : " + accountService.getMyBalance(currentUser));
    }

    private void viewTransferHistory() {
        System.out.println(transferService.getMyTransferHistoryAsFormattedString(currentUser));
        //viewDetailedTransfer();
    }

    private void viewDetailedTransfer(){
        try {
            int selection = consoleService.promptForInt("Enter a Transfer Id from the list above to view details or select 0 to continue");
            if (selection==0){return;}
            else{
                System.out.println(transferService.getTransferDetails(currentUser, selection));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            viewDetailedTransfer();
        }
    }

	private void viewPendingRequests() {
        viewTransferHistory();
        //approveOrDenyTransfer();
    }

    private void approveOrDenyTransfer(){
        try {
            int selection = consoleService.promptForInt("To APPROVE OR DENY, Enter a Transfer Id from the REQUESTS RECEIVED list above or select 0 to continue");
            if (selection==0){return;}
            else{
                boolean TorF = consoleService.promptForBoolean("Please enter either True [Approve] or False [Deny]");
                System.out.println(transferService.approveOrDenyTransfer(currentUser,selection,TorF));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            //approveOrDenyTransfer();
        }
    }

    private void sendBucks() {

        // display list of users
        System.out.println(userService.getListOfUsersAsString(currentUser));

        //prompt for user input
        int selectedUserId = getValidUserId();
        long selectedAmount = getValidAmount();

        Account fromAccount = accountService.getMyAccount(currentUser);
        Account toAccount = accountService.findAccountFromUserId(selectedUserId,currentUser);
        TransferStatus ts = new TransferStatus(1);//pending
        TransferType tt = new TransferType(2); //send

        Transfer newTransfer = new Transfer(0,ts,tt,fromAccount,toAccount,selectedAmount);
        //post transfer!
        System.out.println(transferService.postTransfer(currentUser,newTransfer));
        viewCurrentBalance();
    }
    //checks id isn't users own and id exists for send bucks
    private int getValidUserId(){
        //set to false if selection invalid
        boolean validSelection;
        int accountSelection =0;
        do {
            validSelection=true; //t by default
            accountSelection = consoleService.promptForInt("Please enter a user ID from the list above: ");
            //id isnt their own
            if(currentUser.getUser().getUserId()==((long)(accountSelection))){
                System.out.println("ID can't be your own");
                validSelection=false;
            }
            //check id exists
            if(!userService.idExists(accountSelection)){
                System.out.println("ID doesn't exist");
                validSelection=false;
            }
        }while(!validSelection);
        System.out.println("You Selected: "+ accountSelection + " | "+ userService.getUsernameById(accountSelection));
        return accountSelection;
    }
    //checks amount is greater than 0 and sufficient funds
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
            //check for enough funds
            if(!accountService.hasEnoughFunds(amount, currentUser)){
                validSelection=false;
                System.out.println("Insufficient funds");
            }
        }while(!validSelection);

        return amount;
    }

    private void requestBucks() {
        //display users
        System.out.println(userService.getListOfUsersAsString(currentUser));
        //get user id
        int selectedUserId = getValidUserId();
        //valid amount
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

        System.out.println(
                transferService.postTransfer(currentUser,
                        new Transfer(0,
                                new TransferStatus(1), // pending
                                new TransferType(1), //request
                                accountService.findAccountFromUserId(selectedUserId, currentUser),
                                accountService.getMyAccount(currentUser),
                                amount)));

        // TODO Auto-generated method stub

    }





}
