package dessin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ArdoiseMagique extends JPanel {
    private Color couleurCourante = Color.BLACK;
    private boolean gommeActive = false;
    private JPanel zoneDessin;
    private Point dernierPoint;
    private boolean niveauFacile; // Détermine si le niveau est Facile ou Difficile

    public ArdoiseMagique(int niveau) {
        this.niveauFacile = (niveau == 1); // Si niveau = 1, alors c'est le mode Facile
        initGui();
    }

    private void initGui() {
        setLayout(new BorderLayout());

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

        // Configuration des couleurs selon le niveau
        if (niveauFacile) {
            // Niveau facile : 3 couleurs basiques
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
            // Niveau difficile : Sélecteur de couleur avancé
            JButton btnCouleur = new JButton("Couleur");
            btnCouleur.addActionListener(e -> {
                Color selectedColor = JColorChooser.showDialog(this, "Choisir une couleur", couleurCourante);
                if (selectedColor != null) {
                    couleurCourante = selectedColor;
                }
            });
            barreOutils.add(btnCouleur);
        }

        add(barreOutils, BorderLayout.NORTH);
    }

    // Méthode pour changer de niveau et rafraîchir l'interface
    private void setNiveau(int niveau) {
        this.niveauFacile = (niveau == 1);
        refreshInterface();
    }

    private void refreshInterface() {
        // Met à jour l'interface pour refléter le nouveau niveau sélectionné
        removeAll();
        initGui();
        revalidate();
        repaint();
    }

    // Méthode pour récupérer le JPanel et l'ajouter à une autre interface
    public JPanel getPanel() {
        return this;
    }
}
