public class Ray
{
	
	public Vector originPoint, direction;
	
	public Ray(Vector originPoint, Vector direction)
	{
		this.originPoint = originPoint;
		this.direction = direction;
	}
	
	public Vector getPointAtDistance(double distance) {
		return Vector.add(this.originPoint, this.direction.multiply(distance));
	}
}

