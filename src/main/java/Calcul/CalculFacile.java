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
    private String operation;
    private int niveau; // 1 = Facile, 2 = Difficile

    public CalculFacile(int niveau) {
        this.niveau = niveau;
        setLayout(new BorderLayout());

        calculLabel = new JLabel("Calculez : ", JLabel.CENTER);
        calculLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

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

        verifierButton.addActionListener(e -> verifierReponse());
        solutionButton.addActionListener(e -> afficherSolution());
        nouveauButton.addActionListener(e -> genererCalcul());

        genererCalcul();
    }

    private void genererCalcul() {
        Random rand = new Random();
        int num1, num2;

        if (niveau == 1) {  // Niveau Facile
            num1 = rand.nextInt(10);  // 0-9
            num2 = rand.nextInt(10);
            operation = rand.nextBoolean() ? "+" : "-"; // Addition ou soustraction

            // Empêcher les résultats négatifs
            if (operation.equals("-") && num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }

        } else {  // Niveau Difficile
            int op = rand.nextInt(3); // 0 = addition, 1 = soustraction, 2 = multiplication
            operation = (op == 0) ? "+" : (op == 1) ? "-" : "*";

            if (operation.equals("*")) {
                num1 = rand.nextInt(9) + 1; // Multiplication entre 1 et 9
                num2 = rand.nextInt(9) + 1;
            } else {
                num1 = rand.nextInt(990) + 10; // Addition/Soustraction entre 10 et 999
                num2 = rand.nextInt(990) + 10;
            }
        }

        switch (operation) {
            case "+":
                resultat = num1 + num2;
                break;
            case "-":
                resultat = num1 - num2;
                break;
            case "*":
                resultat = num1 * num2;
                break;
        }

        calculLabel.setText("Calculez : " + num1 + " " + operation + " " + num2 + " = ?");
        reponseField.setText("");
        reponseField.requestFocus();
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
        SwingUtilities.invokeLater(() -> {
            // Options possibles pour l'utilisateur
            Object[] options = {"Facile", "Difficile"};

            // Affichage de la boîte de dialogue avec les options
            int choix = JOptionPane.showOptionDialog(
                null, 
                "Choisissez un niveau :", 
                "Sélection du niveau", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]
            );

            // Vérifie la sélection et attribue le bon niveau
            int niveauChoisi = (choix == 0) ? 1 : 2; // 1 = Facile, 2 = Difficile

            // Création de la fenêtre du jeu avec le niveau sélectionné
            JFrame frame = new JFrame("Jeu de Calcul");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 250);
            frame.add(new CalculFacile(niveauChoisi)); // Passe le niveau sélectionné
            frame.setVisible(true);
        });
    }
}
