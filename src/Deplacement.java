
public class Deplacement 
{
	private int vitesse;
	private int cap;
	
	public Deplacement(int vitesse, int cap)
	{
		this.vitesse = vitesse;
		this.cap = cap;
	}

	public int getCap()
	{
		return cap;
	}

	public void setCap(int cap)
	{
		this.cap = cap;
	}

	public int getVitesse()
	{
		return vitesse;
	}
	
	public void setVitesse(int vitesse)
	{
		this.vitesse = vitesse;
	}
}
