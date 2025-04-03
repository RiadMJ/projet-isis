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
    private int taillePinceau = 4; // Taille par défaut du pinceau
    private JSlider taillePinceauSlider; // Slider pour ajuster la taille

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
                dessinerPoint(e.getPoint());
            }
        });
        zoneDessin.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dessinerLigne(dernierPoint, e.getPoint());
                dernierPoint = e.getPoint();
            }
        });
        add(zoneDessin, BorderLayout.CENTER);

        // Barre d'outils supérieure
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
        btnGomme.addActionListener(e -> {
            gommeActive = true;
            couleurCourante = Color.WHITE;
        });
        barreOutils.add(btnGomme);

        JButton btnEffacer = new JButton("Effacer");
        btnEffacer.addActionListener(e -> zoneDessin.repaint());
        barreOutils.add(btnEffacer);

        // Slider pour la taille du pinceau
        taillePinceauSlider = new JSlider(1, 30, taillePinceau);
        taillePinceauSlider.setMajorTickSpacing(5);
        taillePinceauSlider.setMinorTickSpacing(1);
        taillePinceauSlider.setPaintTicks(true);
        taillePinceauSlider.setPaintLabels(true);
        taillePinceauSlider.addChangeListener(e -> {
            taillePinceau = taillePinceauSlider.getValue();
        });
        
        JLabel tailleLabel = new JLabel("Taille:");
        barreOutils.add(tailleLabel);
        barreOutils.add(taillePinceauSlider);

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

    // Méthodes pour dessiner
    private void dessinerPoint(Point p) {
        Graphics2D g = (Graphics2D)zoneDessin.getGraphics();
        g.setColor(gommeActive ? Color.WHITE : couleurCourante);
        g.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(p.x, p.y, p.x, p.y);
    }

    private void dessinerLigne(Point from, Point to) {
        Graphics2D g = (Graphics2D)zoneDessin.getGraphics();
        g.setColor(gommeActive ? Color.WHITE : couleurCourante);
        g.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(from.x, from.y, to.x, to.y);
    }

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