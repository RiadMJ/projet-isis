package dessin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ArdoiseMagique extends JPanel {
    private Color couleurCourante = Color.BLACK;
    private boolean gommeActive = false;
    private JPanel zoneDessin;
    private Point dernierPoint;
    private boolean niveauFacile;
    private final int TAILLE_GOMME = 20; // Nouvelle constante pour la taille de la gomme
    private final int TAILLE_CRAYON = 4; // Taille normale du crayon

    public ArdoiseMagique(int niveau) {
        this.niveauFacile = (niveau == 1);
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
                Graphics2D g = (Graphics2D)zoneDessin.getGraphics();
                
                if (gommeActive) {
                    // Configuration pour la gomme
                    g.setColor(Color.WHITE);
                    g.setStroke(new BasicStroke(TAILLE_GOMME, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else {
                    // Configuration pour le crayon
                    g.setColor(couleurCourante);
                    g.setStroke(new BasicStroke(TAILLE_CRAYON, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                }
                
                g.drawLine(dernierPoint.x, dernierPoint.y, e.getX(), e.getY());
                dernierPoint = e.getPoint();
            }
        });
        add(zoneDessin, BorderLayout.CENTER);

        // [Le reste du code reste identique...]
        // Barre d'outils pour les couleurs et outils
        JPanel barreOutils = new JPanel();
        barreOutils.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Boutons communs à tous les niveaux
        JButton btnCrayon = new JButton("Crayon");
        btnCrayon.addActionListener(e -> {
            gommeActive = false;
            couleurCourante = Color.BLACK;
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
            btnRouge.addActionListener(e -> {
                gommeActive = false;
                couleurCourante = Color.RED;
            });
            barreOutils.add(btnRouge);

            JButton btnVert = new JButton("Vert");
            btnVert.setBackground(Color.GREEN);
            btnVert.addActionListener(e -> {
                gommeActive = false;
                couleurCourante = Color.GREEN;
            });
            barreOutils.add(btnVert);

            JButton btnBleu = new JButton("Bleu");
            btnBleu.setBackground(Color.BLUE);
            btnBleu.addActionListener(e -> {
                gommeActive = false;
                couleurCourante = Color.BLUE;
            });
            barreOutils.add(btnBleu);
        } else {
            // Niveau difficile : Sélecteur de couleur avancé
            JButton btnCouleur = new JButton("Couleur");
            btnCouleur.addActionListener(e -> {
                Color selectedColor = JColorChooser.showDialog(this, "Choisir une couleur", couleurCourante);
                if (selectedColor != null) {
                    gommeActive = false;
                    couleurCourante = selectedColor;
                }
            });
            barreOutils.add(btnCouleur);
        }

        add(barreOutils, BorderLayout.NORTH);
    }

    // [Les autres méthodes restent inchangées...]
    private void setNiveau(int niveau) {
        this.niveauFacile = (niveau == 1);
        refreshInterface();
    }

    private void refreshInterface() {
        removeAll();
        initGui();
        revalidate();
        repaint();
    }

    public JPanel getPanel() {
        return this;
    }
}