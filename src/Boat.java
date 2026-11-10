import java.io.Serializable;

/**
 * One boat of the club's fleet: its type, name, year of manufacture, make/model, length
 * and purchase price, and the expenses made maintaining it. A boat applies the
 * Commodore's policy: the club may not spend more on a boat than it cost to buy.
 *
 * @author Maxime Manuel Afonso
 */
public class Boat implements Serializable {
    private static final long serialVersionUID = 1L; // version of the serialized form
    private static final int CENTS_PER_DOLLAR = 100;
    // the type, name, year of manufacture, make/model and length of a boat, in columns
    private static final String DESCRIPTION_FORMAT = "%-7s %-20s %4d %-10s %3d'";
    private static final int DESCRIPTION_WIDTH = 49; // number of characters of a formatted description
    // a description, then amounts of up to 999999.99 dollars: the price is less than a million
    private static final String REPORT_LINE_FORMAT =
            "%-" + DESCRIPTION_WIDTH + "s : Paid $%9.2f : Spent $%9.2f";

    private final BoatType type;
    private final String name;
    private final short yearOfManufacture; // a year fits in a short
    private final String makeAndModel;
    private final byte lengthInFeet; // at most 100 feet, which fits in a byte
    private final double purchasePrice;
    private double expenses;

    /**
     * Creates a boat on which nothing has been spent yet.
     *
     * @param type              the type of the boat, sailing or power
     * @param name              the name of the boat
     * @param yearOfManufacture the year in which the boat was built
     * @param makeAndModel      the make/model of the boat
     * @param lengthInFeet      the length of the boat in feet, up to 100
     * @param purchasePrice     the price paid for the boat in dollars, less than a million
     */
    public Boat(BoatType type, String name, short yearOfManufacture, String makeAndModel,
                byte lengthInFeet, double purchasePrice) {
        this.type = type;
        this.name = name;
        this.yearOfManufacture = yearOfManufacture;
        this.makeAndModel = makeAndModel;
        this.lengthInFeet = lengthInFeet;
        this.purchasePrice = purchasePrice;
        expenses = 0.0;
    }// end of the Boat constructor

    /**
     * Checks whether this boat has a given name, without regard for case.
     *
     * @param otherName the name to compare with the name of this boat
     * @return true if the two names are the same ignoring case, false otherwise
     */
    public boolean hasName(String otherName) {
        return name.equalsIgnoreCase(otherName);
    }// end of the hasName method

    /**
     * Gets the price paid for the boat.
     *
     * @return the purchase price in dollars
     */
    public double getPurchasePrice() {
        return purchasePrice;
    }// end of the getPurchasePrice method

    /**
     * Gets the money spent so far maintaining the boat.
     *
     * @return the expenses in dollars
     */
    public double getExpenses() {
        return expenses;
    }// end of the getExpenses method

    /**
     * Gets the money that may still be spent on the boat under the Commodore's policy:
     * the purchase price less the expenses already made.
     *
     * @return the amount left to spend in dollars, to the cent
     */
    public double getRemainingBudget() {
        return (toCents(purchasePrice) - toCents(expenses)) / (double) CENTS_PER_DOLLAR;
    }// end of the getRemainingBudget method

    /**
     * Requests permission to spend money on the boat. The Commodore's policy is applied:
     * the expense is authorized, and added to the expenses of the boat, only if the total
     * spent on the boat does not become more than its purchase price.
     *
     * @param amount the amount to spend in dollars
     * @return true if the expense is authorized and has been recorded, false if it is not
     *         permitted (the expenses of the boat are then unchanged)
     */
    public boolean spend(double amount) {
        boolean authorized;

        // doubles cannot hold every amount exactly (5456.78 + 43.22 gives 5500.000000000001),
        // so the amounts are compared in whole cents
        authorized = toCents(expenses + amount) <= toCents(purchasePrice);
        if (authorized) {
            expenses += amount;
        }
        return authorized;
    }// end of the spend method

    /**
     * Formats the boat as one line of a fleet report: its type, name, year of manufacture,
     * make/model and length, followed by the price paid for it and the money spent on it.
     *
     * @return the line of the fleet report for this boat
     */
    @Override
    public String toString() {
        String description;

        description = String.format(DESCRIPTION_FORMAT, type, name, yearOfManufacture, makeAndModel,
                                    lengthInFeet);
        return formatReportLine(description, purchasePrice, expenses);
    }// end of the toString method

    /**
     * Formats one line of a fleet report: a description followed by an amount paid and an
     * amount spent. Every line of a report is made here, so that the amounts are in the
     * same columns for a boat and for the totals of a fleet.
     *
     * @param description what the amounts are about: a boat, or the label of a total
     * @param amountPaid  the amount paid in dollars
     * @param amountSpent the amount spent in dollars
     * @return the formatted line
     */
    public static String formatReportLine(String description, double amountPaid, double amountSpent) {
        return String.format(REPORT_LINE_FORMAT, description, amountPaid, amountSpent);
    }// end of the formatReportLine method

    /**
     * Converts an amount in dollars to whole cents, so that amounts can be compared exactly.
     *
     * @param dollars the amount in dollars
     * @return the amount in cents, rounded to the nearest cent
     */
    private static long toCents(double dollars) {
        return Math.round(dollars * CENTS_PER_DOLLAR);
    }// end of the toCents method
}// end of the Boat class
