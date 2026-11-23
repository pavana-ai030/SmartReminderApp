
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.toedter.calendar.JDateChooser;
import java.util.Date;


public class SmartReminderApp extends JFrame {

    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField dateField;
    private JTextField timeField;

    private JTable reminderTable;
    private DefaultTableModel tableModel;

    private ArrayList<Reminder> reminders = new ArrayList<>();

    private final String FILE_NAME = "reminders.dat";
    private int editingIndex = -1;
    private JButton editButton;


    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public SmartReminderApp() {

        setTitle("Smart Reminder App");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createInterface();
        loadReminders();
        refreshTable();
        startNotificationChecker();
    }

    private void createInterface() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(246, 244, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel(
                "SMART REMINDER APP", JLabel.CENTER);

        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(new Color(103, 80, 164));

        heading.setBorder(
        BorderFactory.createEmptyBorder(15, 10, 20, 10)
        );

        mainPanel.add(heading, BorderLayout.NORTH);
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        // Input panel
        inputPanel.setBackground(Color.WHITE);

        inputPanel.setBorder(
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );


        titleField = new JTextField();
        descriptionField = new JTextField();
        dateField = new JTextField();
        timeField = new JTextField();
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        titleField.setFont(inputFont);
        descriptionField.setFont(inputFont);
        dateField.setFont(inputFont);
        timeField.setFont(inputFont);
        Color fieldColor = new Color(248, 247, 253);

JTextField[] fields = {
    titleField, descriptionField, dateField, timeField
};

for (JTextField field : fields) {
    field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    field.setBackground(fieldColor);
    field.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(225, 220, 240)
            ),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        )
    );
}

        inputPanel.add(new JLabel("Reminder Title:"));
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
Color labelColor = new Color(65, 55, 85);

for (Component component : inputPanel.getComponents()) {
    if (component instanceof JLabel) {
        component.setFont(labelFont);
        component.setForeground(labelColor);
    }
}

        
inputPanel.add(new JLabel("Date (dd-MM-yyyy):"));

JPanel datePanel = new JPanel(new BorderLayout(5, 0));

JButton calendarButton = new JButton("📅 Select Date");
JButton timeButton = new JButton("🕒 Select Time");
calendarButton.setBackground(new Color(103, 80, 164));
calendarButton.setForeground(Color.WHITE);

timeButton.setBackground(new Color(103, 80, 164));
timeButton.setForeground(Color.WHITE);

calendarButton.setFocusPainted(false);
timeButton.setFocusPainted(false);

calendarButton.setOpaque(true);
timeButton.setOpaque(true);

datePanel.add(dateField, BorderLayout.CENTER);
datePanel.add(calendarButton, BorderLayout.EAST);

calendarButton.addActionListener(e -> {

    JDateChooser chooser = new JDateChooser();
    chooser.setDateFormatString("dd-MM-yyyy");

    int result = JOptionPane.showConfirmDialog(
        this,
        chooser,
        "Select Reminder Date",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result == JOptionPane.OK_OPTION
            && chooser.getDate() != null) {

        Date selectedDate = chooser.getDate();

        dateField.setText(
            new java.text.SimpleDateFormat("dd-MM-yyyy")
                .format(selectedDate)
        );
    }
});

inputPanel.add(datePanel);


        
inputPanel.add(new JLabel("Time (HH:mm):"));

JPanel timePanel = new JPanel(new BorderLayout(5, 0));



timePanel.add(timeField, BorderLayout.CENTER);
timePanel.add(timeButton, BorderLayout.EAST);

timeButton.addActionListener(e -> {

    JSpinner timeSpinner = new JSpinner(
        new SpinnerDateModel()
    );

    JSpinner.DateEditor editor =
        new JSpinner.DateEditor(timeSpinner, "HH:mm");

    timeSpinner.setEditor(editor);

    int result = JOptionPane.showConfirmDialog(
        this,
        timeSpinner,
        "Select Reminder Time",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result == JOptionPane.OK_OPTION) {

        Date selectedTime = (Date) timeSpinner.getValue();

        timeField.setText(
            new java.text.SimpleDateFormat("HH:mm")
                .format(selectedTime)
        );
    }
});

inputPanel.add(timePanel);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "Title", "Description", "Date", "Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reminderTable = new JTable(tableModel);
        reminderTable.setRowHeight(35);

reminderTable.setFont(
    new Font("Segoe UI", Font.PLAIN, 14)
);

reminderTable.setGridColor(new Color(235, 232, 243));
reminderTable.setShowVerticalLines(false);

reminderTable.setSelectionBackground(
    new Color(230, 222, 248)
);

reminderTable.setSelectionForeground(
    new Color(65, 50, 100)
);

reminderTable.getTableHeader().setFont(
    new Font("Segoe UI", Font.BOLD, 14)
);

reminderTable.getTableHeader().setBackground(
    new Color(103, 80, 164)
);

reminderTable.getTableHeader().setForeground(Color.WHITE);
reminderTable.getTableHeader().setOpaque(true);
        reminderTable.setFont(
            new Font("Segoe UI", Font.PLAIN, 14));

        reminderTable.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 14)
        );

        JScrollPane scrollPane = new JScrollPane(reminderTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10));

        JButton addButton = new JButton("Add Reminder");
        editButton = new JButton("Edit Reminder");
        JButton deleteButton = new JButton("Delete Reminder");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        clearButton.setFont(buttonFont);
        addButton.setBackground(new Color(103, 80, 164));
