import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 * Fleet Management System of the Coconut Grove Sailing Club: tracks what the boats of the
 * club cost to buy and to maintain. The fleet is loaded from a CSV file whose name is
 * given on the command line (initialising run) or, when there is no command line
 * parameter, from the file FleetData.db in which the fleet was saved at the end of the
 * previous run. A menu then lets the user print the fleet, add a boat, remove a boat and
 * request permission to spend money on a boat, subject to the Commodore's policy. This
 * class does all the keyboard input and all the file handling of the program.
 *
 * @author Maxime Manuel Afonso
 */
public class FleetManagement {
    private static final String DB_FILE_NAME = "FleetData.db";
    private static final int CSV_FILE_NAME_ARGUMENT = 0; // position of the file name on the command line
    private static final String CSV_SEPARATOR = ",";
    private static final int NUMBER_OF_CSV_FIELDS = 6;
    private static final int TYPE_FIELD = 0; // positions of the fields in a line of CSV data
    private static final int NAME_FIELD = 1;
    private static final int YEAR_FIELD = 2;
    private static final int MAKE_AND_MODEL_FIELD = 3;
    private static final int LENGTH_FIELD = 4;
    private static final int PRICE_FIELD = 5;
    private static final char PRINT_OPTION = 'P';
    private static final char ADD_OPTION = 'A';
    private static final char REMOVE_OPTION = 'R';
    private static final char EXPENSE_OPTION = 'E';
    private static final char EXIT_OPTION = 'X';
    private static final char NO_OPTION = ' '; // the user typed nothing
    private static final int PROMPT_WIDTH = 44;
    private static final String PROMPT_FORMAT = "%-" + PROMPT_WIDTH + "s: "; // prompt formatting "%-44s: "
    private static final String BOAT_NOT_FOUND_MESSAGE = "Cannot find boat ";

