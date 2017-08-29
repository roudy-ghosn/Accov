import java.io.*;
import java.net.*;

import static java.lang.System.exit;
import static java.lang.Math.*;
import static java.lang.Thread.*;

public class Avion {
	private int minVit = 200;
	private int vitMax = 1000;
	private int maxAlt = 20000;

	private Socket socket;
	private PrintStream out;
	private String numeroVol;
	private BufferedReader in;
	private Deplacement deplacement;
	private Coordonnees coordonnees;
	private String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/* Constructor */

	public Avion() {
		int x = (int) (1000 + Math.random() * 10 % 1000);
		int y = (int) (1000 + Math.random() * 10 % 1000);
		int a = (int) (1000 + Math.random() * 10 % 1000);

		int v = (int) (600 + Math.random() * 10 % 200);
		int c = (int) Math.random() * 10 % 360;

		deplacement = new Deplacement(v, c);
		coordonnees = new Coordonnees(x, y, a);

		numeroVol = generer_numero_vol();

		ouvrir_communication();
		envoyer_caracteristiques();
	}

	/* Getters & Setters */

	public String getNumeroVol() {
		return numeroVol;
	}

	public void setNumeroVol(String numeroVol) {
		this.numeroVol = numeroVol;
	}

	public Deplacement getDeplacement() {
		return deplacement;
	}

	public void setDeplacement(Deplacement deplacement) {
		this.deplacement = deplacement;
	}

	public Coordonnees getCoordonnes() {
		return coordonnees;
	}

	public void setCoordonnees(Coordonnees coordonnees) {
		this.coordonnees = coordonnees;
	}

	/* Functions & Procedures */

	public String generer_numero_vol() {
		StringBuilder sb = new StringBuilder(5);
		for (int i = 0; i < 2; i++)
			sb.append(letters.charAt((int) Math.floor(Math.random()
					* letters.length())));

		for (int i = 0; i < 3; i++)
			sb.append((int) Math.floor(Math.random() * 10));

		return sb.toString();
	}

	public boolean ouvrir_communication() {
		try {
			socket = new Socket("localhost", 1500);
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			return true;
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
			return false;
		}
	}

	public void fermer_communication() {
		try {
			socket.close();
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
		}
	}

	public void envoyer_caracteristiques() {
		out.println(afficher_donnees());
	}

	public void changer_vitesse(int vitesse) {
		if (vitesse < 0)
			deplacement.setVitesse(0);
		else if (vitesse > vitMax)
			deplacement.setVitesse(vitMax);
		else
			deplacement.setVitesse(vitesse);
	}

	public void changer_cap(int cap) {
		if ((cap >= 0) && (cap < 360))
			deplacement.setCap(cap);
	}

	public void changer_altitude(int alt) {
		if (alt < 0)
			coordonnees.setAltitude(0);
		else if (alt > maxAlt)
			coordonnees.setAltitude(maxAlt);
		else
			coordonnees.setAltitude(alt);
	}

	public String afficher_donnees() {
		return "Vol: " + numeroVol + ", Localisation: (" + coordonnees.getX()
				+ ", " + coordonnees.getY() + "), Altitude: "
				+ coordonnees.getAltitude() + ", Vitesse: "
				+ deplacement.getVitesse() + ", Cap: " + deplacement.getCap();
	}

	public void calcul_deplacement() {
		float cosinus, sinus, dep_x, dep_y;

		if (deplacement.getVitesse() < minVit) {
			System.out.println("Vitesse trop faible : crash de l'avion \n");
			fermer_communication();
			exit(2);
		}

		if (coordonnees.getAltitude() == 0) {
			System.out.println("L'avion s'est ecrase au sol\n");
			fermer_communication();
			exit(3);
		}

		// cos et sin ont un paramétre en radian, deplacement.cap en degré nos
		// habitudes
		// francophone
		/*
		 * Angle en radian = pi * (angle en degré) / 180 Angle en radian = pi *
		 * (angle en grade) / 200 Angle en grade = 200 * (angle en degré) / 180
		 * Angle en grade = 200 * (angle en radian) / pi Angle en degré = 180 *
		 * (angle en radian) / pi Angle en degré = 180 * (angle en grade) / 200
		 */

		cosinus = (float) cos(deplacement.getCap() * 2 * Math.PI / 360);
		sinus = (float) sin(deplacement.getCap() * 2 * Math.PI / 360);

		// newPOS = oldPOS + Vt
		dep_x = cosinus * deplacement.getVitesse() * 10 / minVit;
		dep_y = sinus * deplacement.getVitesse() * 10 / minVit;

		// on se deplace d'au moins une case quels que soient le cap et la
		// vitesse
		// sauf si cap est un des angles droit

		if ((dep_x > 0) && (dep_x < 1))
			dep_x = 1;
		if ((dep_x < 0) && (dep_x > -1))
			dep_x = -1;

		if ((dep_y > 0) && (dep_y < 1))
			dep_y = 1;
		if ((dep_y < 0) && (dep_y > -1))
			dep_y = -1;

		coordonnees.setX(coordonnees.getX() + (int) dep_x);
		coordonnees.setY(coordonnees.getY() + (int) dep_y);
	}

	public void se_deplacer() throws InterruptedException {
		sleep(1000);
		calcul_deplacement();
		envoyer_caracteristiques();
	}
}
