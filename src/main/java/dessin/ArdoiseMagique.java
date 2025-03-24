package dessin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ArdoiseMagique extends JFrame {

    private Color couleurCourante = Color.BLACK;
    private boolean gommeActive = false;
    private JPanel zoneDessin;
    private Point dernierPoint;
    private boolean niveauFacile = true; // Par défaut, niveau facile

    public ArdoiseMagique() {
        super("Ardoise Magique");
        initGui();
    }

    private void initGui() {
        // Configuration de la fenêtre
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Zone de dessin
        zoneDessin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        zoneDessin.setBackground(Color.WHITE);
        zoneDessin.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dernierPoint = e.getPoint();
            }
        });
        zoneDessin.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g = zoneDessin.getGraphics();
                g.setColor(gommeActive ? Color.WHITE : couleurCourante);
                g.drawLine(dernierPoint.x, dernierPoint.y, e.getX(), e.getY());
                dernierPoint = e.getPoint();
            }
        });
        add(zoneDessin, BorderLayout.CENTER);

        // Barre des menus en haut
        JMenuBar menuBar = new JMenuBar();

        // Menu Activité
        JMenu menuActivite = new JMenu("Activité");
        JMenuItem itemDessin = new JMenuItem("Dessin");
        JMenuItem itemCalcul = new JMenuItem("Calcul");
        JMenuItem itemPendu = new JMenuItem("Pendu");
        menuActivite.add(itemDessin);
        menuActivite.add(itemCalcul);
        menuActivite.add(itemPendu);
        menuBar.add(menuActivite);

        // Menu Niveau
        JMenu menuNiveau = new JMenu("Niveau");
        JMenuItem itemFacile = new JMenuItem("Facile");
        JMenuItem itemDifficile = new JMenuItem("Difficile");

        itemFacile.addActionListener(e -> {
            niveauFacile = true;
            refreshInterface();
        });

        itemDifficile.addActionListener(e -> {
            niveauFacile = false;
            refreshInterface();
        });

        menuNiveau.add(itemFacile);
        menuNiveau.add(itemDifficile);
        menuBar.add(menuNiveau);

        // Menu Administration
        JMenu menuAdmin = new JMenu("Administration");
        JMenuItem itemAdmin = new JMenuItem("Modifier dictionnaire");
        itemAdmin.addActionListener(e -> {
            String password = JOptionPane.showInputDialog(
                this,
                "Entrez le mot de passe administrateur",
                "Accès administration",
                JOptionPane.QUESTION_MESSAGE);
            // Vérifier le mot de passe ici
        });
        menuAdmin.add(itemAdmin);
        menuBar.add(menuAdmin);

        setJMenuBar(menuBar);

        // Barre d'outils pour les couleurs et outils
        JPanel barreOutils = new JPanel();
        barreOutils.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Boutons communs à tous les niveaux
        JButton btnCrayon = new JButton("Crayon");
        btnCrayon.addActionListener(e -> {
            gommeActive = false;
            couleurCourante = Color.BLACK; // Crayon noir par défaut
        });
        barreOutils.add(btnCrayon);

        JButton btnGomme = new JButton("Gomme");
        btnGomme.addActionListener(e -> gommeActive = true);
        barreOutils.add(btnGomme);

        JButton btnEffacer = new JButton("Effacer");
        btnEffacer.addActionListener(e -> zoneDessin.repaint());
        barreOutils.add(btnEffacer);

        if (niveauFacile) {
            // Niveau facile : 3 couleurs de base
            JButton btnRouge = new JButton("Rouge");
            btnRouge.setBackground(Color.RED);
            btnRouge.addActionListener(e -> couleurCourante = Color.RED);
            barreOutils.add(btnRouge);

            JButton btnVert = new JButton("Vert");
            btnVert.setBackground(Color.GREEN);
            btnVert.addActionListener(e -> couleurCourante = Color.GREEN);
            barreOutils.add(btnVert);

            JButton btnBleu = new JButton("Bleu");
            btnBleu.setBackground(Color.BLUE);
            btnBleu.addActionListener(e -> couleurCourante = Color.BLUE);
            barreOutils.add(btnBleu);
        } else {
            // Niveau difficile : Bouton pour ouvrir le sélecteur de couleurs
            JButton btnCouleur = new JButton("Couleur");
            btnCouleur.addActionListener(e -> {
                Color selectedColor = JColorChooser.showDialog(this, "Choisir une couleur", couleurCourante);
                if (selectedColor != null) {
                    couleurCourante = selectedColor; // Mettre à jour la couleur courante
                }
            });
            barreOutils.add(btnCouleur);
        }

        add(barreOutils, BorderLayout.NORTH);

        // Afficher la fenêtre
        setVisible(true);
    }

    private void refreshInterface() {
        // Recréer l'interface pour appliquer les changements de niveau
        getContentPane().removeAll();
        initGui();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArdoiseMagique());
    }
}