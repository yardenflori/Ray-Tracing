public class Plane extends Shape
{
	private static final double epsilon = 0.000001;
	
	Vector normal;
	double offset;
	
	public Plane(Vector normal, double offset, int mat_idx)
	{
		this.normal = normal;
		this.offset = offset;
		this.mat_idx = mat_idx;
	}
	
	@Override
	public Vector normalAtPOC(Intersection hit, Ray hittingRay)
	{
		Vector N = this.normal.normalize();
		
		Vector ray = hittingRay.direction.multiply(-1).normalize();
		if(Vector.dot(N, ray) < 0.0)
			return N.multiply(-1);
		return N;
	}
	
	@Override
	public Intersection getIntersection(Scene scene, Ray ray)
	{
		double alpha = Vector.dot(ray.direction, this.normal);
		
		if(Math.abs(alpha) < epsilon)
			return null;
		
		double whereHit = (this.offset - Vector.dot(ray.originPoint, this.normal)) / alpha;
		if(whereHit < 0)
			return null;
		
		Vector pointOfContact = Vector.add(ray.originPoint, ray.direction.multiply(whereHit));	
		
		return new Intersection(scene, this, ray, pointOfContact);
	}
	
	
}
