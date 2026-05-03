import algoritmos.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new SimuladorGUI().setVisible(true);
        });
    }
}