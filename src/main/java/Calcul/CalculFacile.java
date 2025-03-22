/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Calcul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CalculFacile extends JPanel {
    private JLabel calculLabel;
    private JTextField reponseField;
    private JButton verifierButton, solutionButton, nouveauButton;
    private int resultat;

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

        verifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifierReponse();
            }
        });

        solutionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherSolution();
            }
        });

        nouveauButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genererCalcul();
            }
        });

        genererCalcul();
    }

    private void genererCalcul() {
        Random rand = new Random();
        int num1 = rand.nextInt(10);  // 0-9
        int num2 = rand.nextInt(10);  // 0-9
        calculLabel.setText("Calculez : " + num1 + " + " + num2 + " = ?");
        resultat = num1 + num2;
    }

    private void verifierReponse() {
        try {
            int reponse = Integer.parseInt(reponseField.getText());
            if (reponse == resultat) {
                JOptionPane.showMessageDialog(this, "Bonne réponse !");
            } else {
                JOptionPane.showMessageDialog(this, "Mauvaise réponse !");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrez un nombre valide.");
        }
    }

    private void afficherSolution() {
        JOptionPane.showMessageDialog(this, "La solution est : " + resultat);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jeu de Calcul Facile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 250);
        frame.add(new CalculFacile());
        frame.setVisible(true);
    }
}
