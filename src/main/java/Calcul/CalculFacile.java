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
    private int bonnesReponses = 0; // Compteur de bonnes réponses
    private BackgroundPanel backgroundPanel;

    // Classe interne pour le fond animé
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public void setBackgroundImage(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Dessine le GIF en arrière-plan (ajusté à la taille du panel)
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public CalculFacile(int niveau) {
        this.niveau = niveau;
        setLayout(new BorderLayout());

        // Configuration du fond animé gif 
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        try {
            ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/calcul.gif"));
            backgroundPanel.setBackgroundImage(backgroundIcon.getImage());
        } catch (Exception e) {
            System.err.println("Erreur de chargement du fond d'écran: " + e.getMessage());
            backgroundPanel.setBackground(new Color(240, 240, 240));
        }

        // Panel pour les composants (transparent)
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        calculLabel = new JLabel("Calculez : ", JLabel.CENTER);
        calculLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        calculLabel.setForeground(Color.WHITE);
        calculLabel.setOpaque(false);

        reponseField = new JTextField(10);
        reponseField.setHorizontalAlignment(JTextField.CENTER);

        verifierButton = new JButton("Vérifier");
        solutionButton = new JButton("Solution");
        nouveauButton = new JButton("Nouveau");

        // Style des boutons
        styleButton(verifierButton);
        styleButton(solutionButton);
        styleButton(nouveauButton);

        // Positionnement des composants
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        contentPanel.add(calculLabel, gbc);

        gbc.gridy = 1;
gbc.gridwidth = 1;
JLabel reponseLabel = new JLabel("Réponse:");
reponseLabel.setForeground(Color.WHITE); // Change la couleur du texte en blanc
contentPanel.add(reponseLabel, gbc);

        gbc.gridx = 1;
        contentPanel.add(reponseField, gbc);

        gbc.gridx = 2;
        contentPanel.add(new JLabel(), gbc); // Espace vide

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(verifierButton, gbc);

        gbc.gridx = 1;
        contentPanel.add(solutionButton, gbc);

        gbc.gridx = 2;
        contentPanel.add(nouveauButton, gbc);

        // Ajout des composants au fond animé
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);

        // Gestion des événements
        verifierButton.addActionListener(e -> verifierReponse());
        solutionButton.addActionListener(e -> afficherSolution());
        nouveauButton.addActionListener(e -> genererCalcul());

        genererCalcul();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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
                bonnesReponses++;
                if (bonnesReponses >= 10) {
                    afficherMessageFelicitation();
                    bonnesReponses = 0; // Réinitialiser le compteur après 10 bonnes réponses
                } else {
                    // Créer une fenêtre de message personnalisée pour "Bonne réponse!"
                    final JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Résultat", false);
                    dialog.setSize(300, 150); // Taille de la fenêtre
                    dialog.setLocationRelativeTo(this); // Centrer la fenêtre

                    // Créer un panneau avec le texte de la bonne réponse
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    JLabel label = new JLabel("Bonne réponse !", JLabel.CENTER);
                    label.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
                    panel.add(label, BorderLayout.CENTER);

                    dialog.add(panel);
                    dialog.setVisible(true);

                    // Timer pour fermer la fenêtre après 2 secondes
                    new Timer(500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose(); // Ferme la fenêtre après 2 secondes
                        }
                    }).start();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Mauvaise réponse !", "Résultat", JOptionPane.ERROR_MESSAGE);
            }
            genererCalcul();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrez un nombre valide.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void afficherSolution() {
        JOptionPane.showMessageDialog(this, "La solution est : " + resultat, "Solution", JOptionPane.INFORMATION_MESSAGE);
        genererCalcul();
    }

    private void afficherMessageFelicitation() {
        // Créer une fenêtre avec le message de félicitations
        JFrame messageFrame = new JFrame("Félicitations !");
        messageFrame.setSize(500, 450);
        messageFrame.setLocationRelativeTo(this); // Centre la fenêtre
        messageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel pour le message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        // Affichage du GIF
        try {
            ImageIcon gifIcon = new ImageIcon(getClass().getResource("/celebration.gif"));
            JLabel gifLabel = new JLabel(gifIcon);
            messagePanel.add(gifLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Texte "Bien joué 10 bonnes réponses"
        JPanel textPanel = new JPanel();
        JLabel textLabel = new JLabel("BIEN JOUÉ, 10 BONNES RÉPONSES !", JLabel.CENTER);
        textPanel.add(textLabel);
        messagePanel.add(textPanel, BorderLayout.SOUTH);

        messageFrame.add(messagePanel);
        messageFrame.setVisible(true);

        // Fermer la fenêtre après 4 secondes
        new Timer(4000, e -> messageFrame.dispose()).start();
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
        genererCalcul();
    }

    public JPanel getPanel() {
        return this;
    }
}
