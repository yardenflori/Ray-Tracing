import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene
{
	int imageHeight, imageWidth;
	double aspectRatio;
	
	Camera camera;
	List<Light> lights;
	List<Shape> shapes;
	List<Material> materials;
	Settings settings;
	
	Vector backgroundColor;
	int N;
	int SS;
	int maxRecursion;
	
	public Scene(int imageHeight, int imageWidth)
	{
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.aspectRatio = ((double) this.imageWidth) / this.imageHeight;
		
		this.lights = new ArrayList<>();
		this.shapes = new ArrayList<>();
		this.materials = new ArrayList<>();
		
		maxRecursion = 0;
		N = 1;
		backgroundColor = (new Vector(1,1,1));
	}
	
	public void addCamera(Camera camera)
	{
		if(this.camera != null)
		{
			System.out.println("You already have one camera!");
		}
		this.camera = camera;
	}
	
	public void addSettings(Settings settings)
	{
		if(this.settings != null)
		{
			System.out.println("You already set settings for the scene!");
		}
		this.settings = settings;
	}
	
	public void addMaterial(Material mat)
	{
		this.materials.add(mat);
	}
	
	public void addShape(Shape shape) {
		this.shapes.add(shape);
	}
	
	public void addLight(Light light) {
		this.lights.add(light);
	}
	
	public Ray constructRayThroughPixel(double i, double j)
	{
		double d = this.camera.screenDistance;
		Vector forwardScreenVec = this.camera.w;
		Vector rightScreenVec = this.camera.u;
		Vector topScreenVec = this.camera.v;
		double screenWidth = this.camera.screenWidth;
		double screenHeight = screenWidth / aspectRatio;
		
		Vector p0 = this.camera.position;
		Vector topLeftForwardOffset = forwardScreenVec.multiply(d);
		Vector topLeftRightOffset = rightScreenVec.multiply(-screenWidth/2.0);
		Vector topLeftTopOffset = topScreenVec.multiply(screenHeight / 2.0);

		Vector p1 = Vector.add(p0, topLeftForwardOffset, topLeftRightOffset, topLeftTopOffset);
		if(p1 == null)
			return null;
		
		double jSymmetric = j - this.imageWidth / 2.0;
		double iSymmetric = i - this.imageHeight / 2.0;
		
		double jScreen = (jSymmetric / this.imageWidth  + 0.5) * screenWidth;
		double iScreen  = (iSymmetric  / this.imageHeight + 0.5) * screenHeight;
		
		Vector rightScreenVecWithLength = rightScreenVec.multiply(jScreen);
		Vector topScreenVecWithLength = topScreenVec.multiply(-iScreen);
		
		Vector p = Vector.add(p1, topScreenVecWithLength, rightScreenVecWithLength);
		
		Vector direction = Vector.pointsToVector(p0, p).normalize(); 
		
		return new Ray(p0, direction); 
	}
	
	public Vector getColor(Ray ray)
	{
		return this.getColor(ray, this.maxRecursion);
	}
	
	public Vector getColor(Ray ray, int raysLeft, Shape ... excludedShapes) {
		if(raysLeft < 0)
			return new Vector(0, 0, 0);
		
		Intersection hit = Intersection.findIntersection(this, ray, excludedShapes);
		
		if(hit.shape == null) {
			return this.backgroundColor;
		}
		
		Vector totalColor = new Vector(0, 0, 0);
		Material material = this.materials.get(hit.shape.mat_idx);
		
		// Diffuse and Specular
		Vector diffAndSpec = calcDiffAndSpecular(hit, ray);
		// Reflection
		Vector normalAtPOC = hit.shape.normalAtPOC(hit, ray).normalize(); // N
		Vector reflectedVector = Vector.add(ray.direction.normalize(), normalAtPOC.multiply(-2 * Vector.dot(ray.direction.normalize(), normalAtPOC))).normalize();
		Ray reflectedRay = new Ray(hit.pointOfContact, reflectedVector);
		Vector reflectedColor = this.getColor(reflectedRay, raysLeft-1, hit.shape);
		Vector IRefl = Vector.multiply(reflectedColor, material.reflectionColor);
		Vector bgColor = new Vector(0, 0, 0);
		if(material.trans > 0.0) {
			Ray transparentcyRay = new Ray(Vector.add(hit.pointOfContact, ray.direction.multiply(0.0001)), ray.direction);
			bgColor = this.getColor(transparentcyRay, raysLeft-1, hit.shape);
		}
		// add transparency,diffuse,specular and reflection
		totalColor = Vector.add(totalColor, bgColor.multiply(material.trans));
		totalColor = Vector.add(totalColor, diffAndSpec.multiply(1 - material.trans));
		totalColor = Vector.add(totalColor, IRefl);
		
		if(hit.shape instanceof Sphere)
		{
			Sphere bowl = (Sphere) hit.shape;
			Vector center = bowl.center;
			Vector directToCamera = Vector.pointsToVector(center, this.camera.position).normalize();
			Vector hitToCenter = Vector.add(directToCamera.multiply(bowl.radius),(center));
			double distance = Math.sin(5*Vector.pointsToVector(hit.pointOfContact, hitToCenter).vecLength());
			
			totalColor=(totalColor.multiply(distance)).multiply(1/(Math.pow(2, 0.5)*bowl.radius));
		}
		
		return totalColor.topBoundries(1, 1, 1);
	}
	
	private Vector calcDiffAndSpecular(Intersection hit, Ray ray) {
		Material material = this.materials.get(hit.shape.mat_idx);
		
		Vector v = ray.direction.multiply(-1).normalize();
		Vector normalAtPOC = hit.shape.normalAtPOC(hit, ray).normalize(); // N
		
		Vector totalDiffLightOnPixel = new Vector(0, 0, 0);
		Vector totalSpecLightOnPixel = new Vector(0, 0, 0);
		for(Light light: this.lights) {
			Vector lightVector = Vector.pointsToVector(light.position, hit.pointOfContact).normalize();
			Vector toLightVector = lightVector.multiply(-1); // L
			if(Vector.dot(normalAtPOC, toLightVector) < 0.0)
				continue;
			
			Ray[] subRays = light.getPanelRays(hit.pointOfContact, this.N);
			double shadow = 0.0;
			for(Ray subRay: subRays) {
				List<Shape> intersections = Intersection.findAllIntersectingShapes(this, subRay, hit.shape);
				double intersectionsCount = 0.0;
				for(Shape interShape: intersections) {
					intersectionsCount += 1-this.materials.get(interShape.mat_idx).trans;
				}
				shadow += Math.pow(1-light.shadowIntensity, intersectionsCount);

			}
			
			totalDiffLightOnPixel = Vector.add(totalDiffLightOnPixel, light.color.multiply(Vector.dot(normalAtPOC, toLightVector) * shadow / subRays.length));
			
			Vector reflectedLight = Vector.add(lightVector, normalAtPOC.multiply(-2 * Vector.dot(lightVector, normalAtPOC))).normalize(); // R
			double angleWithLightReflection = Vector.dot(reflectedLight, v);
			if(angleWithLightReflection > 0.0)
				totalSpecLightOnPixel = Vector.add(totalSpecLightOnPixel, light.color.multiply(Math.pow(angleWithLightReflection, material.phong) * shadow * light.specularIntensity / subRays.length));
			
		}
		Vector diffLight = Vector.multiply(totalDiffLightOnPixel, material.diffuseColor);
		Vector specLight = Vector.multiply(totalSpecLightOnPixel, material.specularColor);
		return Vector.add(diffLight, specLight);
	}
	
	
	
	public Ray[] constructRayThroughPixelArr(double top, double left)
	{
		Ray[] rays = new Ray[SS*SS];
		
		Random rnd = new Random();
		
		double mult = 1.0;
		double subRectWidth = mult / SS;
		for(int i = 0; i < SS; i++) {
			double yMin = (subRectWidth * i)+left;
			for(int j = 0; j < SS; j++) {
				double xMin = (subRectWidth * j)+top;
				
				double xOffset = xMin + rnd.nextDouble() * subRectWidth;
				double yOffset = yMin + rnd.nextDouble() * subRectWidth;
				rays[i*SS + j] = constructRayThroughPixel(xOffset, yOffset);
			}
		}
		return rays;
	}
	
	public Vector getColorFromArr(Ray[] rays)
	{
		Vector sum = new Vector(0,0,0);
		for(int i = 0; i < SS*SS; i++)
		{
			sum = Vector.add(sum , getColor(rays[i]));
		}
		return sum.multiply((double)(1/(double)(SS*SS)));
	}
}