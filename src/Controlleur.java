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

			while (avion == null) {
				System.out
						.print("Please enter the flight Number composed of 5 characters : ");
				line = read.readLine();
				if (line != null && line.length() == 5) {
					out.println(id + "/avion=" + line);
					reponse = in.readLine();
					System.out.println(reponse);
					if (!reponse.contains("Erreur"))
						avion = line;
				}
			}

			System.out.println("Avalaible actions : \nvitesse=value \ncap=value \naltitude=value \nfini");

			while (true) {
				System.out.print("Please enter the action for the flight "
						+ avion + ":");
				line = read.readLine();
				out.println(id + "/" + line);
				reponse = in.readLine();
				if (reponse != null)
					System.out.println(reponse);
				if ("fini".equals(line)) {
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
