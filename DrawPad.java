
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DrawPad extends Frame {

    private final List<Point> points = new ArrayList<>();

    private final Label status = new Label("Clicks: 0");

    public DrawPad() {
        super("Draw Pad");

        setLayout(new BorderLayout());

        add(status, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());

                status.setText(
                    "Clicks: " + points.size()
                );

                repaint();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(500, 400);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (Point point : points) {
            g.fillOval(
                point.x - 3,
                point.y - 3,
                6,
                6
            );
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
            new DrawPad().setVisible(true)
        );
    }
}
