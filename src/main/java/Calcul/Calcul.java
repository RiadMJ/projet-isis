// Code créé par Bessibes Mehdi
package Calcul;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Calcul extends JPanel {

    // Labels pour afficher le calcul, les scores et le chronomètre
    private JLabel calculLabel, scoresLabel, chronoLabel;

    // Champ de texte pour la réponse de l'utilisateur
    private JTextField reponseField;

    // Boutons pour vérifier la réponse, afficher la solution, générer un nouveau calcul, et démarrer/arrêter le mode chrono
    private JButton verifierButton, solutionButton, nouveauButton, modeChronoButton, stopChronoButton;

    // Variables pour stocker le résultat du calcul, l'opération, le niveau de difficulté, et le nombre de bonnes réponses
    private int resultat;
    private String operation;
    private int niveau;
    private int bonnesReponses = 0;

    // Panneau pour l'image de fond
    private BackgroundPanel backgroundPanel;

    // Variables pour le mode chrono
    private boolean modeContreLaMontre = false;
    private int scoreChrono = 0;
    private Timer timerChrono;
    private List<Integer> derniersScores = new ArrayList<>();
    private long startTime;
    private int meilleurScore = 0;

    // Variables pour la musique
    private Thread musicThread;
    private Player mp3Player;

    // Variable pour indiquer si le jeu est actif
    private boolean jeuActif = true;

    // Classe interne pour gérer l'image de fond
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public void setBackgroundImage(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            repaint(); // Redessiner le panneau pour afficher la nouvelle image
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Constructeur de la classe Calcul
    public Calcul(int niveau) {
        this.niveau = niveau;
        setLayout(new BorderLayout());

        // Initialisation du panneau de fond
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        try {
            // Chargement de l'image de fond depuis les ressources
            ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/calcul.gif"));
            backgroundPanel.setBackgroundImage(backgroundIcon.getImage());
        } catch (Exception e) {
            // Si l'image ne peut pas être chargée, définir une couleur de fond par défaut
            backgroundPanel.setBackground(new Color(240, 240, 240));
        }

        // Création du panneau de contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false); // Rendre le panneau transparent
        contentPanel.setLayout(new GridBagLayout()); // Utiliser GridBagLayout pour une disposition flexible
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Définir les marges

        // Initialisation des labels et des champs de texte
        calculLabel = new JLabel("Calculez : ", JLabel.CENTER);
        calculLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        reponseField = new JTextField(10);
        reponseField.setHorizontalAlignment(JTextField.CENTER);

        // Initialisation des boutons
        verifierButton = new JButton("Vérifier");
        solutionButton = new JButton("Solution");
        nouveauButton = new JButton("Nouveau");
        modeChronoButton = new JButton("Mode Contre la Montre");
        stopChronoButton = new JButton("Stop Chrono");

        chronoLabel = new JLabel("Temps restant: 60s");
        chronoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        // Application du style aux boutons
        styleButton(verifierButton);
        styleButton(solutionButton);
        styleButton(nouveauButton);
        styleButton(modeChronoButton);
        styleButton(stopChronoButton);
        modeChronoButton.setBackground(new Color(220, 20, 60)); // Couleur rouge pour le bouton mode chrono
        stopChronoButton.setBackground(new Color(105, 105, 105)); // Couleur grise pour le bouton stop chrono
        stopChronoButton.setEnabled(false); // Désactiver le bouton stop chrono au début

        // Ajout des composants au panneau de contenu
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        contentPanel.add(calculLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Réponse:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(reponseField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(verifierButton, gbc);
        gbc.gridx = 1;
        contentPanel.add(solutionButton, gbc);
        gbc.gridx = 2;
        contentPanel.add(nouveauButton, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        contentPanel.add(modeChronoButton, gbc);

        gbc.gridy = 4;
        contentPanel.add(stopChronoButton, gbc);

        // Création d'un panneau transparent pour le contenu
        JPanel transparentPanel = new JPanel(new BorderLayout());
        transparentPanel.setOpaque(true);
        transparentPanel.setBackground(new Color(255, 255, 255, 125)); // Blanc avec une transparence de 125
        transparentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marges
        transparentPanel.add(contentPanel, BorderLayout.CENTER);

        // Création d'un panneau d'encapsulation pour le panneau transparent
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(transparentPanel);

        // Ajout des panneaux au panneau de fond et à la fenêtre principale
        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
        add(chronoLabel, BorderLayout.SOUTH);

        // Initialisation du label des scores
        initialiserScoresLabel();

        // Ajout des écouteurs d'événements aux boutons et au champ de texte
        verifierButton.addActionListener(e -> verifierReponse());
        solutionButton.addActionListener(e -> afficherSolution());
        nouveauButton.addActionListener(e -> genererCalcul());
        modeChronoButton.addActionListener(e -> lancerModeChrono());
        stopChronoButton.addActionListener(e -> arreterModeChronoManuellement());
        reponseField.addActionListener(e -> verifierReponse());

        // Génération du premier calcul
        genererCalcul();

        // Ajout d'un écouteur d'événements pour détecter quand le panneau est caché
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                arreterJeu();
            }
        });
    }

    // Méthode pour appliquer un style uniforme aux boutons
    private void styleButton(JButton button) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    // Méthode pour générer un nouveau calcul
    private void genererCalcul() {
        Random rand = new Random();
        int num1, num2;

        // Générer les nombres et l'opération en fonction du niveau de difficulté
        if (niveau == 1) {
            num1 = rand.nextInt(10);
            num2 = rand.nextInt(10);
            operation = rand.nextBoolean() ? "+" : "-";
            if (operation.equals("-") && num1 < num2) {
                int temp = num1; num1 = num2; num2 = temp; // Assurer que le résultat est positif
            }
        } else {
            int op = rand.nextInt(3);
            operation = (op == 0) ? "+" : (op == 1) ? "-" : "*";
            if (operation.equals("*")) {
                num1 = rand.nextInt(9) + 1;
                num2 = rand.nextInt(9) + 1;
            } else {
                num1 = rand.nextInt(990) + 10;
                num2 = rand.nextInt(990) + 10;
            }
        }

        // Calculer le résultat en fonction de l'opération
        resultat = switch (operation) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            default -> 0;
        };

        // Mettre à jour le label du calcul et réinitialiser le champ de texte
        calculLabel.setText("Calculez : " + num1 + " " + operation + " " + num2 + " = ?");
        reponseField.setText("");
        reponseField.requestFocus(); // Mettre le focus sur le champ de texte
    }

    // Méthode pour vérifier la réponse de l'utilisateur
    private void verifierReponse() {
        try {
            int reponse = Integer.parseInt(reponseField.getText());
            if (reponse == resultat) {
                if (modeContreLaMontre) {
                    scoreChrono++; // Incrémenter le score en mode chrono
                } else {
                    bonnesReponses++; // Incrémenter le nombre de bonnes réponses
                    if (bonnesReponses >= 10) {
                        afficherPopup("BIEN JOUÉ, 10 BONNES RÉPONSES !", "/celebration.gif"); // Afficher un message après 10 bonnes réponses
                        bonnesReponses = 0;
                    }
                }
            } else {
                if (!modeContreLaMontre) {
                    bonnesReponses = 0; // Réinitialiser les bonnes réponses en cas d'erreur
                    afficherPopup("Dommage... c'est une mauvaise réponse", "/dommage.gif");
                }
            }
            genererCalcul(); // Générer un nouveau calcul après chaque réponse
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrez un nombre valide.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Méthode pour lancer le mode chrono
    private void lancerModeChrono() {
        jeuActif = true;
        modeContreLaMontre = true;
        scoreChrono = 0;
        startTime = System.currentTimeMillis();
        stopChronoButton.setEnabled(true);
        playMusic();

        JOptionPane.showMessageDialog(this, "Mode Contre la Montre activé ! Vous avez 1 minute.", "Chrono", JOptionPane.INFORMATION_MESSAGE);

        if (timerChrono != null) timerChrono.stop();
        timerChrono = new Timer(1000, e -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            long remainingTime = 60000 - elapsedTime;
            chronoLabel.setText("Temps restant: " + (remainingTime / 1000) + "s");

            if (remainingTime <= 0) {
                stopChronoButton.setEnabled(false);
                modeContreLaMontre = false;
                enregistrerScore(scoreChrono);
                stopMusic();
                if (scoreChrono > meilleurScore) {
                    meilleurScore = scoreChrono;
                    afficherPopup("NOUVEAU RECORD !", "/nouveau_record.gif");
                } else {
                    JOptionPane.showMessageDialog(this, "Temps écoulé ! Score : " + scoreChrono);
                }
                timerChrono.stop();
            }
        });
        timerChrono.start();
        genererCalcul();
    }

    // Méthode pour arrêter manuellement le mode chrono
    private void arreterModeChronoManuellement() {
        if (timerChrono != null) timerChrono.stop();
        stopMusic();
        modeContreLaMontre = false;
        stopChronoButton.setEnabled(false);
        enregistrerScore(scoreChrono);
        if (scoreChrono > meilleurScore) {
            meilleurScore = scoreChrono;
            afficherPopup("NOUVEAU RECORD !", "/nouveau_record.gif");
        } else {
            JOptionPane.showMessageDialog(this, "Chrono arrêté. Score : " + scoreChrono);
        }
    }

    // Méthode pour enregistrer le score et mettre à jour les meilleurs scores
    private void enregistrerScore(int score) {
        derniersScores.add(score);
        if (derniersScores.size() > 5) {
            derniersScores.remove(0); // Garder seulement les 5 derniers scores
        }
        mettreAJourScores();
    }

    // Méthode pour mettre à jour le label des meilleurs scores
    private void mettreAJourScores() {
        StringBuilder scoresText = new StringBuilder("<html><b>Meilleurs Scores:</b><br>");
        for (int score : derniersScores) {
            scoresText.append(score).append("<br>");
        }
        scoresText.append("</html>");
        scoresLabel.setText(scoresText.toString());
    }

    // Méthode pour initialiser le label des scores
    private void initialiserScoresLabel() {
        scoresLabel = new JLabel();
        scoresLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        add(scoresLabel, BorderLayout.NORTH);
    }

    // Méthode pour afficher la solution du calcul
    private void afficherSolution() {
        JOptionPane.showMessageDialog(this, "La solution est : " + resultat, "Solution", JOptionPane.INFORMATION_MESSAGE);
        genererCalcul();
    }

    // Méthode pour afficher une fenêtre pop-up avec un message et un GIF
    private void afficherPopup(String message, String gifPath) {
        if (!jeuActif) return;
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Info", false);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        if (gifPath != null) {
            try {
                JLabel gifLabel = new JLabel(new ImageIcon(getClass().getResource(gifPath)));
                panel.add(gifLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        panel.add(label, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        new Timer(3000, e -> dialog.dispose()).start(); // Fermer la pop-up après 3 secondes
    }

    // Méthode pour jouer de la musique
    private void playMusic() {
        musicThread = new Thread(() -> {
            try {
                InputStream fis = getClass().getResourceAsStream("/chrono_music.mp3");
                BufferedInputStream bis = new BufferedInputStream(fis);
                mp3Player = new Player(bis);
                mp3Player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        musicThread.start();
    }

    // Méthode pour arrêter la musique
    private void stopMusic() {
        if (mp3Player != null) {
            mp3Player.close();
        }
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();
        }
    }

    // Méthode pour arrêter le jeu et réinitialiser les variables
    public void arreterJeu() {
        if (timerChrono != null) timerChrono.stop();
        stopMusic();
        scoreChrono = 0;
        modeContreLaMontre = false;
        chronoLabel.setText("Temps restant: 60s");
        stopChronoButton.setEnabled(false);
        jeuActif = false;
    }

    // Méthode pour définir le niveau de difficulté
    public void setNiveau(int niveau) {
        this.niveau = niveau;
        genererCalcul();
    }

    // Méthode pour obtenir le panneau principal
    public JPanel getPanel() {
        return this;
    }
}