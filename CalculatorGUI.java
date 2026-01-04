import javax.swing.*;
import java.awt.event.*;
import java.rmi.Naming;
public class CalculatorGUI implements ActionListener {
    JFrame frame;
    JTextField textField;
    JButton[] numButtons = new JButton[10];
    JButton add, sub, mul, div, eq, clr, dot;
    double num1, num2;
    char operator;
    CalculatorService service;
    public CalculatorGUI() {
        try {
            service = (CalculatorService)
                    Naming.lookup("rmi://localhost/CalculatorService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server not running");
            return;
        }
        frame = new JFrame("RMI Calculator");
        frame.setSize(300, 450);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textField = new JTextField();
        textField.setBounds(30, 20, 220, 40);
        textField.setEditable(false);
        frame.add(textField);
        for (int i = 0; i < 10; i++) {
            numButtons[i] = new JButton(String.valueOf(i));
            numButtons[i].addActionListener(this);
        }
        add = new JButton("+");
        sub = new JButton("-");
        mul = new JButton("*");
        div = new JButton("/");
        eq = new JButton("=");
        clr = new JButton("C");
        dot = new JButton(".");
        JButton[] ops = { add, sub, mul, div, eq, clr, dot };
        for (JButton b : ops) b.addActionListener(this);
        int x = 30, y = 80, w = 50, h = 40;
        for (int i = 1; i <= 9; i++) {
            numButtons[i].setBounds(x, y, w, h);
            frame.add(numButtons[i]);
            x += 60;
            if (i % 3 == 0) {
                x = 30;
                y += 50;
            }
        }
        numButtons[0].setBounds(30, y, w, h);
        dot.setBounds(90, y, w, h);
        eq.setBounds(150, y, 100, h);
        add.setBounds(210, 80, w, h);
        sub.setBounds(210, 130, w, h);
        mul.setBounds(210, 180, w, h);
        div.setBounds(210, 280, w, h);
        clr.setBounds(30, 280, 170, h);

        frame.add(numButtons[0]);
        frame.add(dot);
        frame.add(eq);
        frame.add(add);
        frame.add(sub);
        frame.add(mul);
        frame.add(div);
        frame.add(clr);

        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 10; i++)
            if (e.getSource() == numButtons[i])
                textField.setText(textField.getText() + i);

        if (e.getSource() == dot && !textField.getText().contains("."))
            textField.setText(textField.getText() + ".");

        if (e.getSource() == clr)
            textField.setText("");

        if (e.getSource() == add || e.getSource() == sub ||
            e.getSource() == mul || e.getSource() == div) {

            num1 = Double.parseDouble(textField.getText());
            operator = ((JButton) e.getSource()).getText().charAt(0);
            textField.setText("");
        }

        if (e.getSource() == eq) {
            try {
                num2 = Double.parseDouble(textField.getText());
                double result = 0;

                if (operator == '+') result = service.add(num1, num2);
                if (operator == '-') result = service.sub(num1, num2);
                if (operator == '*') result = service.mul(num1, num2);
                if (operator == '/') result = service.div(num1, num2);

                textField.setText(String.valueOf(result));
            } catch (Exception ex) {
                textField.setText("Error");
            }
        }
    }

    public static void main(String[] args) {
        new CalculatorGUI();
    }
}
