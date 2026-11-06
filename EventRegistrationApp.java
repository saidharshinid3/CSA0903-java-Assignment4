
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/campus_events";

    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            URL, USER, PASSWORD
        );
    }
}

class EventService {

    void register(String studentId, String studentName,
                  String eventName) throws Exception {

        if (studentId.trim().isEmpty() ||
            studentName.trim().isEmpty() ||
            eventName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "All fields are required"
            );
        }

        String sql =
            "INSERT INTO registrations " +
            "(student_id, student_name, event_name) " +
            "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, studentName);
            ps.setString(3, eventName);

            ps.executeUpdate();
        }
    }

    List<Object[]> getRegistrations(
            String event, int limit, int offset)
            throws SQLException {

        List<Object[]> rows = new ArrayList<>();

        String sql =
            "SELECT id, student_id, student_name, event_name " +
            "FROM registrations " +
            "WHERE (? = 'All' OR event_name = ?) " +
            "ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, event);
            ps.setString(2, event);
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("event_name")
                    });
                }
            }
        }

        return rows;
    }

    void exportRegistrations(String fileName)
            throws Exception {

        String sql =
            "SELECT id, student_id, student_name, event_name " +
            "FROM registrations ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             FileWriter writer = new FileWriter(fileName)) {

            writer.write(
                "ID,Student ID,Student Name,Event\n"
            );

            while (rs.next()) {
                writer.write(
                    rs.getInt("id") + "," +
                    rs.getString("student_id") + "," +
                    rs.getString("student_name") + "," +
                    rs.getString("event_name") + "\n"
                );
            }
        }
    }
}

public class EventRegistrationApp extends JFrame {

    private final JTextField studentId = new JTextField(12);
    private final JTextField studentName = new JTextField(12);

    private final JComboBox<String> eventName =
        new JComboBox<>(new String[]{
            "Coding Competition",
            "Dance",
            "Singing",
            "Sports",
            "Quiz"
        });

    private final JComboBox<String> filter =
        new JComboBox<>(new String[]{
            "All",
            "Coding Competition",
            "Dance",
            "Singing",
            "Sports",
            "Quiz"
        });

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[]{
                "ID", "Student ID",
                "Student Name", "Event"
            }, 0
        );

    private final JTable table = new JTable(model);

    private final EventService service = new EventService();

    private final int pageSize = 20;

    private int currentPage = 0;

    public EventRegistrationApp() {

        setTitle("Campus Event Registration");

        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new FlowLayout());

        form.add(new JLabel("Student ID:"));
        form.add(studentId);

        form.add(new JLabel("Student Name:"));
        form.add(studentName);

        form.add(new JLabel("Event:"));
        form.add(eventName);

        JButton register = new JButton("Register");
        JButton refresh = new JButton("Refresh");
        JButton previous = new JButton("Previous");
        JButton next = new JButton("Next");
        JButton export = new JButton("Export");

        form.add(register);
        form.add(refresh);

        JPanel filterPanel = new JPanel(new FlowLayout());

        filterPanel.add(new JLabel("Filter Event:"));
        filterPanel.add(filter);
        filterPanel.add(previous);
        filterPanel.add(next);
        filterPanel.add(export);

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(form, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        register.addActionListener(e -> registerStudent());

        refresh.addActionListener(e -> {
            currentPage = 0;
            loadRegistrations();
        });

        filter.addActionListener(e -> {
            currentPage = 0;
            loadRegistrations();
        });

        previous.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                loadRegistrations();
            }
        });

        next.addActionListener(e -> {
            currentPage++;
            loadRegistrations();
        });

        export.addActionListener(e -> exportData());

        createMenuBar();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);

        loadRegistrations();
    }

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");

        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem exportItem = new JMenuItem("Export");
        JMenuItem exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(e -> {
            currentPage = 0;
            loadRegistrations();
        });

        exportItem.addActionListener(e -> exportData());

        exitItem.addActionListener(e -> dispose());

        menu.add(refreshItem);
        menu.add(exportItem);
        menu.add(exitItem);

        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    private void registerStudent() {

        String id = studentId.getText().trim();
        String name = studentName.getText().trim();
        String event = (String) eventName.getSelectedItem();

        try {
            service.register(id, name, event);

            JOptionPane.showMessageDialog(
                this,
                "Registration Successful"
            );

            studentId.setText("");
            studentName.setText("");

            currentPage = 0;
            loadRegistrations();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            String message = e.getMessage();

            if (e instanceof SQLException &&
                ((SQLException) e).getErrorCode() == 1062) {
                message = "Student already registered for this event";
            }

            JOptionPane.showMessageDialog(
                this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadRegistrations() {

        String selectedEvent =
            (String) filter.getSelectedItem();

        int offset = currentPage * pageSize;

        model.setRowCount(0);

        new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground()
                    throws Exception {

                return service.getRegistrations(
                    selectedEvent,
                    pageSize,
                    offset
                );
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> rows = get();

                    for (Object[] row : rows) {
                        model.addRow(row);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    JOptionPane.showMessageDialog(
                        EventRegistrationApp.this,
                        "Loading interrupted"
                    );

                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(
                        EventRegistrationApp.this,
                        "Database Error: " +
                        e.getCause().getMessage()
                    );
                }
            }

        }.execute();
    }

    private void exportData() {

        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(
            new java.io.File("registrations.csv")
        );

        if (chooser.showSaveDialog(this) ==
                JFileChooser.APPROVE_OPTION) {

            String path =
                chooser.getSelectedFile().getAbsolutePath();

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground()
                        throws Exception {

                    service.exportRegistrations(path);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();

                        JOptionPane.showMessageDialog(
                            EventRegistrationApp.this,
                            "Export Successful"
                        );

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            EventRegistrationApp.this,
                            "Export Failed: " + e.getMessage()
                        );
                    }
                }

            }.execute();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
            new EventRegistrationApp().setVisible(true)
        );
    }
}
