import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class creates a calendar using a calendar model. It shows the current month, day, and year. The
 * dates can be changed by clicking on a particular day or using the arrows in the top. The user can also
 * create their own events.
 * @author Alejandro Lopez
 */
public class CalendarView implements ChangeListener{    
    
    private CalendarModel model;
    private final JFrame frame = new JFrame("Calendar");    
    private final JPanel topPanel = new JPanel();
    private final JPanel monthPanel = new JPanel();
    private final JPanel dayPanel = new JPanel();
    private final JButton left = new JButton("<");
    private final JButton right = new JButton(">");
    private final JButton create = new JButton("Create");
    private final JButton quit = new JButton("Quit");
    private ArrayList<JButton> days = new ArrayList<>();
    
    /**
     * Constructor that builds this Calendar. It takes a CalendarModel object and creates a calendar that
     * shows the values and events of the month.
     * @param m model to be shown in this Graphic User Interface
     */
    CalendarView(CalendarModel m)
    {
        model = m;
        model.loadFromFile();
        
        create.setBackground(Color.red);
        create.setForeground(Color.white);
        left.setBackground(Color.white);
        right.setBackground(Color.white);
        quit.setBackground(Color.white);
        topPanel.add(create);
        topPanel.add(left);
        topPanel.add(right);
        topPanel.add(quit);
        
        
        
        create.addActionListener((ActionEvent e) -> {
            makeEvent();
        });
        
        left.addActionListener((ActionEvent e) -> {
            model.prevDay();
        });
        
        right.addActionListener((ActionEvent e) -> {
            model.nextDay();
        });
        
        quit.addActionListener((ActionEvent e) -> {
            model.printToFile();
            System.exit(0);
        });
        
        buildMonthView();
        buildDayView();
        monthPanel.setLayout(new GridLayout(0,7));
        
        frame.add(topPanel, BorderLayout.PAGE_START);
        frame.add(monthPanel, BorderLayout.WEST);
        frame.add(dayPanel, BorderLayout.CENTER);
        frame.setSize(900, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        
    }
    
    /**
     * Builds the month view panel that lists shows the current month and every calendar
     * day in the month. Each individual day can be clicked on to go to that day.
     */
    private void buildMonthView()
    {       
        JLabel monthYear = new JLabel(model.getTitle() + " ", SwingConstants.RIGHT);
        monthPanel.add(monthYear);   
        for (int i = 0; i < 6; i++)
        {
            JLabel skip = new JLabel("");
            JButton k = new JButton();
            monthPanel.add(skip);
        }
        
        JLabel [] days = new JLabel[7];
        days[0] = new JLabel("Su", SwingConstants.CENTER);
        days[1] = new JLabel("Mo", SwingConstants.CENTER);
        days[2] = new JLabel("Tu", SwingConstants.CENTER);
        days[3] = new JLabel("We", SwingConstants.CENTER);
        days[4] = new JLabel("Th", SwingConstants.CENTER);
        days[5] = new JLabel("Fr", SwingConstants.CENTER);
        days[6] = new JLabel("Sa", SwingConstants.CENTER);
        for (int i = 0; i < 7; i++)
        {            
            monthPanel.add(days[i]);
        }        
        
        for(int i = 0; i <= model.getSkippedDays(); i++)
        {
            JLabel skip = new JLabel();
            monthPanel.add(skip);
        }
        for(int i = 0; i < model.getMaxDays(); i++)
        {
            JButton addDate = new JButton(Integer.toString(i+1));
            addDate.addActionListener((ActionEvent e) -> {
                model.setCurrentDay(addDate.getText());
            });
            addDate.setBorderPainted(false);
            if(model.getDay() == i + 1)
                addDate.setForeground(Color.gray);
            else 
                addDate.setBackground(Color.white);
            monthPanel.add(addDate);
        }    
    }
    
    /**
     * Builds the day view panel. This panel shows the current day, the date, and 
     * any events on the given day. 
     */
    public void buildDayView()
    {
        JLabel day = new JLabel(model.getDayDate());      
        ArrayList<Event> events = model.getEvents();
        
        dayPanel.add(day);
        
        for(int i = 0; i < events.size(); i++)
        {
            JTextField eventField = new JTextField(events.get(i).toString());
            eventField.setEditable(false);            
            dayPanel.add(eventField);
        }
        
        if (events.size() == 0)
            dayPanel.add(new JLabel("There are no events on this day."));
        
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.PAGE_AXIS));
    }
    
    /**
     * Creates a JFrame pop-up window that allows the user to enter an event title
     * and start/end times for an event.
     */
    public void makeEvent()
    {
        JFrame makeMe = new JFrame("Create an Event");
        
        JTextField eventName = new JTextField("Untitled event");
        JTextField eventDate = new JTextField(model.getCurrDate());
        JTextField eventStart = new JTextField("Start time");
        JTextField eventEnd = new JTextField("End Time");
        JButton save = new JButton("Save");
        eventName.setPreferredSize(new Dimension(350,30));
        eventDate.setPreferredSize(new Dimension(70,30));
        eventStart.setPreferredSize(new Dimension(70,30));
        eventEnd.setPreferredSize(new Dimension(70,30));
        
        save.addActionListener((ActionEvent e) -> {
            Event someEvent = new Event();
            someEvent.createEvent(eventName.getText(),model.getCurrDate(),
                    eventStart.getText(), eventEnd.getText());
            if(model.add(someEvent) == true)
                {
                    makeMe.dispose();
                }
            else
                handleTimeConflict();                
            
        });
        
        makeMe.setLayout(new FlowLayout());
        makeMe.add(eventName, BorderLayout.PAGE_START);
        makeMe.add(eventDate);
        makeMe.add(eventStart);
        makeMe.add(eventEnd);
        makeMe.add(save);
        
        makeMe.setSize(400, 150);
        makeMe.setVisible(true);
    }
    
    /**
     * Handles any time conflicts when a user enters an event. Brings up a window that notifies
     *  the user of the error.
     */
    public void handleTimeConflict()
    {
        JDialog conflict = new JDialog();
        JOptionPane pane = new JOptionPane();
        pane.showMessageDialog(conflict, "Error: There already exists an event at that time.",
                "Time Conflict Error", JOptionPane.ERROR_MESSAGE);
        pane.setVisible(true);
    }

    /**
     * Repaints the frame when the current day is changed or if an event is added.
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {        
        frame.validate();

        monthPanel.removeAll();
        dayPanel.removeAll();        
        buildMonthView();
        buildDayView();
        
        frame.revalidate();
        frame.repaint();
    }
}
