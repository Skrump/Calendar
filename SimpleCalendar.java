/** HW#4 Solution
 * @author Alejandro Lopez
 * This program runs a calendar GUI application. It utilizes the MVC pattern. 
 */
public class SimpleCalendar {
    
    /**
     * Runs the program
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CalendarModel calendar = new CalendarModel();
        CalendarView frame = new CalendarView(calendar);
        calendar.attach(frame);
    }
    
}
