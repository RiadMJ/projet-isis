package mainPackage;

import Calcul.CalculFacile;
import dessin.ArdoiseMagique;
import pendu.Pendu;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ProjetJeuxIsis {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private boolean isAdmin = false;
    private int niveauChoisi = 1; // Stocke le niveau sélectionné (1 = Facile par défaut)
    private ArdoiseMagique ardoiseMagique;
    private CalculFacile calculFacile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProjetJeuxIsis().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Menu des Mini-Jeux");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiser la fenêtre
        frame.setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel(new BorderLayout());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // CardLayout pour afficher les mini-jeux

        // Ajout des panneaux pour chaque mini-jeu
        cardPanel.add(createMainPanel(), "Main");
        cardPanel.add(createArdoiseMagiquePanel(), "ArdoiseMagique");
        cardPanel.add(createCalculPanel(), "Calcul");
        cardPanel.add(createPenduPanel(), "Pendu");

        contentPanel.add(cardPanel, BorderLayout.CENTER);
        frame.setJMenuBar(createMenuBar()); // Crée la barre de menu et l'ajoute au JFrame
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
        button.setBorder(new RoundedBorder(25)); // Bord arrondi

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
                button.setBackground(endColor); // Change couleur au survol
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(startColor); // Rétablit la couleur initiale
            }
        });

        return button;
    }

    private JPanel createArdoiseMagiquePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Image de fond GIF
        ImageIcon ardoiseBackground = new ImageIcon(getClass().getResource("/pardoise.gif"));
        JLabel backgroundLabel = new JLabel(ardoiseBackground);
        panel.add(backgroundLabel, BorderLayout.CENTER);

        // Créer un panneau pour le texte et le bouton, et le mettre dans un layout avec des éléments centrés
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);  // Laisser ce panneau transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Ardoise Magique - Dessinez ici!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        label.setForeground(Color.WHITE); // Texte en blanc pour contraster avec le fond
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le texte
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible avant le texte
        contentPanel.add(label);

        // Réduire l'espace entre le texte et le bouton en utilisant un petit espace fixe
        contentPanel.add(Box.createVerticalStrut(20)); // Espace fixe plus petit entre le texte et le bouton

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Bouton plus petit
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le bouton

        startGameButton.addActionListener(e -> {
            if (ardoiseMagique == null) {
                ardoiseMagique = new ArdoiseMagique(niveauChoisi);
            }
            ardoiseMagique.setNiveau(niveauChoisi); // Mettre à jour le niveau
            JPanel jeuPanel = ardoiseMagique.getPanel(); // Passe le bon niveau
            cardPanel.add(jeuPanel, "ArdoiseMagiqueGame");
            cardLayout.show(cardPanel, "ArdoiseMagiqueGame");
        });

        contentPanel.add(startGameButton); // Ajouter le bouton au panneau contentPanel
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Placer contentPanel au centre de backgroundLabel

        return panel;
    }

    private JPanel createCalculPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Image de fond GIF
        ImageIcon calculBackground = new ImageIcon(getClass().getResource("/pcalcul.gif"));
        JLabel backgroundLabel = new JLabel(calculBackground);
        panel.add(backgroundLabel, BorderLayout.CENTER);

        // Créer un panneau pour le texte et le bouton, et le mettre dans un layout avec des éléments centrés
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);  // Laisser ce panneau transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Calcul - Résolvez des problèmes!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        label.setForeground(Color.WHITE); // Texte en blanc pour contraster avec le fond
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le texte
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible avant le texte
        contentPanel.add(label);

        // Réduire l'espace entre le texte et le bouton en utilisant un petit espace fixe
        contentPanel.add(Box.createVerticalStrut(20)); // Espace fixe plus petit entre le texte et le bouton

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Bouton plus petit
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le bouton
        startGameButton.addActionListener(e -> {
            if (calculFacile == null) {
                calculFacile = new CalculFacile(niveauChoisi);
            }
            calculFacile.setNiveau(niveauChoisi); // Mettre à jour le niveau
            JPanel jeuPanel = calculFacile.getPanel(); // Passe le bon niveau
            cardPanel.add(jeuPanel, "CalculFacileGame");
            cardLayout.show(cardPanel, "CalculFacileGame");
        });

        contentPanel.add(startGameButton); // Ajouter le bouton au panneau contentPanel
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Placer contentPanel au centre de backgroundLabel

        return panel;
    }

    private JPanel createPenduPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Image de fond GIF
        ImageIcon penduBackground = new ImageIcon(getClass().getResource("/ppendu.gif"));
        JLabel backgroundLabel = new JLabel(penduBackground);
        panel.add(backgroundLabel, BorderLayout.CENTER);

        // Créer un panneau pour le texte et le bouton, et le mettre dans un layout avec des éléments centrés
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);  // Laisser ce panneau transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Pendu - Devinez les mots!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        label.setForeground(Color.WHITE); // Texte en blanc pour contraster avec le fond
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le texte
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible avant le texte
        contentPanel.add(label);

        // Réduire l'espace entre le texte et le bouton en utilisant un petit espace fixe
        contentPanel.add(Box.createVerticalStrut(20)); // Espace fixe plus petit entre le texte et le bouton

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Bouton plus petit
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le bouton
        startGameButton.addActionListener(e -> {
            JPanel jeuPanel = new Pendu(niveauChoisi).getPanel();
            cardPanel.add(jeuPanel, "PenduGame");
            cardLayout.show(cardPanel, "PenduGame");
        });

        contentPanel.add(startGameButton); // Ajouter le bouton au panneau contentPanel
        contentPanel.add(Box.createVerticalGlue()); // Ajouter un espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout());
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Placer contentPanel au centre de backgroundLabel

        return panel;
    }

    private String createRainbowText(String text) {
        StringBuilder rainbowText = new StringBuilder("<html>");
        String[] colors = {
                "#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8B00FF"
        };

        int colorIndex = 0;
        for (int i = 0; text != null && i < text.length(); i++) {
            rainbowText.append("<font color=").append(colors[colorIndex]).append(">").append(text.charAt(i)).append("</font>");
            colorIndex = (colorIndex + 1) % colors.length; // Cycle à travers les couleurs de l'arc-en-ciel
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

        niveauFacile.addActionListener(e -> {
            niveauChoisi = 1;
            if (ardoiseMagique != null) {
                ardoiseMagique.setNiveau(niveauChoisi);
            }
            if (calculFacile != null) {
                calculFacile.setNiveau(niveauChoisi);
            }
        });
        niveauDifficile.addActionListener(e -> {
            niveauChoisi = 2;
            if (ardoiseMagique != null) {
                ardoiseMagique.setNiveau(niveauChoisi);
            }
            if (calculFacile != null) {
                calculFacile.setNiveau(niveauChoisi);
            }
        });

        menuNiveau.add(niveauFacile);
        menuNiveau.add(niveauDifficile);

        JMenu menuAdmin = new JMenu("Administration");
        JMenuItem menuLogin = new JMenuItem("Se connecter");
        JMenuItem changePassword = new JMenuItem("Changer mot de passe");

        menuLogin.addActionListener(e -> authenticateAdmin());
        changePassword.addActionListener(e -> changeAdminPassword());

        menuAdmin.add(menuLogin);
        menuAdmin.add(changePassword);

        menuBar.add(menuActivites);
        menuBar.add(menuNiveau);
        menuBar.add(menuAdmin);

        return menuBar;
    }

    private void changeAdminPassword() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(frame, "Vous devez être connecté en tant qu'admin");
            return;
        }

        String current = JOptionPane.showInputDialog(frame, "Mot de passe actuel:");
        if (current == null || !PasswordManager.verifyPassword(current)) {
            JOptionPane.showMessageDialog(frame, "Mot de passe incorrect");
            return;
        }

        String newPass = JOptionPane.showInputDialog(frame, "Nouveau mot de passe:");
        if (newPass != null && !newPass.isEmpty()) {
            PasswordManager.changePassword(newPass);
            JOptionPane.showMessageDialog(frame, "Mot de passe changé avec succès");
        }
    }

    private void authenticateAdmin() {
        String password = JOptionPane.showInputDialog(frame, "Entrez le mot de passe administrateur:");
        if (password != null && PasswordManager.verifyPassword(password)) {
            isAdmin = true;
            JOptionPane.showMessageDialog(frame, "Accès administrateur accordé.");
            addAdminPanel();
        } else {
            JOptionPane.showMessageDialog(frame, "Mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAdminPanel() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals("AdminPanel")) {
                cardLayout.show(cardPanel, "Admin");
                return;
            }
        }

        cardPanel.add(createAdminPanel(), "Admin");
        cardLayout.show(cardPanel, "Admin");
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("AdminPanel");

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> wordsList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(wordsList);

        loadWordsIntoList(listModel);

        JPanel editPanel = new JPanel(new BorderLayout());
        JTextField wordField = new JTextField();
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setEnabled(false);

        wordField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                saveButton.setEnabled(!wordField.getText().trim().isEmpty());
            }
        });

        wordsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = wordsList.getSelectedValue();
                wordField.setText(selected != null ? selected : "");
            }
        });

        saveButton.addActionListener(e -> {
            String newWord = wordField.getText().trim().toUpperCase();
            if (!newWord.isEmpty()) {
                int selectedIndex = wordsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    listModel.set(selectedIndex, newWord);
                } else {
                    listModel.addElement(newWord);
                }
                saveWordsToFile(listModel);
                wordField.setText("");
                wordsList.clearSelection();
            }
        });

        JButton newButton = new JButton("Nouveau");
        newButton.addActionListener(e -> {
            wordField.setText("");
            wordsList.clearSelection();
            wordField.requestFocus();
        });

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> {
            int selectedIndex = wordsList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
                saveWordsToFile(listModel);
            }
        });

        editPanel.add(wordField, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(new JLabel("Gestion des mots du Pendu", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadWordsIntoList(DefaultListModel<String> listModel) {
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionnaire.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    listModel.addElement(line.trim().toUpperCase());
                }
            }
        } catch (IOException e) {
            String[] defaultWords = {"PROGRAMMATION", "AGRAFEUSE", "ORDINATEUR", "FONDATEUR", "PENDU",
                    "SOURIS", "CALIFORNIE", "EXTENSION", "DEVELOPPEUR", "ALGORITHME"};
            for (String word : defaultWords) {
                listModel.addElement(word);
            }
        }
    }

    private void saveWordsToFile(DefaultListModel<String> listModel) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dictionnaire.txt"))) {
            for (int i = 0; i < listModel.getSize(); i++) {
                writer.write(listModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erreur lors de la sauvegarde des mots",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class PasswordManager {
        private static final String CONFIG_FILE = "config.properties";
        private static final String DEFAULT_HASH = hashPassword("admin123");

        public static boolean verifyPassword(String input) {
            String storedHash = readHashFromFile();
            return hashPassword(input).equals(storedHash);
        }

        public static void changePassword(String newPassword) {
            writeHashToFile(hashPassword(newPassword));
        }

        private static String hashPassword(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erreur de hachage", e);
            }
        }

        private static String readHashFromFile() {
            try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
                return reader.readLine();
            } catch (IOException e) {
                return DEFAULT_HASH;
            }
        }

        private static void writeHashToFile(String hash) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                writer.write(hash);
            } catch (IOException e) {
                throw new RuntimeException("Erreur d'écriture du fichier de configuration", e);
            }
        }
    }

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

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10); // Espacement des bords
        }
    }
}