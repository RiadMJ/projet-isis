// Fait par Riad MALKI JAMAI

package mainPackage; // Déclaration du package auquel appartient cette classe


import java.awt.BorderLayout; // Importation des classes nécessaires pour l'interface graphique
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Calcul.Calcul; // Importation des classes des mini-jeux
import dessin.ArdoiseMagique;
import pendu.Pendu;

public class ProjetJeuxIsis { // Déclaration de la classe principale
    private JFrame frame; // Déclaration de la fenêtre principale
    private JPanel cardPanel; // Panneau pour gérer les différentes vues
    private CardLayout cardLayout; // Layout pour basculer entre les vues
    private boolean isAdmin = false; // Variable pour vérifier si l'utilisateur est administrateur
    private int niveauChoisi = 1; // Niveau de difficulté par défaut
    private ArdoiseMagique ardoiseMagique; // Instance du jeu Ardoise Magique
    private Calcul calculFacile; // Instance du jeu Calcul

    public static void main(String[] args) { // Méthode principale pour lancer l'application
        SwingUtilities.invokeLater(() -> new ProjetJeuxIsis().createAndShowGUI());
    }

    private void createAndShowGUI() { // Méthode pour créer et afficher l'interface graphique
        frame = new JFrame("Menu des Mini-Jeux"); // Création de la fenêtre principale
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fermeture de l'application lorsque la fenêtre est fermée
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiser la fenêtre
        frame.setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel contentPanel = new JPanel(new BorderLayout()); // Panneau principal avec un BorderLayout
        cardLayout = new CardLayout(); // Initialisation du CardLayout
        cardPanel = new JPanel(cardLayout); // Panneau utilisant le CardLayout pour les vues

        // Ajout des panneaux pour chaque mini-jeu
        cardPanel.add(createMainPanel(), "Main"); // Panneau principal
        cardPanel.add(createArdoiseMagiquePanel(), "ArdoiseMagique"); // Panneau pour Ardoise Magique
        cardPanel.add(createCalculPanel(), "Calcul"); // Panneau pour Calcul
        cardPanel.add(createPenduPanel(), "Pendu"); // Panneau pour Pendu

        contentPanel.add(cardPanel, BorderLayout.CENTER); // Ajout du panneau des vues au centre
        frame.setJMenuBar(createMenuBar()); // Création de la barre de menu
        frame.add(contentPanel); // Ajout du panneau principal à la fenêtre

        frame.setVisible(true); // Rendre la fenêtre visible
    }

