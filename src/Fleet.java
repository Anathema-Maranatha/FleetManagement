import java.io.Serializable;
import java.util.ArrayList;

/**
 * The fleet of the Coconut Grove Sailing Club: an arbitrary number of boats, kept in the
 * order in which they joined the fleet. The fleet finds its boats by name and reports
 * what they cost to buy and to maintain.
 *
 * @author Maxime Manuel Afonso
 */
public class Fleet implements Serializable {
    private static final long serialVersionUID = 1L; // version of the serialized form
    private static final String REPORT_TITLE = "Fleet report:";
    private static final String REPORT_LINE_START = "\n    "; // every line under the title is indented
    private static final String TOTAL_LABEL = "Total";

    private final ArrayList<Boat> boats;

    /**
     * Creates a fleet with no boats.
     */
    public Fleet() {
        boats = new ArrayList<>();
    }// end of the Fleet constructor

    /**
     * Adds a boat at the end of the fleet.
     *
     * @param boat the boat that joins the fleet
     */
    public void addBoat(Boat boat) {
        boats.add(boat);
    }// end of the addBoat method

    /**
     * Finds the boat that has a given name, without regard for case.
     *
     * @param boatName the name of the boat to find
     * @return the first boat of the fleet with that name, or null if there is no such boat
     */
    public Boat findBoat(String boatName) {
        Boat boatFound = null; // stays null if no boat has that name
        int index = 0;

        // the search stops at the first boat that has the name, or at the end of the fleet
        while (boatFound == null && index < boats.size()) {
            if (boats.get(index).hasName(boatName)) {
                boatFound = boats.get(index);
            }
            index++;
        }// end of the while loop
        return boatFound;
    }// end of the findBoat method

    /**
     * Removes the boat that has a given name, without regard for case.
     *
     * @param boatName the name of the boat to remove
     * @return true if the boat was found and removed, false if there is no such boat
     */
    public boolean removeBoat(String boatName) {
        Boat boat = findBoat(boatName);

        if (boat != null) {
            boats.remove(boat);
        }
        return boat != null;
    }// end of the removeBoat method

    /**
     * Gets the total of the prices paid for the boats of the fleet.
     *
     * @return the total purchase price in dollars
     */
    public double getTotalPurchasePrice() {
        double totalPurchasePrice = 0.0;

        for (Boat boat : boats) {
            totalPurchasePrice += boat.getPurchasePrice();
        }
        return totalPurchasePrice;
    }// end of the getTotalPurchasePrice method

    /**
     * Gets the total of the money spent maintaining the boats of the fleet.
     *
     * @return the total expenses in dollars
     */
    public double getTotalExpenses() {
        double totalExpenses = 0.0;

        for (Boat boat : boats) {
            totalExpenses += boat.getExpenses();
        }
        return totalExpenses;
    }// end of the getTotalExpenses method

    /**
     * Formats the fleet as a report: a title, one line for each boat, and a last line
     * with the total paid for the boats and the total spent on them.
     *
     * @return the fleet report, with its lines separated by new line characters
     */
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder(REPORT_TITLE);

        for (Boat boat : boats) {
            report.append(REPORT_LINE_START).append(boat); // the line comes from the toString of the boat
        }
        report.append(REPORT_LINE_START);
        report.append(Boat.formatReportLine(TOTAL_LABEL, getTotalPurchasePrice(), getTotalExpenses()));

        return report.toString();
    }// end of the toString method
}// end of the Fleet class
