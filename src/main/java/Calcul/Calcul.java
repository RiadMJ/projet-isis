// Full updated code of the Calcul class with the quit/reset behavior
package Calcul;

import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Calcul extends JPanel {

    private JLabel calculLabel, scoresLabel;
    private JTextField reponseField;
    private JButton verifierButton, solutionButton, nouveauButton, modeChronoButton, stopChronoButton;
    private int resultat;
    private String operation;
    private int niveau;
    private int bonnesReponses = 0;
    private BackgroundPanel backgroundPanel;
    private JLabel chronoLabel;

    private boolean modeContreLaMontre = false;
    private int scoreChrono = 0;
    private Timer timerChrono;
    private List<Integer> derniersScores = new ArrayList<>();
    private long startTime;
    private int meilleurScore = 0;
    private Thread musicThread;
    private Player mp3Player;
    private boolean jeuActif = true;

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
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public Calcul(int niveau) {
        this.niveau = niveau;
        setLayout(new BorderLayout());

        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        try {
            ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/calcul.gif"));
            backgroundPanel.setBackgroundImage(backgroundIcon.getImage());
        } catch (Exception e) {
            backgroundPanel.setBackground(new Color(240, 240, 240));
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        calculLabel = new JLabel("Calculez : ", JLabel.CENTER);
        calculLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        reponseField = new JTextField(10);
        reponseField.setHorizontalAlignment(JTextField.CENTER);

        verifierButton = new JButton("Vérifier");
        solutionButton = new JButton("Solution");
        nouveauButton = new JButton("Nouveau");
        modeChronoButton = new JButton("Mode Contre la Montre");
        stopChronoButton = new JButton("Stop Chrono");

        chronoLabel = new JLabel("Temps restant: 60s");
        chronoLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));

        styleButton(verifierButton);
        styleButton(solutionButton);
        styleButton(nouveauButton);
        styleButton(modeChronoButton);
        styleButton(stopChronoButton);
        modeChronoButton.setBackground(new Color(220, 20, 60));
        stopChronoButton.setBackground(new Color(105, 105, 105));
        stopChronoButton.setEnabled(false);

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

        JPanel transparentPanel = new JPanel(new BorderLayout());
        transparentPanel.setOpaque(true);
        transparentPanel.setBackground(new Color(255, 255, 255, 125));
        transparentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        transparentPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(transparentPanel);

        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
        add(chronoLabel, BorderLayout.SOUTH);

        initialiserScoresLabel();

        verifierButton.addActionListener(e -> verifierReponse());
        solutionButton.addActionListener(e -> afficherSolution());
        nouveauButton.addActionListener(e -> genererCalcul());
        modeChronoButton.addActionListener(e -> lancerModeChrono());
        stopChronoButton.addActionListener(e -> arreterModeChronoManuellement());
        reponseField.addActionListener(e -> verifierReponse());

        genererCalcul();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                arreterJeu();
            }
        });
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

        if (niveau == 1) {
            num1 = rand.nextInt(10);
            num2 = rand.nextInt(10);
            operation = rand.nextBoolean() ? "+" : "-";
            if (operation.equals("-") && num1 < num2) {
                int temp = num1; num1 = num2; num2 = temp;
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

        resultat = switch (operation) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "*" -> num1 * num2;
            default -> 0;
        };

        calculLabel.setText("Calculez : " + num1 + " " + operation + " " + num2 + " = ?");
        reponseField.setText("");
        reponseField.requestFocus();
    }

    private void verifierReponse() {
        try {
            int reponse = Integer.parseInt(reponseField.getText());
            if (reponse == resultat) {
                if (modeContreLaMontre) {
                    scoreChrono++;
                } else {
                    bonnesReponses++;
                    if (bonnesReponses >= 10) {
                        afficherPopup("BIEN JOUÉ, 10 BONNES RÉPONSES !", "/celebration.gif");
                        bonnesReponses = 0;
                    }
                }
            } else {
                if (!modeContreLaMontre) {
                    bonnesReponses = 0;
                    afficherPopup("Dommage... c'est une mauvaise réponse", "/dommage.gif");
                }
            }
            genererCalcul();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrez un nombre valide.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

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

    private void enregistrerScore(int score) {
        derniersScores.add(score);
        if (derniersScores.size() > 5) {
            derniersScores.remove(0);
        }
        mettreAJourScores();
    }

    private void mettreAJourScores() {
        StringBuilder scoresText = new StringBuilder("<html><b>Meilleurs Scores:</b><br>");
        for (int score : derniersScores) {
            scoresText.append(score).append("<br>");
        }
        scoresText.append("</html>");
        scoresLabel.setText(scoresText.toString());
    }

    private void initialiserScoresLabel() {
        scoresLabel = new JLabel();
        scoresLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        add(scoresLabel, BorderLayout.NORTH);
    }

    private void afficherSolution() {
        JOptionPane.showMessageDialog(this, "La solution est : " + resultat, "Solution", JOptionPane.INFORMATION_MESSAGE);
        genererCalcul();
    }

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

        new Timer(3000, e -> dialog.dispose()).start();
    }

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

    private void stopMusic() {
        if (mp3Player != null) {
            mp3Player.close();
        }
        if (musicThread != null && musicThread.isAlive()) {
            musicThread.interrupt();
        }
    }

    public void arreterJeu() {
        if (timerChrono != null) timerChrono.stop();
        stopMusic();
        scoreChrono = 0;
        modeContreLaMontre = false;
        chronoLabel.setText("Temps restant: 60s");
        stopChronoButton.setEnabled(false);
        jeuActif = false;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
        genererCalcul();
    }

    public JPanel getPanel() {
        return this;
    }
}