    private JPanel createMainPanel() { // Création du panneau principal
        JPanel panel = new JPanel(); // Création d'un nouveau panneau
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Layout vertical
        panel.setOpaque(false); // Fond transparent

        JLabel titleLabel = new JLabel(createRainbowText("Bienvenue dans les Mini-Jeux")); // Création du titre
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36)); // Police et taille du texte
        titleLabel.setForeground(Color.BLACK); // Couleur du texte
        titleLabel.setPreferredSize(new Dimension(800, 50)); // Taille préférée
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Alignement horizontal
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage

        panel.add(titleLabel); // Ajout du titre au panneau

        JPanel buttonPanel = new JPanel(); // Panneau pour les boutons
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20)); // Layout en grille
        buttonPanel.setOpaque(false); // Fond transparent
        buttonPanel.setBackground(new Color(255, 228, 225)); // Couleur de fond

        // Création des boutons avec des GIFs
        buttonPanel.add(createButtonWithGif("Ardoise Magique", "/gif_ardoise2.gif", "ArdoiseMagique", "ArdoiseMagique"));
        buttonPanel.add(createButtonWithGif("Calcul", "/gif_calcul2.gif", "Calcul", "Calcul"));
        buttonPanel.add(createButtonWithGif("Pendu", "/gif_pendu2.gif", "Pendu", "Pendu"));
        buttonPanel.add(createButtonWithGif("Quitter", "/gif_quitter.gif", "Quitter", "Quitter"));

        panel.add(buttonPanel); // Ajout du panneau de boutons au panneau principal

        return panel; // Retourne le panneau principal
    }

    private JButton createButtonWithGif(String text, String gifPath, String actionCommand, String cardName) {
        JButton button = new JButton(text); // Création d'un bouton
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 24)); // Police et taille du texte
        button.setForeground(Color.black); // Couleur du texte
        button.setPreferredSize(new Dimension(200, 100)); // Taille préférée
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Curseur de la main
        button.setBorder(new RoundedBorder(25)); // Bord arrondi
        button.setIcon(new ImageIcon(getClass().getResource(gifPath))); // Icône du bouton
        button.setActionCommand(actionCommand); // Commande d'action
        button.setHorizontalTextPosition(SwingConstants.CENTER); // Position horizontale du texte
        button.setVerticalTextPosition(SwingConstants.BOTTOM); // Position verticale du texte
        button.setBorderPainted(false); // Ne pas peindre la bordure

        button.setContentAreaFilled(false); // Ne pas remplir la zone de contenu
        button.setOpaque(true); // Fond opaque

        button.addActionListener(e -> { // Ajout d'un écouteur d'action
            if ("Quitter".equals(text)) { // Si le bouton est "Quitter"
                System.exit(0); // Quitter l'application
            } else { // Sinon
                cardLayout.show(cardPanel, cardName); // Afficher le panneau correspondant
            }
        });

        return button; // Retourne le bouton
    }

    private JPanel createArdoiseMagiquePanel() { // Création du panneau pour Ardoise Magique
        JPanel panel = new JPanel(); // Création d'un nouveau panneau
        panel.setLayout(new BorderLayout()); // Layout BorderLayout

        // Image de fond GIF
        ImageIcon ardoiseBackground = new ImageIcon(getClass().getResource("/pardoise.gif"));
        JLabel backgroundLabel = new JLabel(ardoiseBackground); // Label pour l'image de fond
        panel.add(backgroundLabel, BorderLayout.CENTER); // Ajout du label au centre

        // Créer un panneau pour le texte et le bouton
        JPanel contentPanel = new JPanel(); // Panneau pour le contenu
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Layout vertical
        contentPanel.setOpaque(false); // Fond transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Ardoise Magique - Dessinez ici!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // Police et taille du texte
        label.setForeground(Color.WHITE); // Couleur du texte
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible avant le texte
        contentPanel.add(label); // Ajout du texte au panneau

        // Espace fixe entre le texte et le bouton
        contentPanel.add(Box.createVerticalStrut(20));

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20)); // Police et taille du texte
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Taille préférée
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage

        startGameButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            if (ardoiseMagique == null) { // Si l'instance du jeu n'existe pas
                ardoiseMagique = new ArdoiseMagique(niveauChoisi); // Créer une nouvelle instance
            }
            ardoiseMagique.setNiveau(niveauChoisi); // Mettre à jour le niveau
            JPanel jeuPanel = ardoiseMagique.getPanel(); // Obtenir le panneau du jeu
            cardPanel.add(jeuPanel, "ArdoiseMagiqueGame"); // Ajouter le panneau au CardLayout
            cardLayout.show(cardPanel, "ArdoiseMagiqueGame"); // Afficher le panneau du jeu
        });

        contentPanel.add(startGameButton); // Ajout du bouton au panneau
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout()); // Layout BorderLayout pour le label
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Ajout du panneau de contenu au centre

        return panel; // Retourne le panneau
    }

    private JPanel createCalculPanel() { // Création du panneau pour Calcul
        JPanel panel = new JPanel(); // Création d'un nouveau panneau
        panel.setLayout(new BorderLayout()); // Layout BorderLayout

        // Image de fond GIF
        ImageIcon calculBackground = new ImageIcon(getClass().getResource("/pcalcul.gif"));
        JLabel backgroundLabel = new JLabel(calculBackground); // Label pour l'image de fond
        panel.add(backgroundLabel, BorderLayout.CENTER); // Ajout du label au centre

        // Créer un panneau pour le texte et le bouton
        JPanel contentPanel = new JPanel(); // Panneau pour le contenu
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Layout vertical
        contentPanel.setOpaque(false); // Fond transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Calcul - Résolvez des problèmes!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // Police et taille du texte
        label.setForeground(Color.WHITE); // Couleur du texte
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible avant le texte
        contentPanel.add(label); // Ajout du texte au panneau

        // Espace fixe entre le texte et le bouton
        contentPanel.add(Box.createVerticalStrut(20));

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20)); // Police et taille du texte
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Taille préférée
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage
        startGameButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            if (calculFacile == null) { // Si l'instance du jeu n'existe pas
                calculFacile = new Calcul(niveauChoisi); // Créer une nouvelle instance
            }
            calculFacile.setNiveau(niveauChoisi); // Mettre à jour le niveau
            JPanel jeuPanel = calculFacile.getPanel(); // Obtenir le panneau du jeu
            cardPanel.add(jeuPanel, "CalculFacileGame"); // Ajouter le panneau au CardLayout
            cardLayout.show(cardPanel, "CalculFacileGame"); // Afficher le panneau du jeu
        });

        contentPanel.add(startGameButton); // Ajout du bouton au panneau
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout()); // Layout BorderLayout pour le label
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Ajout du panneau de contenu au centre

        return panel; // Retourne le panneau
    }

    private JPanel createPenduPanel() { // Création du panneau pour Pendu
        JPanel panel = new JPanel(); // Création d'un nouveau panneau
        panel.setLayout(new BorderLayout()); // Layout BorderLayout

        // Image de fond GIF
        ImageIcon penduBackground = new ImageIcon(getClass().getResource("/ppendu.gif"));
        JLabel backgroundLabel = new JLabel(penduBackground); // Label pour l'image de fond
        panel.add(backgroundLabel, BorderLayout.CENTER); // Ajout du label au centre

        // Créer un panneau pour le texte et le bouton
        JPanel contentPanel = new JPanel(); // Panneau pour le contenu
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Layout vertical
        contentPanel.setOpaque(false); // Fond transparent

        // Texte au-dessus du bouton
        JLabel label = new JLabel("Pendu - Devinez les mots!", JLabel.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // Police et taille du texte
        label.setForeground(Color.WHITE); // Couleur du texte
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible avant le texte
        contentPanel.add(label); // Ajout du texte au panneau

        // Espace fixe entre le texte et le bouton
        contentPanel.add(Box.createVerticalStrut(20));

        // Bouton "Démarrer le jeu"
        JButton startGameButton = new JButton("Démarrer le jeu");
        startGameButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20)); // Police et taille du texte
        startGameButton.setPreferredSize(new Dimension(200, 50)); // Taille préférée
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrage
        startGameButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            JPanel jeuPanel = new Pendu(niveauChoisi).getPanel(); // Créer une nouvelle instance du jeu
            cardPanel.add(jeuPanel, "PenduGame"); // Ajouter le panneau au CardLayout
            cardLayout.show(cardPanel, "PenduGame"); // Afficher le panneau du jeu
        });

        contentPanel.add(startGameButton); // Ajout du bouton au panneau
        contentPanel.add(Box.createVerticalGlue()); // Espace flexible après le bouton

        backgroundLabel.setLayout(new BorderLayout()); // Layout BorderLayout pour le label
        backgroundLabel.add(contentPanel, BorderLayout.CENTER); // Ajout du panneau de contenu au centre

        return panel; // Retourne le panneau
    }

    private String createRainbowText(String text) { // Création d'un texte arc-en-ciel
        StringBuilder rainbowText = new StringBuilder("<html>"); // Utilisation de HTML pour le texte coloré
        String[] colors = {
                "#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8B00FF"
        }; // Couleurs de l'arc-en-ciel

        int colorIndex = 0; // Index de la couleur
        for (int i = 0; text != null && i < text.length(); i++) { // Parcourir chaque caractère du texte
            rainbowText.append("<font color=").append(colors[colorIndex]).append(">").append(text.charAt(i)).append("</font>");
            colorIndex = (colorIndex + 1) % colors.length; // Passer à la couleur suivante
        }
        rainbowText.append("</html>"); // Fermer la balise HTML

        return rainbowText.toString(); // Retourne le texte coloré
    }

    private JMenuBar createMenuBar() { // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar(); // Création d'une nouvelle barre de menu

        JMenu menuActivites = new JMenu("Jeux"); // Menu pour les jeux
        JMenuItem menuMain = new JMenuItem("Retour à l'interface des jeux"); // Option pour revenir au menu principal
        JMenuItem menuDessin = new JMenuItem("Ardoise Magique"); // Option pour Ardoise Magique
        JMenuItem menuCalcul = new JMenuItem("Calcul"); // Option pour Calcul
        JMenuItem menuPendu = new JMenuItem("Pendu"); // Option pour Pendu

        menuMain.addActionListener(e -> cardLayout.show(cardPanel, "Main")); // Afficher le panneau principal
        menuDessin.addActionListener(e -> cardLayout.show(cardPanel, "ArdoiseMagique")); // Afficher le panneau Ardoise Magique
        menuCalcul.addActionListener(e -> cardLayout.show(cardPanel, "Calcul")); // Afficher le panneau Calcul
        menuPendu.addActionListener(e -> cardLayout.show(cardPanel, "Pendu")); // Afficher le panneau Pendu

        menuActivites.add(menuMain); // Ajout des options au menu Jeux
        menuActivites.add(menuDessin);
        menuActivites.add(menuCalcul);
        menuActivites.add(menuPendu);

        JMenu menuNiveau = new JMenu("Niveau"); // Menu pour le niveau de difficulté
        JMenuItem niveauFacile = new JMenuItem("Facile"); // Option pour le niveau facile
        JMenuItem niveauDifficile = new JMenuItem("Difficile"); // Option pour le niveau difficile

        niveauFacile.addActionListener(e -> { // Ajout d'un écouteur d'action
            niveauChoisi = 1; // Mettre à jour le niveau
            if (ardoiseMagique != null) { // Si l'instance du jeu existe
                ardoiseMagique.setNiveau(niveauChoisi); // Mettre à jour le niveau
            }
            if (calculFacile != null) { // Si l'instance du jeu existe
                calculFacile.setNiveau(niveauChoisi); // Mettre à jour le niveau
            }
        });
        niveauDifficile.addActionListener(e -> { // Ajout d'un écouteur d'action
            niveauChoisi = 2; // Mettre à jour le niveau
            if (ardoiseMagique != null) { // Si l'instance du jeu existe
                ardoiseMagique.setNiveau(niveauChoisi); // Mettre à jour le niveau
            }
            if (calculFacile != null) { // Si l'instance du jeu existe
                calculFacile.setNiveau(niveauChoisi); // Mettre à jour le niveau
            }
        });

        menuNiveau.add(niveauFacile); // Ajout des options au menu Niveau
        menuNiveau.add(niveauDifficile);

        JMenu menuAdmin = new JMenu("Administration"); // Menu pour l'administration
        JMenuItem menuLogin = new JMenuItem("Se connecter"); // Option pour se connecter
        JMenuItem changePassword = new JMenuItem("Changer mot de passe"); // Option pour changer le mot de passe

        menuLogin.addActionListener(e -> authenticateAdmin()); // Ajout d'un écouteur d'action
        changePassword.addActionListener(e -> changeAdminPassword()); // Ajout d'un écouteur d'action

        menuAdmin.add(menuLogin); // Ajout des options au menu Administration
        menuAdmin.add(changePassword);

        menuBar.add(menuActivites); // Ajout des menus à la barre de menu
        menuBar.add(menuNiveau);
        menuBar.add(menuAdmin);

        return menuBar; // Retourne la barre de menu
    }

    private void changeAdminPassword() { // Changer le mot de passe administrateur
        if (!isAdmin) { // Si l'utilisateur n'est pas administrateur
            JOptionPane.showMessageDialog(frame, "Vous devez être connecté en tant qu'admin");
            return;
        }

        String current = JOptionPane.showInputDialog(frame, "Mot de passe actuel:"); // Demander le mot de passe actuel
        if (current == null || !PasswordManager.verifyPassword(current)) { // Vérifier le mot de passe
            JOptionPane.showMessageDialog(frame, "Mot de passe incorrect");
            return;
        }

        String newPass = JOptionPane.showInputDialog(frame, "Nouveau mot de passe:"); // Demander le nouveau mot de passe
        if (newPass != null && !newPass.isEmpty()) { // Si le nouveau mot de passe n'est pas vide
            PasswordManager.changePassword(newPass); // Changer le mot de passe
            JOptionPane.showMessageDialog(frame, "Mot de passe changé avec succès");
        }
    }

    private void authenticateAdmin() { // Authentification de l'administrateur
        String password = JOptionPane.showInputDialog(frame, "Entrez le mot de passe administrateur:"); // Demander le mot de passe
        if (password != null && PasswordManager.verifyPassword(password)) { // Vérifier le mot de passe
            isAdmin = true; // Mettre à jour le statut administrateur
            JOptionPane.showMessageDialog(frame, "Accès administrateur accordé.");
            addAdminPanel(); // Ajouter le panneau administrateur
        } else { // Si le mot de passe est incorrect
            JOptionPane.showMessageDialog(frame, "Mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAdminPanel() { // Ajouter le panneau administrateur
        for (Component comp : cardPanel.getComponents()) { // Parcourir les composants du panneau
            if (comp.getName() != null && comp.getName().equals("AdminPanel")) { // Si le panneau administrateur existe déjà
                cardLayout.show(cardPanel, "Admin"); // Afficher le panneau administrateur
                return;
            }
        }

        cardPanel.add(createAdminPanel(), "Admin"); // Ajouter le panneau administrateur
        cardLayout.show(cardPanel, "Admin"); // Afficher le panneau administrateur
    }

    private JPanel createAdminPanel() { // Création du panneau administrateur
        JPanel panel = new JPanel(new BorderLayout()); // Création d'un nouveau panneau
        panel.setName("AdminPanel"); // Nom du panneau

        DefaultListModel<String> listModel = new DefaultListModel<>(); // Modèle de liste pour les mots
        JList<String> wordsList = new JList<>(listModel); // Liste pour afficher les mots
        JScrollPane scrollPane = new JScrollPane(wordsList); // Défilement pour la liste

        loadWordsIntoList(listModel); // Charger les mots dans la liste

        JPanel editPanel = new JPanel(new BorderLayout()); // Panneau pour éditer les mots
        JTextField wordField = new JTextField(); // Champ de texte pour entrer un mot
        JButton saveButton = new JButton("Enregistrer"); // Bouton pour enregistrer le mot
        saveButton.setEnabled(false); // Désactiver le bouton par défaut

        wordField.getDocument().addDocumentListener(new DocumentListener() { // Ajout d'un écouteur de document
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            private void update() { // Mettre à jour l'état du bouton
                saveButton.setEnabled(!wordField.getText().trim().isEmpty());
            }
        });

        wordsList.addListSelectionListener(e -> { // Ajout d'un écouteur de sélection
            if (!e.getValueIsAdjusting()) { // Si la sélection est terminée
                String selected = wordsList.getSelectedValue(); // Obtenir le mot sélectionné
                wordField.setText(selected != null ? selected : ""); // Mettre à jour le champ de texte
            }
        });

        saveButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            String newWord = wordField.getText().trim().toUpperCase(); // Obtenir le nouveau mot
            if (!newWord.isEmpty()) { // Si le mot n'est pas vide
                int selectedIndex = wordsList.getSelectedIndex(); // Obtenir l'index sélectionné
                if (selectedIndex != -1) { // Si un mot est sélectionné
                    listModel.set(selectedIndex, newWord); // Mettre à jour le mot
                } else { // Sinon
                    listModel.addElement(newWord); // Ajouter le nouveau mot
                }
                saveWordsToFile(listModel); // Enregistrer les mots dans le fichier
                wordField.setText(""); // Réinitialiser le champ de texte
                wordsList.clearSelection(); // Réinitialiser la sélection
            }
        });

        JButton newButton = new JButton("Nouveau"); // Bouton pour ajouter un nouveau mot
        newButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            wordField.setText(""); // Réinitialiser le champ de texte
            wordsList.clearSelection(); // Réinitialiser la sélection
            wordField.requestFocus(); // Mettre le focus sur le champ de texte
        });

        JButton deleteButton = new JButton("Supprimer"); // Bouton pour supprimer un mot
        deleteButton.addActionListener(e -> { // Ajout d'un écouteur d'action
            int selectedIndex = wordsList.getSelectedIndex(); // Obtenir l'index sélectionné
            if (selectedIndex != -1) { // Si un mot est sélectionné
                listModel.remove(selectedIndex); // Supprimer le mot
                saveWordsToFile(listModel); // Enregistrer les mots dans le fichier
            }
        });

        editPanel.add(wordField, BorderLayout.CENTER); // Ajout du champ de texte au panneau
        JPanel buttonPanel = new JPanel(new FlowLayout()); // Panneau pour les boutons
        buttonPanel.add(saveButton); // Ajout des boutons au panneau
        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        editPanel.add(buttonPanel, BorderLayout.SOUTH); // Ajout du panneau de boutons au panneau d'édition

        panel.add(new JLabel("Gestion des mots du Pendu", JLabel.CENTER), BorderLayout.NORTH); // Ajout du titre au panneau
        panel.add(scrollPane, BorderLayout.CENTER); // Ajout de la liste au panneau
        panel.add(editPanel, BorderLayout.SOUTH); // Ajout du panneau d'édition au panneau

        return panel; // Retourne le panneau administrateur
    }

    private void loadWordsIntoList(DefaultListModel<String> listModel) { // Charger les mots dans la liste
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionnaire.txt"))) { // Lire le fichier de mots
            String line; // Ligne du fichier
            while ((line = reader.readLine()) != null) { // Parcourir chaque ligne
                if (!line.trim().isEmpty()) { // Si la ligne n'est pas vide
                    listModel.addElement(line.trim().toUpperCase()); // Ajouter le mot à la liste
                }
            }
        } catch (IOException e) { // Si le fichier n'existe pas
            String[] defaultWords = {"PROGRAMMATION", "AGRAFEUSE", "ORDINATEUR", "FONDATEUR", "PENDU",
                    "SOURIS", "CALIFORNIE", "EXTENSION", "DEVELOPPEUR", "ALGORITHME"}; // Mots par défaut
            for (String word : defaultWords) { // Parcourir les mots par défaut
                listModel.addElement(word); // Ajouter les mots à la liste
            }
        }
    }

    private void saveWordsToFile(DefaultListModel<String> listModel) { // Enregistrer les mots dans le fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dictionnaire.txt"))) { // Écrire dans le fichier de mots
            for (int i = 0; i < listModel.getSize(); i++) { // Parcourir chaque mot
                writer.write(listModel.getElementAt(i)); // Écrire le mot dans le fichier
                writer.newLine(); // Nouvelle ligne
            }
        } catch (IOException e) { // Si une erreur se produit
            JOptionPane.showMessageDialog(frame, "Erreur lors de la sauvegarde des mots",
                    "Erreur", JOptionPane.ERROR_MESSAGE); // Afficher un message d'erreur
        }
    }

    static class PasswordManager { // Gestionnaire de mot de passe
        private static final String CONFIG_FILE = "config.properties"; // Fichier de configuration
        private static final String DEFAULT_HASH = hashPassword("admin123"); // Mot de passe par défaut

        public static boolean verifyPassword(String input) { // Vérifier le mot de passe
            String storedHash = readHashFromFile(); // Lire le hash du fichier
            return hashPassword(input).equals(storedHash); // Comparer les hash
        }

        public static void changePassword(String newPassword) { // Changer le mot de passe
            writeHashToFile(hashPassword(newPassword)); // Écrire le nouveau hash dans le fichier
        }

        private static String hashPassword(String password) { // Hacher le mot de passe
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Algorithme de hachage
                byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8)); // Hacher le mot de passe
                return Base64.getEncoder().encodeToString(hash); // Encoder le hash en Base64
            } catch (NoSuchAlgorithmException e) { // Si l'algorithme n'existe pas
                throw new RuntimeException("Erreur de hachage", e); // Lancer une exception
            }
        }

        private static String readHashFromFile() { // Lire le hash du fichier
            try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) { // Lire le fichier de configuration
                return reader.readLine(); // Lire la première ligne
            } catch (IOException e) { // Si le fichier n'existe pas
                return DEFAULT_HASH; // Retourner le hash par défaut
            }
        }

        private static void writeHashToFile(String hash) { // Écrire le hash dans le fichier
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) { // Écrire dans le fichier de configuration
                writer.write(hash); // Écrire le hash
            } catch (IOException e) { // Si une erreur se produit
                throw new RuntimeException("Erreur d'écriture du fichier de configuration", e); // Lancer une exception
            }
        }
    }

    class RoundedBorder extends AbstractBorder { // Bordure arrondie
        private int radius; // Rayon de la bordure

        public RoundedBorder(int radius) { // Constructeur
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { // Peindre la bordure
            g.setColor(Color.BLACK); // Couleur de la bordure
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius); // Dessiner un rectangle arrondi
        }

        @Override
        public Insets getBorderInsets(Component c) { // Obtenir les marges de la bordure
            return new Insets(10, 10, 10, 10); // Marges de 10 pixels
        }
    }
}


