import java.util.*;

public class Radar {

	public static void main(String[] args) {

		Saca saca = new Saca();
		saca.start();

		saca.ajouter_avions(new Avion());
		saca.ajouter_avions(new Avion());
		saca.ajouter_avions(new Avion());

		try {
			while (true) {
				for (String key : saca.getAvions().keySet()) {
					saca.getAvions().get(key).se_deplacer();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
