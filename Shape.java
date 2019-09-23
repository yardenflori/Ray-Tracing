public abstract class Shape
{
	public int mat_idx;
	
	public abstract Intersection getIntersection(Scene scene, Ray ray);
	
	public abstract Vector normalAtPOC(Intersection hit, Ray hittingRay);

}
