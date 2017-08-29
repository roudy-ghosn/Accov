
public class Coordonnees 
{
	private int x;
	private int y;
	private int altitude;
	
	public Coordonnees(int x, int y, int altitude)
	{
		this.x = x;
		this.y = y;
		this.altitude = altitude;
	}

	public int getX()
	{
		return this.x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getAltitude()
	{
		return altitude;
	}

	public void setAltitude(int altitude)
	{
		this.altitude = altitude;
	}
}
