
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class FeeService {

    public String makePayment(String regNo, String amountText) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Registration number is required"
            );
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Enter a valid amount"
            );
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero"
            );
        }

        return "Payment successful for " + regNo +
               "\nAmount: Rs " + amount;
    }
}

public class FeeWindow extends JFrame {

    private final JTextField regNo = new JTextField(15);
    private final JTextField amount = new JTextField(15);
    private final JLabel status = new JLabel("Enter payment details");

    private final FeeService service = new FeeService();

    public FeeWindow() {
        setTitle("Student Fee Payment");
        setLayout(new GridLayout(5, 2, 10, 10));

        JButton pay = new JButton("Pay");
        JButton clear = new JButton("Clear");
        JButton receipt = new JButton("Receipt");

        add(new JLabel("Registration Number:"));
        add(regNo);

        add(new JLabel("Amount:"));
        add(amount);

        add(pay);
        add(clear);

        add(receipt);
        add(status);

        pay.addActionListener(e -> makePayment());

        clear.addActionListener(e -> {
            regNo.setText("");
            amount.setText("");
            status.setText("Fields cleared");
        });

        receipt.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                status.getText(),
                "Payment Receipt",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(null);
    }

    private void makePayment() {
        try {
            String message = service.makePayment(
                regNo.getText(),
                amount.getText()
            );

            status.setText("Payment successful");

            JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException e) {
            status.setText("Payment failed");

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new FeeWindow().setVisible(true)
        );
    }
}
