//fait par Lina Ouazzani
package dessin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage; // Pour pouvoir dessiner dans une image qu'on garde en mémoire.
import java.io.File; // Pour pouvoir manipuler des fichiers (comme enregistrer).
import java.io.IOException; // Pour savoir s'il y a eu un problème lors de l'enregistrement.
import javax.imageio.ImageIO; // Pour pouvoir lire et écrire des images (comme le format PNG).
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import javax.swing.filechooser.FileNameExtensionFilter; // Pour choisir seulement les fichiers PNG quand on enregistre.

public class ArdoiseMagique extends JPanel {// Ma classe ArdoiseMagique, c'est un type de "panneau" où je vais dessiner.

    // Couleur du pinceau
    private Color couleurCourante = Color.BLACK;
    
    // Indique si la gomme est active
    private boolean gommeActive = false;
    
    // Zone où on va dessiner
    private JPanel zoneDessin;
    
    // Mémorise la position de la souris pour dessiner des lignes
    private Point dernierPoint;
    
    // Niveau facile ou non (true = facile)
    private boolean niveauFacile;
    
    // Taille actuelle du pinceau
    private int taillePinceau = 4;
    
    // Slider pour changer la taille du pinceau
    private JSlider taillePinceauSlider;
    
    // Image sur laquelle on dessine
    private BufferedImage imageDessin;
    
    // Outil pour dessiner sur l'image
    private Graphics2D graphicsDessin;

    // Constructeur avec paramètre de niveau
    public ArdoiseMagique(int niveau) {
        this.niveauFacile = (niveau == 1); // Si niveau == 1, c’est le mode facile
        initGui(); // Initialise toute l'interface
    }

