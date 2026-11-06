
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Converter extends JFrame implements ActionListener {

    private final JTextField amount = new JTextField(10);

    private final JComboBox<String> source =
        new JComboBox<>(new String[]{"INR", "USD", "EUR"});

    private final JComboBox<String> target =
        new JComboBox<>(new String[]{"USD", "INR", "EUR"});

    private final JLabel result = new JLabel("Result will appear here");

    private final JButton frameButton =
        new JButton("Frame Listener");

    private final JButton innerButton =
        new JButton("Inner Class");

    private final JButton anonymousButton =
        new JButton("Anonymous Class");

    private final JButton lambdaButton =
        new JButton("Lambda");

    public Converter() {
        super("Currency Converter");

        setLayout(new FlowLayout());

        add(new JLabel("Amount:"));
        add(amount);

        add(new JLabel("From:"));
        add(source);

        add(new JLabel("To:"));
        add(target);

        add(frameButton);
        add(innerButton);
        add(anonymousButton);
        add(lambdaButton);

        add(result);

        // Style A: Frame implements ActionListener
        frameButton.addActionListener(this);

        // Style B: Named inner class
        innerButton.addActionListener(new ConvertHandler());

        // Style C: Anonymous inner class
        anonymousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }
        });

        // Style D: Lambda expression
        lambdaButton.addActionListener(e -> convert());

        // Pressing Enter in the amount field
        amount.addActionListener(e -> convert());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private static double rate(String currency) {
        if (currency.equals("INR")) return 1.0;
        if (currency.equals("USD")) return 83.0;
        if (currency.equals("EUR")) return 90.0;

        return 1.0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        convert();
    }

    private class ConvertHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            convert();
        }
    }

    private void convert() {
        try {
            double value = Double.parseDouble(
                amount.getText().trim()
            );

            if (value < 0) {
                throw new NumberFormatException();
            }

            String from = (String) source.getSelectedItem();
            String to = (String) target.getSelectedItem();

            double converted =
                value * rate(from) / rate(to);

            String message = String.format(
                "%.2f %s = %.2f %s",
                value, from, converted, to
            );

            result.setText(message);

            JOptionPane.showMessageDialog(
                this,
                message,
                "Conversion Result",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {
            result.setText("Invalid amount");

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid positive amount",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new Converter().setVisible(true)
        );
    }
}
