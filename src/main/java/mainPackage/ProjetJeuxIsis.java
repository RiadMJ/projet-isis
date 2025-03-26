package mainPackage;

import Calcul.CalculFacile;
import dessin.ArdoiseMagique;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pendu.Pendu;

public class ProjetJeuxIsis {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private boolean isAdmin = false;
    private int niveauChoisi = 1; // Niveau par défaut

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjetJeuxIsis().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Menu des Mini-Jeux");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Fond avec un thème enfantin (nuances pastels)
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 223, 186), getWidth(), getHeight(), new Color(255, 175, 216));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // CardLayout pour afficher les mini-jeux

        // Ajout des panneaux pour chaque mini-jeu
        cardPanel.add(createMainPanel(), "Main");
        cardPanel.add(createArdoiseMagiquePanel(), "ArdoiseMagique");
        cardPanel.add(createCalculPanel(), "Calcul");
        cardPanel.add(createPenduPanel(), "Pendu");

        contentPanel.add(cardPanel, BorderLayout.CENTER);
        frame.setJMenuBar(createMenuBar());
        frame.add(contentPanel);

        frame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Laisse le fond transparent pour permettre au dégradé d'apparaître

        JLabel titleLabel = new JLabel(createRainbowText("Bienvenue dans les Mini-Jeux"));
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setPreferredSize(new Dimension(800, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setOpaque(false);

        buttonPanel.setBackground(new Color(255, 228, 225));

        // Création des 4 boutons avec des couleurs pastel et des formes arrondies
        buttonPanel.add(createButton("Ardoise Magique", new Color(255, 182, 193), new Color(255, 105, 180), "ArdoiseMagique"));
        buttonPanel.add(createButton("Calcul", new Color(173, 216, 230), new Color(135, 206, 250), "Calcul"));
        buttonPanel.add(createButton("Pendu", new Color(255, 255, 204), new Color(255, 255, 153), "Pendu"));
        buttonPanel.add(createButton("Quitter", new Color(255, 235, 205), new Color(255, 182, 193), "Quitter"));

        panel.add(buttonPanel);

        return panel;
    }

    private JButton createButton(String text, Color startColor, Color endColor, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        button.setForeground(Color.black);
        button.setPreferredSize(new Dimension(200, 100));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(25));

        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(startColor); // Applique la couleur initiale

        button.addActionListener(e -> {
            if ("Quitter".equals(text)) {
                System.exit(0);
            } else {
                cardLayout.show(cardPanel, cardName); // Affiche le panneau du mini-jeu correspondant
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(endColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(startColor);
            }
        });

        return button;
    }

    private JPanel createArdoiseMagiquePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridLayout(2, 1));

        JLabel label = new JLabel("Ardoise Magique - Dessinez ici!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 24));

        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        startGameButton.addActionListener(e -> {
            JFrame jeuFrame = new JFrame("Jeu de Dessin");
            jeuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jeuFrame.setSize(800, 600);
            jeuFrame.add(new ArdoiseMagique(niveauChoisi)); // Passe le niveau
            jeuFrame.setVisible(true);
        });

        panel.add(label);
        panel.add(startGameButton);

        return panel;
    }

    private JPanel createCalculPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(new GridLayout(2, 1));

        JLabel label = new JLabel("Calcul - Résolvez des problèmes!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 24));

        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        startGameButton.addActionListener(e -> {
            JFrame jeuFrame = new JFrame("Jeu de Calcul");
            jeuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jeuFrame.setSize(500, 250);
            jeuFrame.add(new CalculFacile(niveauChoisi)); // Passe le bon niveau
            jeuFrame.setVisible(true);
        });

        panel.add(label);
        panel.add(startGameButton);

        return panel;
    }

    private JPanel createPenduPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(204, 255, 204)); // Vert clair
        panel.setLayout(new GridLayout(2, 1));

        JLabel label = new JLabel("Pendu - Devinez les mots!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 24));

        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));

        startGameButton.addActionListener(e -> {
            JFrame jeuFrame = new JFrame("Jeu du Pendu");
            jeuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jeuFrame.setSize(600, 400);
            jeuFrame.add(new Pendu(niveauChoisi)); // Passe le niveau
            jeuFrame.setVisible(true);
        });

        panel.add(label);
        panel.add(startGameButton);

        return panel;
    }

    private String createRainbowText(String text) {
        StringBuilder rainbowText = new StringBuilder("<html>");
        String[] colors = {
                "#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8B00FF"
        };

        int colorIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            rainbowText.append("<font color=").append(colors[colorIndex]).append(">").append(text.charAt(i)).append("</font>");
            colorIndex = (colorIndex + 1) % colors.length;
        }
        rainbowText.append("</html>");

        return rainbowText.toString();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuActivites = new JMenu("Jeux");
        JMenuItem menuMain = new JMenuItem("Retour à l'interface des jeux");
        JMenuItem menuDessin = new JMenuItem("Ardoise Magique");
        JMenuItem menuCalcul = new JMenuItem("Calcul");
        JMenuItem menuPendu = new JMenuItem("Pendu");
        menuMain.addActionListener(e -> cardLayout.show(cardPanel, "Main"));
        menuDessin.addActionListener(e -> cardLayout.show(cardPanel, "ArdoiseMagique"));
        menuCalcul.addActionListener(e -> cardLayout.show(cardPanel, "Calcul"));
        menuPendu.addActionListener(e -> cardLayout.show(cardPanel, "Pendu"));
        menuActivites.add(menuMain);
        menuActivites.add(menuDessin);
        menuActivites.add(menuCalcul);
        menuActivites.add(menuPendu);

        JMenu menuNiveau = new JMenu("Niveau");
        JMenuItem niveauFacile = new JMenuItem("Facile");
        JMenuItem niveauDifficile = new JMenuItem("Difficile");

        niveauFacile.addActionListener(e -> niveauChoisi = 1);
        niveauDifficile.addActionListener(e -> niveauChoisi = 2);

        menuNiveau.add(niveauFacile);
        menuNiveau.add(niveauDifficile);

        JMenu menuAdmin = new JMenu("Administration");
        JMenuItem menuLogin = new JMenuItem("Se connecter");
        menuLogin.addActionListener(e -> authenticateAdmin());
        menuAdmin.add(menuLogin);

        menuBar.add(menuActivites);
        menuBar.add(menuNiveau);
        menuBar.add(menuAdmin);

        return menuBar;
    }

    private void authenticateAdmin() {
        String password = JOptionPane.showInputDialog(frame, "Entrez le mot de passe administrateur:");
        if (password != null && password.equals("admin123")) {
            isAdmin = true;
            JOptionPane.showMessageDialog(frame, "Accès administrateur accordé.");
        } else {
            JOptionPane.showMessageDialog(frame, "Mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classe pour définir une bordure arrondie
    class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}

       
