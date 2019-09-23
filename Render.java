
public class Render
{
	public Scene scene;
	public Ray[][] rays;
	
	public Render(Scene scene, Ray[][] rays) 
	{
		this.scene = scene;
		this.rays = rays;
	}

	public Vector[] rendLine() 
	{
		Vector[] colors = new Vector[this.rays.length];
		for(int i = 0; i < colors.length; i++)
			colors[i] = scene.getColorFromArr(this.rays[i]);
		return colors;
	}

}
