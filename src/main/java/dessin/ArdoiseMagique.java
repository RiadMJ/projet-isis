package dessin;

import java.awt.*;

import java.awt.event.*;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ArdoiseMagique extends JPanel {

    private Color couleurCourante = Color.BLACK;

    private boolean gommeActive = false;

    private JPanel zoneDessin;

    private Point dernierPoint;

    private boolean niveauFacile;

    private int taillePinceau = 4;

    private JSlider taillePinceauSlider;

    private BufferedImage imageDessin;

    private Graphics2D graphicsDessin;

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

                if (imageDessin != null) {

                    g.drawImage(imageDessin, 0, 0, this);

                }

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

        

        // Gestion du redimensionnement

        zoneDessin.addComponentListener(new ComponentAdapter() {

            @Override

            public void componentResized(ComponentEvent e) {

                initialiserImage();

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

        btnEffacer.addActionListener(e -> effacer());

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

        // Bouton pour enregistrer

        JButton btnEnregistrer = new JButton("Enregistrer");

        btnEnregistrer.addActionListener(e -> enregistrerDessin());

        barreOutils.add(btnEnregistrer);

        add(barreOutils, BorderLayout.NORTH);

        

        // Initialiser l'image après que le composant est visible

        SwingUtilities.invokeLater(this::initialiserImage);

    }

    private void initialiserImage() {

        if (zoneDessin.getWidth() > 0 && zoneDessin.getHeight() > 0) {

            imageDessin = new BufferedImage(

                zoneDessin.getWidth(), 

                zoneDessin.getHeight(), 

                BufferedImage.TYPE_INT_RGB

            );

            graphicsDessin = imageDessin.createGraphics();

            effacer();

        }

    }

    private void dessinerPoint(Point p) {

        if (graphicsDessin == null) return;

        

        graphicsDessin.setColor(gommeActive ? Color.WHITE : couleurCourante);

        graphicsDessin.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        graphicsDessin.drawLine(p.x, p.y, p.x, p.y);

        zoneDessin.repaint();

    }

    private void dessinerLigne(Point from, Point to) {

        if (graphicsDessin == null) return;

        

        graphicsDessin.setColor(gommeActive ? Color.WHITE : couleurCourante);

        graphicsDessin.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        graphicsDessin.drawLine(from.x, from.y, to.x, to.y);

        zoneDessin.repaint();

    }

    private void effacer() {

        if (graphicsDessin != null) {

            graphicsDessin.setColor(Color.WHITE);

            graphicsDessin.fillRect(0, 0, imageDessin.getWidth(), imageDessin.getHeight());

            zoneDessin.repaint();

        }

    }

    private void enregistrerDessin() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Enregistrer le dessin");

        fileChooser.setFileFilter(new FileNameExtensionFilter("Images PNG", "png"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fichier = fileChooser.getSelectedFile();

            if (!fichier.getName().toLowerCase().endsWith(".png")) {

                fichier = new File(fichier.getParentFile(), fichier.getName() + ".png");

            }

            try {

                ImageIO.write(imageDessin, "png", fichier);

                JOptionPane.showMessageDialog(this, "Dessin enregistré avec succès !");

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du dessin.");

            }

        }

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

    

    @Override

    public Dimension getPreferredSize() {

        return new Dimension(800, 600);

    }

}

