
import java.util.Arrays;

public class Vector
{
	static final double epsilon = 0.000001;
	
	double[] coords;
	int length;
	
	public Vector(double ... arr)
	{
		this.coords = arr;
		this.length = this.coords.length;
	}
	
	public double x()
	{
		return this.coords[0];
	}
	
	
	public double y()
	{
		return this.coords[1];
	}
	
	
	public double z()
	{
		return this.coords[2];
	}
	
	
	public boolean isPoint()
	{
		return (this.length == 3);
	}
	
	public static double dot(Vector v1, Vector v2)
	{
		if(v1.length != v2.length)
		{
			System.exit(5);
		}
		
		double res = 0.0;
		for(int i = 0; i < v1.length; i++)
			res += v1.coords[i] * v2.coords[i];
		
		return res;
	}
	
	public static Vector cross(Vector v1, Vector v2)
	{
		if(!v1.isPoint() || !v2.isPoint())
			return null;
		
		double x = v1.y() * v2.z() - v1.z() * v2.y();
		double y = v1.z() * v2.x() - v1.x() * v2.z();
		double z = v1.x() * v2.y() - v1.y() * v2.x();
		
		return new Vector(x, y, z);
	}
	
	public static Vector crossNormalized(Vector v1, Vector v2)
	{
		return Vector.cross(v1, v2).normalize();
	}
	
	public Vector normalize()
	{
		double length = this.vecLength();
		if(length < epsilon)
			return this;
		
		return this.multiply(1/length);
	}
	
	public double vecLength()
	{
		double sum = 0.0;
		for(int i = 0; i < this.length; i++)
			sum += Math.pow(this.coords[i], 2);
		return Math.sqrt(sum);
	}
	
	public Vector multiply(double scalar)
	{
		double[] arr = new double[this.length];
		for(int i = 0; i < this.length; i++)
			arr[i] = this.coords[i] * scalar;
		return new Vector(arr);
	}
	
	public static Vector multiply(Vector ... vectors)
	{
		double[] arr = null;
		for(Vector v: vectors)
		{
			if(arr == null)
				arr = Arrays.copyOf(v.coords, v.length);
			else
			{
				if(arr.length != v.length)
					return null;
				
				for(int i = 0; i < v.length; i++)
					arr[i] *= v.coords[i];
			}
		}
		
		return new Vector(arr);
	}
	
	public static Vector add(Vector ... vectors)
	{
		double[] arr = null;
		
		for(Vector v: vectors)
		{
			if(arr == null)
				arr = new double[v.length];
			else
				if(arr.length != v.length)
					return null;
			
			for(int i = 0; i < v.length; i++)
				arr[i] += v.coords[i];
		}
		
		return new Vector(arr);
	}
	
	public static Vector pointsToVector(Vector p1, Vector p2)
	{
		if(p1.length != p2.length)
			return null;
		
		double[] arr = new double[p1.length];
		for(int i = 0; i < arr.length; i++)
			arr[i] = p2.coords[i] - p1.coords[i];
		return new Vector(arr);
	}
	
	
	public Vector copy(int size)
	{
		return new Vector(Arrays.copyOf(this.coords, size));
	}
	
	public Vector copy()
	{
		return this.copy(this.length);
	}
	
	public Vector topBoundries(double ... boundries) {
		if(boundries.length != this.length)
			return null;
		
		for(int i = 0; i < boundries.length; i++) {
			if(this.coords[i] > boundries[i])
			this.coords[i] = boundries[i];
		}
		
		return this;
	}
	
	public void print()
	{
		for (int i =0; i< this.coords.length;i++)
		{
			System.out.println(this.coords[i]);
		}
	}
	
	
}
