package pendu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Pendu extends JPanel {
    private final String[] dictionnaire = {"PROGRAMMATION", "AGRAFEUSE", "ORDINATEUR", "SONIC", "VITESSE",
            "SOURIS", "CALIFORNIE", "EXTENSION", "DEVELOPPEUR", "ALGORITHME"};

    private String motSecret;
    private char[] motAffiche;
    private int erreurs = 0;
    private JLayeredPane layeredPane;
    private BackgroundPanel backgroundPanel;

    private final JLabel motLabel;
    private final JLabel penduLabel;
    private final JPanel clavierPanel;
    private final JButton resetButton;

    // Images du pendu (dans dossier du projet)
    private final String[] penduImages = {
            "/pendu0.png", "/pendu1.png", "/pendu2.png",
            "/pendu3.png", "/pendu4.png", "/pendu5.png",
            "/pendu6.png", "/pendu7.png"
    };

    public Pendu(int niveauChoisi) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 800));

        // Choisir un mot aléatoire
        Random random = new Random();
        motSecret = dictionnaire[random.nextInt(dictionnaire.length)];
        motAffiche = new char[motSecret.length()];
        for (int i = 0; i < motSecret.length(); i++) {
            motAffiche[i] = '_'; // Initialise avec des underscores
        }

        // Label affichant le mot caché
        motLabel = new JLabel(formatMotAffiche(), SwingConstants.CENTER);
        motLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(motLabel, BorderLayout.NORTH);

        // Créer un JLayeredPane pour superposer le fond et l'image du pendu
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // Utiliser un layout null pour positionner les composants manuellement
        layeredPane.setBounds(0, 0, getWidth(), getHeight());
        add(layeredPane, BorderLayout.CENTER);

        // Panneau pour le fond d'écran
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(backgroundPanel, Integer.valueOf(0)); // Ajouter le fond à l'arrière-plan

        // Charger le fond d'écran
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/Sonic.gif")); // Remplacez par le chemin de votre image
        backgroundPanel.setBackgroundImage(backgroundIcon.getImage());

        // Panneau pour l'image du pendu
        penduLabel = new JLabel();
        penduLabel.setBounds(0, 0, getWidth(), getHeight());
        penduLabel.setHorizontalAlignment(SwingConstants.CENTER);
        penduLabel.setVerticalAlignment(SwingConstants.CENTER);
        layeredPane.add(penduLabel, Integer.valueOf(1)); // Ajouter l'image du pendu au premier plan

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

        // Ajouter un ComponentListener pour redimensionner les composants
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layeredPane.setBounds(0, 0, getWidth(), getHeight());
                backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
                penduLabel.setBounds(0, 0, getWidth(), getHeight());
                updatePenduImage(); // Mettre à jour l'image du pendu
            }
        });
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

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Image penduImage;

        public void setBackgroundImage(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            repaint(); // Redessiner le panneau lorsque le fond change
        }

        public void setPenduImage(Image penduImage) {
            this.penduImage = penduImage;
            repaint(); // Redessiner le panneau lorsque l'image du pendu change
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Dessiner le fond d'écran
            if (backgroundImage != null) {
                // Réduire la taille du GIF
                int gifWidth = (int) (getWidth() * 1);
                int gifHeight = (int) (getHeight() * 0.8); 
                g.drawImage(backgroundImage, 0, 10, gifWidth, gifHeight, this);
            }

            // Dessiner l'image du pendu
            if (penduImage != null) {
                int newWidth = (int) (getWidth() * 0.3); 
                int newHeight = (int) (getHeight() * 0.8); 
                int x = (getWidth() - newWidth) / 2; 
                int y = (getHeight() - newHeight) / 2;
                g.drawImage(penduImage, x, y, newWidth, newHeight, this);
            }
        }
    }

    // Met à jour l'image du pendu
    private void updatePenduImage() {
        if (erreurs < penduImages.length) {
            var imgURL = getClass().getResource(penduImages[erreurs]);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image image = originalIcon.getImage();

                backgroundPanel.setPenduImage(image); // Mettre à jour l'image du pendu dans le panneau

                // Calculer la nouvelle taille en fonction de la taille de la fenêtre
                int newWidth = (int) (getWidth() * 0.3); 
                int newHeight = (int) (getHeight() * 0.8);

                // Redimensionner l'image en conservant les proportions
                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                penduLabel.setIcon(new ImageIcon(scaledImage));
                penduLabel.setHorizontalAlignment(SwingConstants.CENTER);
                penduLabel.setVerticalAlignment(SwingConstants.CENTER);
            } else {
                penduLabel.setText("Image introuvable : " + penduImages[erreurs]);
                System.err.println("ERREUR: Impossible de charger " + penduImages[erreurs]);
            }
        }
    }

    // Le jeu est terminé
    private void gameOver(boolean victoire) {
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
                    Thread.sleep(500);
                }

                motLabel.setForeground(originalColor);

                JOptionPane.showMessageDialog(this,
                        victoire ? "Bravo ! Vous avez gagné !\nLe mot était : " + motSecret
                                : "Dommage ! Le mot était : " + motSecret,
                        victoire ? "Victoire" : "Défaite",
                        victoire ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

                // Réinitialiser le jeu après la boîte de dialogue
                SwingUtilities.invokeLater(this::resetGame);

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
    }

    // Génère un nouveau mot et initialise le jeu
    private void resetWord() {
        Random random = new Random();
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionnaire.txt"))) {
            List<String> words = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim().toUpperCase());
                }
            }
            motSecret = words.get(random.nextInt(words.size()));
        } catch (IOException e) {
            motSecret = dictionnaire[random.nextInt(dictionnaire.length)];
        }
        motAffiche = new char[motSecret.length()];
        for (int i = 0; i < motSecret.length(); i++) {
            motAffiche[i] = '_';
        }
    }

    // Formatage du mot affiché (_ _ _ _ _ _)
    private String formatMotAffiche() {
        StringBuilder sb = new StringBuilder();
        for (char c : motAffiche) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }

    public JPanel getPanel() {
        return this;
    }
}
