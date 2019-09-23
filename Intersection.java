import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Intersection
{
	Scene scene;
	Shape shape;
	Ray ray;
	Vector pointOfContact;
	Vector normal;
	
	public Intersection(Scene scene, Shape shape, Ray ray, Vector pointOfContact)
	{
		this.scene = scene;
		this.shape = shape;
		this.ray = ray;
		this.pointOfContact = pointOfContact;
	}
	
	
	
	public static Intersection findIntersection(Scene scene, Ray ray, Shape[] shapeToLeave)
	{
		Intersection firstInter = null;		
		List<Shape> shapeToLeaveList = Arrays.asList(shapeToLeave);
		
		double firstInterDist = 0.0;
		for(Shape shape: scene.shapes) {
			if(shapeToLeaveList.contains(shape))
				continue;
			
			Intersection inter = shape.getIntersection(scene, ray);
			if(inter != null)
			{
				double distance = Vector.pointsToVector(ray.originPoint,
						inter.pointOfContact).vecLength();
				if(firstInter == null || distance < firstInterDist)
				{
					firstInterDist = distance;
					firstInter = inter;
				}
			}
		}
		
		if(firstInter == null)
			return new Intersection(scene, null, ray, null);
		return firstInter;
	}
	
	public static Intersection findIntersection(Scene scene, Ray ray)
	{
		return Intersection.findIntersection(scene, ray, new Shape[0]);
	}
	
	public static List<Shape> findAllIntersectingShapes(Scene scene, Ray ray, Shape shapeToStop)
	{
		List<Shape> shapes = new ArrayList<>();
		List<Double> dists = new ArrayList<>();	
		
		double stopDist = Double.MAX_VALUE;
		for(Shape shape: scene.shapes)
		{
			
			Intersection inter = shape.getIntersection(scene, ray);
			if(inter != null)
			{
				double distance = Vector.pointsToVector(ray.originPoint,
						inter.pointOfContact).vecLength();
				
				if(shapeToStop == shape)
				{
					stopDist = distance;
				} 
				else
				{
					shapes.add(shape);
					dists.add(distance);
				}
			}
		}
		
		List<Shape> inTheWayShapes = new ArrayList<>();
		
		for(int i = 0; i <shapes.size(); i++)
		{
			if(dists.get(i) < stopDist)
				inTheWayShapes.add(shapes.get(i));
		}
		
		return inTheWayShapes;
	}
	

}
