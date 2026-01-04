import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServiceImpl extends UnicastRemoteObject
        implements CalculatorService {

    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    public double add(double a, double b) {
        return a + b;
    }

    public double sub(double a, double b) {
        return a - b;
    }

    public double mul(double a, double b) {
        return a * b;
    }

    public double div(double a, double b) {
        if (b == 0)
            throw new ArithmeticException("Division by zero");
        return a / b;
    }

    public static void main(String[] args) {
        try {
            CalculatorService service = new CalculatorServiceImpl();
            Naming.rebind("CalculatorService", service);
            System.out.println("RMI Calculator Server running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