addButton.setForeground(Color.WHITE);

editButton.setBackground(new Color(116, 145, 205));
editButton.setForeground(Color.WHITE);

deleteButton.setBackground(new Color(210, 90, 110));
deleteButton.setForeground(Color.WHITE);

clearButton.setBackground(new Color(125, 125, 140));
clearButton.setForeground(Color.WHITE);

JButton[] buttons = {
    addButton, editButton, deleteButton, clearButton
};

for (JButton button : buttons) {
    button.setFocusPainted(false);
    button.setOpaque(true);
    button.setBorder(
        BorderFactory.createEmptyBorder(10, 18, 10, 18)
    );
}

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addButton.addActionListener(e -> addReminder());
        editButton.addActionListener(e -> editReminder());
        deleteButton.addActionListener(e -> deleteReminder());
        clearButton.addActionListener(e -> clearFields());
    }

    // Add reminder
    private void addReminder() {

        try {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a reminder title.");
                return;
            }

            LocalDateTime dateTime = LocalDateTime.parse(
                    dateField.getText().trim() + " " +
                    timeField.getText().trim(), formatter);

            if (!dateTime.isAfter(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this,
                        "Please select a future date and time.");
                return;
            }

            Reminder reminder = new Reminder(
                    title, description, dateTime);

            reminders.add(reminder);

            saveReminders();
            refreshTable();
            clearFields();

            JOptionPane.showMessageDialog(this,
                    "Reminder added successfully!");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date or time!\n" +
                "Enter date as dd-MM-yyyy\n" +
                "Enter time as HH:mm");
        }
    }

    // Edit reminder
    private void editReminder() {
         if (editingIndex == -1) {

        int row = reminderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reminder to edit.");
            return;
        }

        editingIndex = row;

        Reminder reminder = reminders.get(row);

        titleField.setText(reminder.title);
        descriptionField.setText(reminder.description);

        dateField.setText(
                reminder.dateTime.format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        timeField.setText(
                reminder.dateTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")));

        editButton.setText("Save Changes");

        JOptionPane.showMessageDialog(this,
                "Modify the details and click Save Changes.");

        return;
    }

    // Second click: Save modified details
    try {

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty.");
            return;
        }

        LocalDateTime newDateTime = LocalDateTime.parse(
                dateField.getText().trim() + " " +
                timeField.getText().trim(), formatter);

        if (!newDateTime.isAfter(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                    "Please select a future date and time.");
            return;
        }

        Reminder reminder = reminders.get(editingIndex);

        reminder.title = title;
        reminder.description = description;
        reminder.dateTime = newDateTime;
        reminder.notified = false;

        saveReminders();
        refreshTable();

        editingIndex = -1;
        editButton.setText("Edit Reminder");

        clearFields();

        JOptionPane.showMessageDialog(this,
                "Reminder updated successfully!");

    } catch (DateTimeParseException ex) {

        JOptionPane.showMessageDialog(this,
                "Invalid date or time!");
    }
}
    // Delete reminder
    private void deleteReminder() {

        int row = reminderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a reminder to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this reminder?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            reminders.remove(row);

            saveReminders();
            refreshTable();

            JOptionPane.showMessageDialog(this,
                    "Reminder deleted successfully!");
        }
    }

    // Display reminders
    private void refreshTable() {

        tableModel.setRowCount(0);

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern("HH:mm");

        for (Reminder reminder : reminders) {

            tableModel.addRow(new Object[] {
                    reminder.title,
                    reminder.description,
                    reminder.dateTime.format(dateFormatter),
                    reminder.dateTime.format(timeFormatter)
            });
        }
    }

    // Clear input fields
    private void clearFields() {

        titleField.setText("");
        descriptionField.setText("");
        dateField.setText("");
        timeField.setText("");

        reminderTable.clearSelection();
    }

    // Save reminders to file
    private void saveReminders() {

        try (ObjectOutputStream output =
                new ObjectOutputStream(
                        new FileOutputStream(FILE_NAME))) {

            output.writeObject(reminders);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving reminders!");
        }
    }

    // Load saved reminders
    @SuppressWarnings("unchecked")
    private void loadReminders() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream input =
                new ObjectInputStream(
                        new FileInputStream(file))) {

            reminders = (ArrayList<Reminder>) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load saved reminders.");
        }
    }

    // Check for due reminders
    private void startNotificationChecker() {

        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                LocalDateTime now = LocalDateTime.now();

                for (Reminder reminder : reminders) {

                    if (!reminder.notified &&
                            !reminder.dateTime.isAfter(now)) {

                        reminder.notified = true;
                        saveReminders();

                        SwingUtilities.invokeLater(() -> {

                            JOptionPane.showMessageDialog(
                                    SmartReminderApp.this,
                                    "Reminder: " + reminder.title +
                                    "\n" + reminder.description,
                                    "Reminder Notification",
                                    JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                }

            }

        }, 1000, 10000);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SmartReminderApp app = new SmartReminderApp();
            app.setVisible(true);

        });
    }
}

// Reminder class
class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    String title;
    String description;
    LocalDateTime dateTime;
    boolean notified = false;

    Reminder(String title, String description,
             LocalDateTime dateTime) {

        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }
}
