

public class Sphere extends Shape
{
	static final double epsilon = 0.000001;
	
	Vector center;
	double radius;
	
	public Sphere(Vector center, double radius, int mat_idx)
	{
		this.center = center;
		this.radius = radius;
		this.mat_idx = mat_idx;
	}

	@Override
	public Intersection getIntersection(Scene scene, Ray ray)
	{
		Vector originToCenter = Vector.pointsToVector(this.center, ray.originPoint);
		
		double a = 1.0;
		double b = 2.0 * Vector.dot(ray.direction, originToCenter);
		double c = Math.pow(originToCenter.vecLength(), 2) - Math.pow(this.radius, 2);
		
		double discriminant = Math.pow(b,  2) - 4 * a * c;
		if(discriminant < 0)
			return null;
		
		if(discriminant - epsilon <= 0)
		{
			double t = (-b)/(2 * a);
			
			if(t < 0)
				return null;
			
			Vector pointOfContact = Vector.add(ray.originPoint, ray.direction.multiply(t));
			return new Intersection(scene, this, ray, pointOfContact);
		}
		
		double t1 = (-b + Math.sqrt(discriminant))/(2 * a);
		double t2 = (-b - Math.sqrt(discriminant))/(2 * a);
		
		if(t1 < 0 && t2 < 0)
			return null;
		
		Vector pointOfContact1 = Vector.add(ray.originPoint, ray.direction.multiply(t1));
		Vector pointOfContact2 = Vector.add(ray.originPoint, ray.direction.multiply(t2));
		
		Vector selectedPOC = null;
		
		if(t2 < 0)
			selectedPOC = pointOfContact1;
		else if(t1 < 0)
			selectedPOC = pointOfContact2;
		else
		{
			double length1 = Vector.pointsToVector(ray.originPoint, pointOfContact1).vecLength();
			double length2 = Vector.pointsToVector(ray.originPoint, pointOfContact2).vecLength();
			
			if(length1 < length2)
				selectedPOC = pointOfContact1;
			else
				selectedPOC = pointOfContact2;
		}
		
		return new Intersection(scene, this, ray, selectedPOC);
	}

	@Override
	public Vector normalAtPOC(Intersection hit, Ray hittingRay)
	{
		Vector N = Vector.pointsToVector(this.center, hit.pointOfContact).normalize();
		
		Vector v = hittingRay.direction.multiply(-1).normalize();
		if(Vector.dot(N, v) < 0.0)
			return N.multiply(-1);
		return N;
	}

}