    /**
     * Runs the Fleet Management System: welcome, load the fleet, menu, save the fleet, exit.
     *
     * @param args the name of the CSV file that holds the fleet data for an initialising
     *             run; nothing for a normal run, which reads the fleet saved in FleetData.db
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Fleet fleet;

        displayWelcomeMessage();
        try {
            // an initialising run names the CSV file on the command line, a normal run does not
            if (args.length > 0) {
                fleet = loadFleetFromCsvFile(args[CSV_FILE_NAME_ARGUMENT]);
            }
            else {
                fleet = loadFleetFromDbFile(DB_FILE_NAME);
            }
            processMenuOptions(keyboard, fleet);
            saveFleetToDbFile(fleet, DB_FILE_NAME);
        }
        catch (IOException exception) {
            System.out.println("ERROR: Cannot read or write the fleet data: " + exception.getMessage());
        }
        catch (ClassNotFoundException exception) {
            System.out.println("ERROR: " + DB_FILE_NAME + " does not hold a fleet: "
                               + exception.getMessage());
        }
        displayExitMessage();
    }// end of the main method

    /**
     * Displays the welcome message.
     */
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the Fleet Management System");
        System.out.println("--------------------------------------");
    }// end of the displayWelcomeMessage method

    /**
     * Loads a fleet from a text file of comma separated values, in which every line holds
     * the data of one boat.
     *
     * @param csvFileName the name of the CSV file
     * @return the fleet made of the boats described in the file, with no expenses yet
     * @throws IOException if the file cannot be opened or read
     */
    private static Fleet loadFleetFromCsvFile(String csvFileName) throws IOException {
        BufferedReader csvFile = new BufferedReader(new FileReader(csvFileName));
        Fleet fleet = new Fleet();
        String csvLine;

        try {
            csvLine = csvFile.readLine();
            while (csvLine != null) { // null means that the end of the file has been reached
                if (!csvLine.isBlank()) { // a line with no data is skipped
                    fleet.addBoat(createBoat(csvLine));
                }
                csvLine = csvFile.readLine();
            }// end of the while loop
        }
        finally {
            csvFile.close(); // the file is closed whether or not it could be read
        }
        return fleet;
    }// end of the loadFleetFromCsvFile method

    /**
     * Loads the fleet that was saved in serialized form in an object file.
     *
     * @param dbFileName the name of the object file
     * @return the fleet read from the file, with its boats and their expenses
     * @throws IOException            if the file cannot be opened or read
     * @throws ClassNotFoundException if the file does not hold a fleet
     */
    private static Fleet loadFleetFromDbFile(String dbFileName) throws IOException, ClassNotFoundException {
        ObjectInputStream dbFile = new ObjectInputStream(new FileInputStream(dbFileName));
        Fleet fleet;

        try {
            fleet = (Fleet) dbFile.readObject(); // the fleet and all its boats are read in one shot
        }
        finally {
            dbFile.close(); // the file is closed whether or not it could be read
        }
        return fleet;
    }// end of the loadFleetFromDbFile method

    /**
     * Saves a fleet in serialized form in an object file.
     *
     * @param fleet      the fleet to save
     * @param dbFileName the name of the object file
     * @throws IOException if the file cannot be opened or written
     */
    private static void saveFleetToDbFile(Fleet fleet, String dbFileName) throws IOException {
        ObjectOutputStream dbFile = new ObjectOutputStream(new FileOutputStream(dbFileName));

        try {
            dbFile.writeObject(fleet); // the fleet and all its boats are written in one shot
        }
        finally {
            dbFile.close(); // the file is closed whether or not it could be written
        }
    }// end of the saveFleetToDbFile method

    /**
     * Creates a boat from one line of CSV data: the type, name, year of manufacture,
     * make/model, length in feet and purchase price of the boat, separated by commas.
     *
     * @param csvData the data of the boat, e.g. SAILING,Finesse,1974,Tartan,34,9200.50
     * @return the boat described by the data, with no expenses yet
     * @throws IllegalArgumentException if the data does not have six fields, or if the
     *                                  type or one of the numbers is not valid
     */
    private static Boat createBoat(String csvData) {
        String[] fields = csvData.split(CSV_SEPARATOR);
        BoatType type;
        String name;
        short yearOfManufacture;
        String makeAndModel;
        byte lengthInFeet;
        double purchasePrice;

        if (fields.length != NUMBER_OF_CSV_FIELDS) {
            throw new IllegalArgumentException("Boat data must have " + NUMBER_OF_CSV_FIELDS + " fields");
        }
        // valueOf and the parse methods throw an IllegalArgumentException if a field is not valid
        type = BoatType.valueOf(fields[TYPE_FIELD].trim().toUpperCase());
        name = fields[NAME_FIELD].trim();
        yearOfManufacture = Short.parseShort(fields[YEAR_FIELD].trim());
        makeAndModel = fields[MAKE_AND_MODEL_FIELD].trim();
        lengthInFeet = Byte.parseByte(fields[LENGTH_FIELD].trim());
        purchasePrice = Double.parseDouble(fields[PRICE_FIELD].trim());

        return new Boat(type, name, yearOfManufacture, makeAndModel, lengthInFeet, purchasePrice);
    }// end of the createBoat method

    /**
     * Repeatedly gets a menu option and carries it out, until the user chooses to exit.
     *
     * @param keyboard the Scanner reading the keyboard
     * @param fleet    the fleet that the options print and change
     */
    private static void processMenuOptions(Scanner keyboard, Fleet fleet) {
        char optionLetter;

        do {
            optionLetter = getMenuOption(keyboard);
            switch (optionLetter) { // the exit option needs no case: it ends the loop
                case PRINT_OPTION:
                    printFleet(fleet);
                    break;
                case ADD_OPTION:
                    addBoat(keyboard, fleet);
                    break;
                case REMOVE_OPTION:
                    removeBoat(keyboard, fleet);
                    break;
                case EXPENSE_OPTION:
                    requestExpense(keyboard, fleet);
                    break;
            }// end of the switch statement
        } while (optionLetter != EXIT_OPTION);
    }// end of the processMenuOptions method

    /**
     * Displays the menu and reads the option chosen, asking again until it is one of the
     * five options (in upper or lower case).
     *
     * @param keyboard the Scanner reading the keyboard
     * @return the option as an upper case letter: PRINT_OPTION, ADD_OPTION, REMOVE_OPTION,
     *         EXPENSE_OPTION or EXIT_OPTION
     */
    private static char getMenuOption(Scanner keyboard) {
        char optionLetter;

        System.out.println();
        optionLetter = getOptionLetter(keyboard);

        while (optionLetter != PRINT_OPTION && optionLetter != ADD_OPTION && optionLetter != REMOVE_OPTION
               && optionLetter != EXPENSE_OPTION && optionLetter != EXIT_OPTION) {
            System.out.println("Invalid menu option, try again");
            optionLetter = getOptionLetter(keyboard);
        }
        return optionLetter;
    }// end of the getMenuOption method

    /**
     * Displays the menu and reads the first letter of what the user types.
     *
     * @param keyboard the Scanner reading the keyboard
     * @return the letter in upper case, or NO_OPTION if the user typed nothing
     */
    private static char getOptionLetter(Scanner keyboard) {
        String optionTyped = getUserInput(keyboard, "(P)rint, (A)dd, (R)emove, (E)xpense, e(X)it");
        char optionLetter = NO_OPTION;

        if (!optionTyped.isEmpty()) {
            optionLetter = Character.toUpperCase(optionTyped.charAt(0));
        }
        return optionLetter;
    }// end of the getOptionLetter method

    /**
     * Displays a prompt and reads the line that the user types in answer. A whole line is
     * read because the names of boats can be made of several words.
     *
     * @param keyboard the Scanner reading the keyboard
     * @param prompt   the text that tells the user what to type
     * @return the line typed, without the spaces before and after it
     */
    private static String getUserInput(Scanner keyboard, String prompt) {
        System.out.printf(PROMPT_FORMAT, prompt);
        return keyboard.nextLine().trim();
    }// end of the getUserInput method

    /**
     * Prints the boat inventory: the fleet report with one line for each boat and the totals.
     *
     * @param fleet the fleet to print
     */
    private static void printFleet(Fleet fleet) {
        System.out.println();
        System.out.println(fleet); // the toString method of the fleet makes the report
    }// end of the printFleet method

    /**
     * Adds a boat to the fleet: asks for the data of the new boat, typed like one line of
     * the CSV file. If the data is not valid no boat is added.
     *
     * @param keyboard the Scanner reading the keyboard
     * @param fleet    the fleet that the new boat joins
     */
    private static void addBoat(Scanner keyboard, Fleet fleet) {
        String csvData = getUserInput(keyboard, "Please enter the new boat CSV data");

        try {
            fleet.addBoat(createBoat(csvData));
        }
        catch (IllegalArgumentException exception) {
            // a field is missing or not valid: the program goes on with the fleet unchanged
            System.out.println("Invalid boat data, boat not added");
        }
    }// end of the addBoat method

    /**
     * Removes a boat from the fleet: asks for the name of the boat, in upper or lower
     * case, and reports if the fleet has no boat with that name.
     *
     * @param keyboard the Scanner reading the keyboard
     * @param fleet    the fleet that the boat leaves
     */
    private static void removeBoat(Scanner keyboard, Fleet fleet) {
        String boatName = getUserInput(keyboard, "Which boat do you want to remove?");

        if (!fleet.removeBoat(boatName)) {
            System.out.println(BOAT_NOT_FOUND_MESSAGE + boatName);
        }
    }// end of the removeBoat method

    /**
     * Requests permission to spend money on a boat: asks for the name of the boat, in
     * upper or lower case, and for the amount, then reports whether the Commodore's
     * policy authorizes the expense. Reports if the fleet has no boat with that name.
     *
     * @param keyboard the Scanner reading the keyboard
     * @param fleet    the fleet that holds the boat
     */
    private static void requestExpense(Scanner keyboard, Fleet fleet) {
        String boatName = getUserInput(keyboard, "Which boat do you want to spend on?");
        Boat boat = fleet.findBoat(boatName);
        double amount;

        if (boat == null) {
            System.out.println(BOAT_NOT_FOUND_MESSAGE + boatName);
        }
        else {
            try {
                amount = Double.parseDouble(getUserInput(keyboard, "How much do you want to spend?"));
                // the boat applies the Commodore's policy, and records the expense if it is authorized
                if (boat.spend(amount)) {
                    System.out.printf("Expense authorized, $%.2f spent.%n", boat.getExpenses());
                }
                else {
                    System.out.printf("Expense not permitted, only $%.2f left to spend.%n",
                                      boat.getRemainingBudget());
                }// end of the inner if-else statement
            }
            catch (NumberFormatException exception) {
                // what was typed is not a number: the program goes on with nothing spent
                System.out.println("Invalid amount, nothing spent");
            }
        }// end of the outer if-else statement
    }// end of the requestExpense method

    /**
     * Displays the exit message.
     */
    private static void displayExitMessage() {
        System.out.println();
        System.out.println("Exiting the Fleet Management System");
    }// end of the displayExitMessage method
}// end of the FleetManagement class
