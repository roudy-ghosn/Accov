import java.io.*;
import java.net.*;

public class Controlleur extends Thread {
	private String id;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String line, avion;
	private String reponse;

	/* Constructor */

	public Controlleur(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		BufferedReader read = new BufferedReader(new InputStreamReader(
				System.in));

		try {
			socket = new Socket("localhost", 1501);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			
			while(reponse == null || reponse.contains("Erreur")){
				System.out.print("Please enter the flight Number : ");
				line = read.readLine();
				if (line != null && line.length() == 5)
					out.println(id + "/avion=" + line);
				else 
					System.out.print("Please re-enter a flight Number composed of 5 characters : ");
				reponse = in.readLine();
				System.out.println(reponse);
			}
			
			avion = line;
			
			System.out.println("Avalaible actions : vitesse, cap, altitude, fini ");
			
			while (true) {
				System.out.print("Please enter the action for the flight "
						+ avion + " (Example vitesse=500) : ");
				line = read.readLine();
				out.println(id + "/" + line);
				reponse = in.readLine();
				if (reponse != null)
					System.out.println(reponse);
				if (line == "fini")
					Thread.interrupted();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
