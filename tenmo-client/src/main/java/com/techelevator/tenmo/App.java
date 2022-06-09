package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    //Added to handle Account services
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

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

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
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
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
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
		// TODO Auto-generated method stub

        //check for current user;

        if(currentUser==null){
            System.out.println("Current User Cannot Be Null");
            return ;
        }

        //Set account in account service

        accountService.setAccount(currentUser);
        //accountService.getBalance();

        // SOUT
        System.out.println("Your Current Balance is : " + accountService.getBalance());

		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        // display list of users
        accountService.setAccount(currentUser);
        userService.getListOfUsers(currentUser);
        System.out.println(userService.getListOfUsersAsString());

        String prompt = "Please enter a user ID from the list above: ";

        //

        boolean validSelection = true;
        int accountSelection =0;


        do {
            validSelection=true;
            accountSelection = consoleService.promptForInt(prompt);

            //id isnt their own
            if(currentUser.getUser().getId().equals((long)(accountSelection))){
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

        prompt="How much TE Bucks would you like to send? ";

        Long amount=0L;
        do{
            validSelection=true;
            amount = consoleService.promptForBigDecimal(prompt).longValue();

            if(amount<=0){
                validSelection=false;
                System.out.println("Amount cannot be 0 or negative");
            }

            //check for enough funds
            if(!accountService.hasEnoughFunds(amount)){
                validSelection=false;
                System.out.println("Insufficient funds");
            }

        }while(!validSelection);

        Transfer newTransfer = new Transfer();
        newTransfer.setTransferId(0);
        newTransfer.setTransferStatusId(1); //pending = 1 //approved=2 //rejected =3
        newTransfer.setAmount(amount);
        newTransfer.setTransferTypeId(2); //1=request 2= send



        //TODO FIX ERROR: ACCOUNTS BEING PASSED ARE USER IDS AND NOT ACCOUNT IDS

        int fromAccount = accountService.findAccountIdFromUserId(Math.toIntExact(currentUser.getUser().getId()),currentUser);
        int toAccount= accountService.findAccountIdFromUserId(accountSelection, currentUser);
      newTransfer.setAccountFrom(fromAccount);
      newTransfer.setAccountTo(toAccount);

        Transfer returnedTransfer=transferService.postTransfer(currentUser,newTransfer);
        System.out.println(returnedTransfer.getTransferId());
        System.out.println(returnedTransfer.toString());






        //check that money went through
        viewCurrentBalance();



		
	}
	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    



}
