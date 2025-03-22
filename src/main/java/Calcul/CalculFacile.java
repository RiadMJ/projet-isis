/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Calcul;

/**
 *
 * @author mehdi
 */

/**
 *
 * @author mehdi
 */
import javax.swing.*;
import java.awt.*;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CalculFacile extends JPanel {
    private JLabel calculLabel;
    private JTextField reponseField;
    private JButton verifierButton, solutionButton, nouveauButton;

    public CalculFacile() {
        setLayout(new BorderLayout());

        calculLabel = new JLabel("Calculez : ", JLabel.CENTER);
        calculLabel.setFont(new Font("Arial", Font.BOLD, 18));

        reponseField = new JTextField(10);
        reponseField.setHorizontalAlignment(JTextField.CENTER);

        verifierButton = new JButton("Vérifier");
        solutionButton = new JButton("Solution");
        nouveauButton = new JButton("Nouveau");

        JPanel panel = new JPanel();
        panel.add(calculLabel);
        panel.add(reponseField);
        panel.add(verifierButton);
        panel.add(solutionButton);
        panel.add(nouveauButton);

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jeu de Calcul Facile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 250);
        frame.add(new CalculFacile());
        frame.setVisible(true);
    }
}
