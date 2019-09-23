public class Camera
{
	Vector position, lookAtPoint, up;
	double screenDistance, screenWidth;
	
	Vector normal;
	Vector u, v, w; 
	
	public Camera(Vector position, Vector lookAtPoint, Vector up,
			double screenDistance, double screenWidth)
	{
		this.position = position;
		this.lookAtPoint = lookAtPoint;
		this.up = up;
		this.screenDistance = screenDistance;
		this.screenWidth = screenWidth;
		
		this.calcCoordSystem();
		
	}
	
	private void calcCoordSystem()
	{
		this.normal = Vector.pointsToVector(this.position, this.lookAtPoint);		
		this.w = this.normal.normalize();
		this.u = Vector.crossNormalized(this.up, this.normal);
		this.v = Vector.cross(this.w, this.u);
	}
	
	
	
}
