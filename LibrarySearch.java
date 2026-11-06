
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LibrarySearch extends JFrame {

    private final JTextField searchField = new JTextField(20);

    private final JComboBox<String> category =
        new JComboBox<>(new String[]{
            "All", "Java", "Python", "Database", "Networking"
        });

    private final JTextArea results = new JTextArea();

    private final JLabel status =
        new JLabel("Enter a book name and click Search");

    private int currentPage = 1;

    public LibrarySearch() {
        setTitle("Campus Library Search");

        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(category);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        results.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(results);

        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton previous = new JButton("Prev");
        JButton next = new JButton("Next");
        JButton reserve = new JButton("Reserve");

        bottomPanel.add(previous);
        bottomPanel.add(next);
        bottomPanel.add(reserve);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(status, BorderLayout.PAGE_END);

        searchButton.addActionListener(e -> searchBooks());

        searchField.addActionListener(e -> searchBooks());

        previous.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                status.setText("Page " + currentPage);
            }
        });

        next.addActionListener(e -> {
            currentPage++;
            status.setText("Page " + currentPage);
        });

        reserve.addActionListener(e -> {
            if (results.getSelectedText() != null) {
                JOptionPane.showMessageDialog(
                    this, "Book reserved successfully"
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Select a book from the results"
                );
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        String selectedCategory =
            (String) category.getSelectedItem();

        if (keyword.isEmpty()) {
            status.setText("Please enter a search keyword");
            return;
        }

        results.setText(
            "Search Results\n\n" +
            "Keyword: " + keyword +
            "\nCategory: " + selectedCategory +
            "\n\nBook 1: Java Programming\n" +
            "Book 2: Advanced Java\n" +
            "Book 3: Object Oriented Programming"
        );

        currentPage = 1;
        status.setText("Search completed");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new LibrarySearch().setVisible(true)
        );
    }
}