    // Méthode qui construit l'interface graphique
    private void initGui() {
        setLayout(new BorderLayout());

        // Création de la zone de dessin
        zoneDessin = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Redessine l'image actuelle si elle existe
                if (imageDessin != null) {
                    g.drawImage(imageDessin, 0, 0, this);
                }
            }
        };

        // Fond blanc pour la zone de dessin
        zoneDessin.setBackground(Color.WHITE);

        // Quand on clique avec la souris
        zoneDessin.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dernierPoint = e.getPoint();
                dessinerPoint(e.getPoint()); // On dessine un point à l’endroit cliqué
            }
        });

        // Quand on déplace la souris en maintenant cliqué
        zoneDessin.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dessinerLigne(dernierPoint, e.getPoint()); // Dessine une ligne entre l'ancien et le nouveau point
                dernierPoint = e.getPoint(); // Met à jour le dernier point
            }
        });

        // Quand la zone change de taille, on réinitialise l'image
        zoneDessin.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                initialiserImage();
            }
        });
        add(zoneDessin, BorderLayout.CENTER);

        // Barre en haut avec les outils
        JPanel barreOutils = new JPanel();
        barreOutils.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Crayon (dessin en noir)
        JButton btnCrayon = new JButton("Crayon");
        btnCrayon.addActionListener(e -> {
            gommeActive = false;
            couleurCourante = Color.BLACK;
        });
        barreOutils.add(btnCrayon);

        // Gomme (dessin en blanc)
        JButton btnGomme = new JButton("Gomme");
        btnGomme.addActionListener(e -> {
            gommeActive = true;
            couleurCourante = Color.WHITE;
        });
        barreOutils.add(btnGomme);

        // Effacer tout le dessin
        JButton btnEffacer = new JButton("Effacer");
        btnEffacer.addActionListener(e -> effacer());
        barreOutils.add(btnEffacer);

        // Slider pour ajuster la taille du pinceau
        taillePinceauSlider = new JSlider(1, 30, taillePinceau);
        taillePinceauSlider.setMajorTickSpacing(5);
        taillePinceauSlider.setMinorTickSpacing(1);
        taillePinceauSlider.setPaintTicks(true);
        taillePinceauSlider.setPaintLabels(true);
        taillePinceauSlider.addChangeListener(e -> {
            taillePinceau = taillePinceauSlider.getValue(); // Met à jour la taille
        });
        JLabel tailleLabel = new JLabel("Taille:");
        barreOutils.add(tailleLabel);
        barreOutils.add(taillePinceauSlider);

        // Choix des couleurs selon le niveau
        if (niveauFacile) {
            // Couleurs fixes en mode facile
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
        } else { // Sinon (si on est au niveau difficile)
            // Sélecteur de couleur avancé en mode difficile
            JButton btnCouleur = new JButton("Couleur"); // Je crée un bouton "Couleur".
            btnCouleur.addActionListener(e -> { // Quand on clique sur "Couleur"
                Color selectedColor = JColorChooser.showDialog(this, "Choisir une couleur", couleurCourante);// ...j'ouvre une fenêtre spéciale pour choisir n'importe quelle couleur.
                if (selectedColor != null) { // Si on a choisi une couleur (et pas juste fermé la fenêtre)
                    gommeActive = false;// ...je désactive la gomme...
                    couleurCourante = selectedColor; // je mets la couleur qu on a choisie
                }
            });
            barreOutils.add(btnCouleur);
        }

        // Bouton pour enregistrer le dessin
        JButton btnEnregistrer = new JButton("Enregistrer");
        btnEnregistrer.addActionListener(e -> enregistrerDessin());
        barreOutils.add(btnEnregistrer);

        add(barreOutils, BorderLayout.NORTH);

        // Une fois l'interface prête, on initialise l'image de dessin
        SwingUtilities.invokeLater(this::initialiserImage);
    }

    // Crée ou recrée l'image de dessin
    private void initialiserImage() {
        if (zoneDessin.getWidth() > 0 && zoneDessin.getHeight() > 0) {
            imageDessin = new BufferedImage(
                zoneDessin.getWidth(),
                zoneDessin.getHeight(),
                BufferedImage.TYPE_INT_RGB
            );
            graphicsDessin = imageDessin.createGraphics();
            effacer(); // Efface pour avoir un fond blanc
        }
    }

    // Dessine un simple point
    private void dessinerPoint(Point p) {
        if (graphicsDessin == null) return;

        graphicsDessin.setColor(gommeActive ? Color.WHITE : couleurCourante);
        graphicsDessin.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsDessin.drawLine(p.x, p.y, p.x, p.y); // Une ligne du point vers lui-même = un point
        zoneDessin.repaint();
    }

    // Dessine une ligne entre deux points
    private void dessinerLigne(Point from, Point to) {
        if (graphicsDessin == null) return;

        graphicsDessin.setColor(gommeActive ? Color.WHITE : couleurCourante);
        graphicsDessin.setStroke(new BasicStroke(taillePinceau, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphicsDessin.drawLine(from.x, from.y, to.x, to.y);
        zoneDessin.repaint();
    }

    // Efface tout (remplit de blanc)
    private void effacer() {
        if (graphicsDessin != null) {
            graphicsDessin.setColor(Color.WHITE);
            graphicsDessin.fillRect(0, 0, imageDessin.getWidth(), imageDessin.getHeight());
            zoneDessin.repaint();
        }
    }

    // Sauvegarde le dessin dans un fichier PNG
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

    // Permet de changer de niveau (facile/difficile)
    public void setNiveau(int niveau) {
        this.niveauFacile = (niveau == 1);
        refreshInterface(); // On recharge toute l'interface avec le bon niveau
    }

    // Recharge toute l’interface (utile si on change de niveau)
    private void refreshInterface() {
        removeAll();
        initGui();
        revalidate();
        repaint();
    }

    // Permet de récupérer le panel (utile si on utilise cette classe ailleurs)
    public JPanel getPanel() {
        return this;
    }

    // Définit la taille préférée de l'ardoise
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600); // Taille fixe demandée
    }
}
