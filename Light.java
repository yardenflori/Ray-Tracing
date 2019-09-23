

import java.util.Random;

public class Light
{
	public Vector position, color;
	public double specularIntensity, shadowIntensity;
	public double lightRadius;
	
	public Light(Vector position, Vector color, double specularIntensity,
			double shadowIntensity, double lightRadius)
	{
		this.position = position;
		this.color = color;
		this.specularIntensity = specularIntensity;
		this.shadowIntensity = shadowIntensity;
		this.lightRadius = lightRadius;
	}
	
	public Ray[] getPanelRays(Vector whereToHit, int N)
	{
		Ray[] rays = new Ray[N*N];
		
		Vector normal = Vector.pointsToVector(this.position, whereToHit).normalize();
		Vector up = new Vector(0, 1, 0);		
		Vector right = Vector.cross(up, normal).normalize();
		Vector top = Vector.cross(normal, right);
		
		Random rnd = new Random();
		
		double recWidth = (this.lightRadius) / N;
		for(int i = 0; i < N; i++)
		{
			double y = recWidth * i - this.lightRadius * 0.5;
			
			for(int j = 0; j < N; j++)
			{
				double x = recWidth * j - this.lightRadius * 0.5;				
				double xOffset = x + rnd.nextDouble() * recWidth;
				double yOffset = y + rnd.nextDouble() * recWidth;
				
				Vector light = Vector.add(this.position, top.multiply(yOffset), right.multiply(xOffset));
				rays[i*N + j] = new Ray(light, Vector.pointsToVector(light, whereToHit).normalize());
			}
		}
		
		return rays;
	}
	
}

