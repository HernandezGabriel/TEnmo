package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    //Added to handle Account services
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);

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
        
        userService.getListOfUsers(currentUser);
        System.out.println(userService.getListOfUsersAsString());

        String prompt = "Please enter a user ID from the list above: ";

        //

        boolean validSelection = true;
        int selection =0;


        do {
            selection = consoleService.promptForInt(prompt);

            //id isnt their own
            if(currentUser.getUser().getId().equals(selection)){
                System.out.println("ID can't be your own");
                validSelection=false;
            }

            //check id exists
            if(!userService.idExists(selection)){
                System.out.println("ID doesn't exist");
                validSelection=false;
            }


        }while(!validSelection);

        System.out.println("You Selected: "+ selection + " | "+ userService.getUsernameById(selection));

        prompt="How much TE Bucks would you like to send? ";


        validSelection=true;
        do{
            Long amount = consoleService.promptForBigDecimal(prompt).longValue();

            if(amount<=0){
                validSelection=false;
                System.out.println("Amount cannot be 0 or negative");
            }

            if(!accountService.hasEnoughFunds(amount)){
                validSelection=false;
                System.out.println("Insufficient funds");
            }




        }while(!validSelection);


        //check for enough funds

        //check that money went through




		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    



}
