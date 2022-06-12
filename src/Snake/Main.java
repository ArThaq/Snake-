package Snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel{
	
	private final int WIDTH = 50;								// Initialise la largeur des cases
	private Deque<SnakePart> snake = new ArrayDeque<>();		// Initialise une "Double Ended Queue" qui permet l'insertion ou la suppression
																// d'element dans une file au debut ou a la fin, ici, cela permet de manipuler
																// la tete ou la queue du serpent
	private Point apple = new Point(0,0);						// Initialise les coordonnees de la pomme
	private Random rand = new Random();							// Initialise un random
	
	private boolean isGrowing = false;							// Initialise le bouleen d'agrandissement du serpent
	private boolean gameLost = false;
	
	private int offset = 0;										// Initialise l'int de decalage des parties du serpent
	private int newDirection = 39;								// Initialise la direction de base du serpent, le nombre correspond au code
																// de la touche sur le clavier : 37 = fleche de gauche
																//								 38 = fleche du haut
																// 								 39 = fleche de droite
																//								 40 = fleche du bas
	
	private static int dimFenetre = 669;						// Dimension dimFenetre x dimFenetre de la fenetre

	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake"); 					// Initialise la fenetre
		Main panel = new Main();								// Initialise le panneau
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void keyPressed(KeyEvent e) {
				panel.onKeyPressed(e.getKeyCode());				// Recupere le code de la touche de clavier pressee
				
			}
		});
		frame.setContentPane(panel);							// Remplace le panneau JPanel par defaut
		frame.setSize(dimFenetre, dimFenetre);							// Donne les dimensions a la fenetre
		frame.setResizable(false); 								// Empeche de redimensioner la fenetre
		frame.setLocationRelativeTo(null); 						// Centre la fenetre
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	// Arrete le programme quand la fenetre est fermee
		frame.setVisible(true);									// Affiche la fenetre
	}

	public Main() {
		createApple();											// Cree la pomme
		snake.add(new SnakePart(0, 0, 39));						// Cree le serpent
		setBackground(Color.WHITE);								// Rend le fond de la fenetre blanc
		new Thread(new Runnable() {						 		// Initialise un Thread qui permet executer simultanement plusieurs traitement
			
			@Override
			public void run() {									// Execute le sous-programme
				while(true) {									// Boucle infinie
					repaint();
					try {										// Traitement d'exception : bloc try
						Thread.sleep(15-snake.size()/10);				// Permet de modifier la fréquence de rafraichissement de la fenêtre, entre autre, la vitesse du serpent
					} catch (InterruptedException e) {			// Traitement d'exception : bloc catch
						e.printStackTrace();					// Affiche la trace de pile de l'instance
					}
				}
			}
		}).start();
	}
	
	public void createApple() {									// Cree une pomme aux coordonnees aleatoires
		boolean positionAvailable;								// Initialise le booleen de position disponible pour la pomme
		do {
			apple.x = rand.nextInt(12);							// Genere des coordonnees aleatoires
			apple.y = rand.nextInt(12);							// Genere des coordonnees aleatoires
			positionAvailable = true;
			for(SnakePart p : snake) {
				if (p.x == apple.x && p.y == apple.y) {			// Verifie que le serpent ne soit pas sur la position pour ne pas y genere la pomme
					positionAvailable = false;
					break;										// Sort de la boucle
				}
			}
		} while (!positionAvailable);							// Boucle tant que la postion n'est pas disponible
	}
	
	@Override
	protected void paintComponent(Graphics g) {					// Permet de peindre des cases
		super.paintComponent(g);
		
		if (gameLost) {
			g.setColor(Color.RED);								// Fixe la couleur du texte en rouge
			g.setFont(new Font("Arial", 80, 80));				// Fixe la police du texte
			g.drawString("Partie Terminée", dimFenetre/2 - g.getFontMetrics().stringWidth("Partie Terminée")/2, dimFenetre/2);
																// Permet de centrer le texte en divisant les dimensions de la fenetre par 2
																//  et en soustrayant la taille du texte
			g.setColor(Color.BLUE);
			g.setFont(new Font("Arial", 40, 40));
			g.drawString("Score : " + (snake.size() - 1), dimFenetre/2 - g.getFontMetrics().stringWidth("Score :  ")/2, dimFenetre - dimFenetre/4);
			return;
		}
		
		offset += 5;											// Incremente le decalage des parties du serpent
		SnakePart head = null;
		if (offset == WIDTH) {									// Remet a 0 le decalage quand il atteind la valeur de WIDTH
			offset = 0;
			try {
				head = (SnakePart) snake.getFirst().clone();	// Duplique la tete
				head.move();									// Deplace la tete
				head.direction = newDirection;					// Change la direction de la tete
				snake.addFirst(head);							// Rajoute la tete
				if (head.x == apple.x && head.y == apple.y) {	// Verifie si la tete mange la pomme
					isGrowing = true;
					createApple();
				}
				if (!isGrowing) {								// Verifie si le serpent grossi
					snake.pollLast();							// Supprime la queue
					
				}
				else {
					isGrowing = false;
				}
				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		g.setColor(Color.RED);									// Fixe la couleur de la pomme en rouge
		g.fillOval(apple.x * WIDTH + WIDTH/4, apple.y * WIDTH + WIDTH/4, WIDTH/2, WIDTH/2); 	// Dessine la pomme
		g.setColor(new Color(0,153,0));							// Fixe la couleur du serpent en vert
		for(SnakePart p : snake ) {								// Permet de simuler un decalage des parties du serpent en fonction de sa direction
																// afin de rendre le deplacement du serpent plus fluide
			if (offset == 0) {
				if (p != head) {
					if (p.x == head.x && p.y == head.y) {
						gameLost = true;
					}
				}
			}
			if (p.direction == 37 || p.direction == 39) {
				g.fillRect(p.x * WIDTH + ((p.direction == 37) ? -offset : offset), p.y * WIDTH, WIDTH, WIDTH);
			} else {
				g.fillRect(p.x * WIDTH, p.y * WIDTH + ((p.direction == 38) ? - offset : offset), WIDTH, WIDTH);
			}
			
		}
		g.setColor(Color.blue);
		g.drawString("Score : " + (snake.size() - 1), 10, 20);
		
	}
	
	public void onKeyPressed(int keyCode) {
		if (keyCode >= 37 && keyCode <= 40) {					// Les codes des touches de direction allant de 37 a 40, cette condition permet 
																// de ne pas prendre en compte les autres touches du clavier
			if (Math.abs(keyCode - newDirection) != 2) {		// Empeche de changer a la direction opposee
				newDirection = keyCode;
			}
			
		}
	}
	class SnakePart {
		public int x, y, direction;								// Initialise des ints
		
		public SnakePart(int x, int y, int direction) {			// Initialise les coordonnees des parties du serpent
			this.x = x;
			this.y = y;
			this.direction = direction;
		}
		
		public void move() {									// Permet de deplacer la tete du serpent en incrementant ou decrementant
																// ses coordonnees en fonction de la direction (soit la touche pressee)
			if (direction == 37 || direction == 39) {
				x += (direction == 37) ? -1 : 1;
				if (x > 13) {
					x = -1;
				}
				else if (x < -1) {
					x = 13;
				}
			} else {
				y += (direction == 38) ? -1 : 1;
				if (y > 13) {
					y = -1;
				}
				else if (y < -1) {
					y = 13;
				}
			}
		}
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new SnakePart(x, y, direction);
		}
	}
}
