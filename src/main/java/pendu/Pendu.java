package pendu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class Pendu extends JFrame {
    private final String[] dictionnaire = {"PROGRAMMATION", "AGRAFEUSE", "ORDINATEUR", "FONDATEUR", "PENDU",
            "SOURIS", "CALIFORNIE", "EXTENSION", "DEVELOPPEUR", "ALGORITHME"};

    private String motSecret;
    private char[] motAffiche;
    private int erreurs = 0;
    private int tempsRestant;
    private int tempsInitial = 60;
    private final JSpinner tempsSpinner;
    private Timer timer;

    private final JLabel motLabel;
    private final JLabel penduLabel;
    private final JLabel chronoLabel;
    private final JPanel clavierPanel;
    private final JButton resetButton;

    // Images du pendu (dans dossier du projet)
    private final String[] penduImages = {
            "/pendu0.png", "/pendu1.png", "/pendu2.png",
            "/pendu3.png", "/pendu4.png", "/pendu5.png",
            "/pendu6.png", "/pendu7.png"
    };

    public Pendu() {
        setTitle("Jeu du Pendu");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel configuration temps
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        configPanel.add(new JLabel("Temps (secondes):"));
        tempsSpinner = new JSpinner(new SpinnerNumberModel(tempsInitial, 10, 180, 5));
        configPanel.add(tempsSpinner);
        JButton applyTimeButton = new JButton("Appliquer");
        applyTimeButton.addActionListener(e -> {
            tempsInitial = (int) tempsSpinner.getValue();
            resetGame();
        });
        
        configPanel.add(applyTimeButton);
        add(configPanel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new GridLayout(2, 1)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getWidth(), (int)(getHeight() * 0.15)); // 15% de la hauteur
            }
        };
        
        
        // chrono
        chronoLabel = new JLabel("Temps restant : " + tempsRestant + "s", SwingConstants.CENTER);
        chronoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        chronoLabel.setForeground(Color.RED);
        topPanel.add(chronoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Choisir un mot aléatoire
        Random random = new Random();
        motSecret = dictionnaire[random.nextInt(dictionnaire.length)];
        motAffiche = new char[motSecret.length()];
        for (int i = 0; i < motSecret.length(); i++) {
            motAffiche[i] = '_'; // Initialise avec des underscores
        }

        // Label affichant le mot caché
        motLabel = new JLabel(formatMotAffiche(), SwingConstants.CENTER);
        motLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(motLabel, BorderLayout.NORTH);

        // Image du pendu
        penduLabel = new JLabel();
        updatePenduImage();
        add(penduLabel, BorderLayout.CENTER);

         // Panel pour le clavier et le bouton Reset
         JPanel clavierContainer = new JPanel(new BorderLayout());

         // Clavier virtuel (Panel avec boutons de A à Z)
         clavierPanel = new JPanel();
         clavierPanel.setLayout(new GridLayout(3, 9));
         for (char c = 'A'; c <= 'Z'; c++) {
             JButton lettreButton = new JButton(String.valueOf(c));
             lettreButton.setFont(new Font("Arial", Font.BOLD, 16));
             lettreButton.setBackground(new Color(200, 230, 255));
             lettreButton.setFocusPainted(false);
             lettreButton.addActionListener(new LetterButtonListener(lettreButton, c));
             clavierPanel.add(lettreButton);
         }
 
         // Bouton ResetGame
         resetButton = new JButton("Réinitialiser");
         resetButton.setFont(new Font("Arial", Font.BOLD, 16));
         resetButton.setBackground(Color.RED);
         resetButton.setForeground(Color.WHITE);
         resetButton.addActionListener(e -> resetGame());
 
         // Ajout des composants au conteneur du clavier
         clavierContainer.add(clavierPanel, BorderLayout.CENTER);
         clavierContainer.add(resetButton, BorderLayout.SOUTH);
         add(clavierContainer, BorderLayout.SOUTH);

        // Lancer le chrono dès le début
        startTimer();
        setVisible(true);
    }

    // Démarrer le chrono
    private void startTimer() {
        if (timer != null) {
            timer.stop(); // Si un timer existe déjà, on l'arrête avant d'en créer un nouveau
        }
        tempsRestant = tempsInitial;
        timer = new Timer(1000, e -> {
            tempsRestant--;
            chronoLabel.setText("Temps restant : " + tempsRestant + "s");

            if (tempsRestant <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(null, " Temps écoulé ! Vous avez perdu.\nLe mot était : " + motSecret);
                resetGame();
            }
        });
        timer.start();
    }

    // Listener des boutons du clavier
    private class LetterButtonListener implements ActionListener {
        private final JButton button;
        private final char lettre;

        public LetterButtonListener(JButton button, char lettre) {
            this.button = button;
            this.lettre = lettre;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            button.setEnabled(false); // Désactiver le bouton après usage
            boolean trouve = false;

            // Vérifier si la lettre est dans le mot
            for (int i = 0; i < motSecret.length(); i++) {
                if (motSecret.charAt(i) == lettre) {
                    motAffiche[i] = lettre;
                    trouve = true;
                }
            }

            // Mettre à jour l'affichage du mot
            motLabel.setText(formatMotAffiche());

            if (!trouve) {
                button.setBackground(Color.RED);
                erreurs++;
                updatePenduImage();

                if (erreurs >= penduImages.length - 1) {
                    gameOver(false);
                }
            } else {
                button.setBackground(Color.GREEN);
                if (!new String(motAffiche).contains("_")) {
                    gameOver(true);
                }
            }
        }
    }

    // Génère un nouveau mot et initialise le jeu
    private void resetWord() {
        Random random = new Random();
        motSecret = dictionnaire[random.nextInt(dictionnaire.length)];
        motAffiche = new char[motSecret.length()];
        for (int i = 0; i < motSecret.length(); i++) {
            motAffiche[i] = '_';
        }
    }

    // Met à jour l'image du pendu
    private void updatePenduImage() {
        if (erreurs < penduImages.length) {
            var imgURL = getClass().getResource(penduImages[erreurs]);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image image = originalIcon.getImage();
            
                // Calculer la nouvelle taille en fonction de la taille de la fenêtre
                int newWidth = (int)(getWidth() * 0.4); // 60% de la largeur de la fenêtre
                int newHeight = (int)(getHeight() * 0.6); // 40% de la hauteur de la fenêtre
            
                // Redimensionner l'image en conservant les proportions
                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                penduLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                penduLabel.setText("Image introuvable : " + penduImages[erreurs]);
                System.err.println("ERREUR: Impossible de charger " + penduImages[erreurs]);
            }
        }
    }

    // Le jeu est perdu
    private void gameOver(boolean victoire) {
        timer.stop();

        for (Component comp : clavierPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(false);
            }
        }

        new Thread(() -> {
            try {
                Color originalColor = motLabel.getForeground();
                Color flashColor = victoire ? Color.GREEN : Color.RED;

                for (int i = 0; i < 6; i++) {
                    motLabel.setForeground(i % 2 == 0 ? flashColor : originalColor);
                    Thread.sleep(200);
                }

                motLabel.setForeground(originalColor);

                JOptionPane.showMessageDialog(this, 
                    victoire ? "Bravo! Vous avez gagné!\nLe mot était: " + motSecret
                             : "Dommage! Le mot était: " + motSecret,
                    victoire ? "Victoire" : "Défaite",
                    victoire ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    // Réinitialiser le jeu
    private void resetGame() {
        resetWord();
        motLabel.setText(formatMotAffiche());
        erreurs = 0;
        updatePenduImage();

        // Réactiver tous les boutons du clavier
        for (Component comp : clavierPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setEnabled(true);
                button.setBackground(new Color(200, 230, 255)); // Couleur bleue claire par défaut
        }
    }

    // Réinitialiser aussi la couleur du texte
    motLabel.setForeground(Color.BLACK);

        // redémarrer le chrono
        startTimer();
    }


    // Formatage du mot affiché (_ _ _ _ _ _)
    private String formatMotAffiche() {
        StringBuilder sb = new StringBuilder();
        for (char c : motAffiche) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pendu::new);
    }
}
