import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Saca extends Thread {
	private AjoutAvion ajoutAvion;
	private AjoutControlAvion ajoutControlAvion;

	private static BufferedReader buffer;
	private static HashMap<String, Avion> avions;
	private static ArrayList<BufferedReader> buffers;
	private static HashMap<String, Avion> avionsControle;

	private static Integer compteurAvion = 1;
	private static Integer compteurControlleur = 1;

	/* Constructor */

	public Saca() {
		avions = new HashMap<String, Avion>();
		buffers = new ArrayList<BufferedReader>();
		avionsControle = new HashMap<String, Avion>();
	}

	/* Getters & Setters */

	public HashMap<String, Avion> getAvions() {
		return avions;
	}

	public void setAvions(HashMap<String, Avion> avions) {
		this.avions = avions;
	}

	/* Functions & Procedures */

	public static class AjoutControlAvion extends Thread {
		@Override
		public void run() {
			Socket controlSocket;
			Controlleur controlleur;
			ServerSocket controlServerSocket;

			try {
				controlServerSocket = new ServerSocket(1501);

				while (true) {
					controlSocket = controlServerSocket.accept();
					System.out.println("Controlleur " + compteurControlleur++
							+ " Connecté");
					controlleur = new Controlleur(controlSocket);
					controlleur.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class Controlleur extends Thread {
		PrintWriter out;
		BufferedReader in;
		Socket controlSocket;
		String messageErreur = "";

		Avion avion;
		String action;
		Integer valeur = 0;
		String numeroVol = "";
		String controlleur = "";

		public Controlleur(Socket controlSocket) {
			this.controlSocket = controlSocket;
			try {
				in = new BufferedReader(new InputStreamReader(
						controlSocket.getInputStream()));
				out = new PrintWriter(controlSocket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean controller_action(String request) {
			controlleur = request.substring(0, request.indexOf("/"));
			if (request.contains("fini"))
				action = request.substring(request.indexOf("/") + 1,
						request.length());
			else
				action = request.substring(request.indexOf("/") + 1,
						request.indexOf("="));

			if ("fini".equals(action)) {
				avionsControle.remove(controlleur);
			} else if ("avion".equals(action)) {
				numeroVol = request.substring(request.indexOf("=") + 1,
						request.length());
				if (avions.get(numeroVol) == null) {
					messageErreur = "Plane " + numeroVol + " doesn't exist ";
					return false;
				} else {
					for (String key : avionsControle.keySet()) {
						if (numeroVol.equals(avionsControle.get(key)
								.getNumeroVol())) {
							messageErreur = "Plane " + numeroVol
									+ " already being controlled ";
							return false;
						}
					}
					avionsControle.put(controlleur, avions.get(numeroVol));
				}
			} else {
				try {
					valeur = Integer.parseInt((request.substring(
							request.indexOf("=") + 1, request.length())));
				} catch (NumberFormatException e) {
					messageErreur = "Value entered is not a number";
					return false;
				}

				avion = avionsControle.get(controlleur);
				switch (action) {
				case "vitesse":
					avion.changer_vitesse(valeur);
					break;
				case "cap":
					avion.changer_cap(valeur);
					break;
				case "altitude":
					avion.changer_altitude(valeur);
					break;
				default:
					messageErreur = "Command not found for " + numeroVol + " ";
					return false;
				}
			}
			return true;
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (controller_action(in.readLine())) {
						out.println("Action Executed Successfully");
					} else {
						out.println("Erreur : " + messageErreur);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class AvionLog extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					sleep(1500);
					if (check_collision()) {
						System.out
								.println("A possible collision was detected !!");
						this.stop();
					} else {
						System.out
								.println("--------------------------------------------");
						for (String key : avions.keySet()) {
							System.out.println(avions.get(key)
									.afficher_donnees());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean check_collision() {
		for (String key : avions.keySet()) {
			for (String key2 : avions.keySet()) {
				if (avions.get(key).getCoordonnes().getX() == avions.get(key2)
						.getCoordonnes().getX()
						&& avions.get(key).getCoordonnes().getY() == avions
								.get(key2).getCoordonnes().getY()
						&& avions.get(key).getCoordonnes().getAltitude() == avions
								.get(key2).getCoordonnes().getAltitude()
						&& key != key2) {
					return true;
				}
			}
		}
		return false;
	}

	public static class AjoutAvion extends Thread {
		Socket socket;
		ServerSocket serveurAvion;
		AvionLog avionLog = new AvionLog();

		@Override
		public void run() {
			try {
				serveurAvion = new ServerSocket(1500);

				while (true) {
					socket = serveurAvion.accept();
					System.out.println("Flight Number " + compteurAvion++
							+ " successfully connected with SACA:");
					buffer = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					System.out.println(buffer.readLine());
					buffers.add(buffer);
					if (buffers.size() == 1) {
						avionLog.start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void ajouter_avions(Avion avion) {
		if (avion != null)
			avions.put(avion.getNumeroVol(), avion);
	}

	@Override
	public void run() {
		System.out.println("SACA Started Successfully");

		ajoutAvion = new AjoutAvion();
		ajoutControlAvion = new AjoutControlAvion();

		try {
			ajoutAvion.start();
			ajoutControlAvion.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
