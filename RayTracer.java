import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer
{

	public int imageWidth;
	public int imageHeight;
	
	public Scene scene;

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as input.
	 */
	public static void main(String[] args)
	{

		try {

			RayTracer tracer = new RayTracer();

            // Default values:
			tracer.imageWidth = 300;
			tracer.imageHeight = 300;

			if (args.length < 2)
				throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3)
			{
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}
			


			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);

//		} catch (IOException e) {
//			System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException
	{
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		scene = new Scene(this.imageHeight, this.imageWidth);
		int max_mat = 0;

		while ((line = r.readLine()) != null)
		{
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#'))
			{  // This line in the scene file is a comment
				continue;
			}
			else
			{
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam")) {
					Vector pos = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					Vector lap = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));
					Vector up = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]), Double.parseDouble(params[8]));
					double screen_dist = Double.parseDouble(params[9]), screen_width = Double.parseDouble(params[10]);
					scene.addCamera(new Camera(pos, lap, up, screen_dist, screen_width));
					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				}
				else if (code.equals("set")) {
					scene.backgroundColor = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					scene.N = Integer.parseInt(params[3]);
					scene.maxRecursion = Integer.parseInt(params[4]);
					scene.SS = Integer.parseInt(params[5]);
                    System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				}
				else if (code.equals("mtl"))
				{
					Vector diffuse = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					Vector specular = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));
					Vector reflection = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]), Double.parseDouble(params[8]));
					Double phong = Double.parseDouble(params[9]);
					Double trans = Double.parseDouble(params[10]);
					scene.addMaterial(new Material(diffuse, specular, reflection, phong, trans));
					System.out.println(String.format("Parsed material (line %d)", lineNum));
				}
				else if (code.equals("sph"))
				{
					Vector center = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					double radius = Double.parseDouble(params[3]);
					int mat_idx = Integer.parseInt(params[4]) - 1;
					max_mat = Math.max(max_mat, mat_idx);
					scene.addShape(new Sphere(center, radius, mat_idx));
					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				}
				else if (code.equals("pln"))
				{
					Vector normal = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					double offset = Double.parseDouble(params[3]);
					int mat_idx = Integer.parseInt(params[4]) - 1;
					max_mat = Math.max(max_mat, mat_idx);
					scene.addShape(new Plane(normal, offset, mat_idx));
					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				}
				else if (code.equals("trg"))
                {
                    Vector p1 = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    Vector p2 = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));
                    Vector p3 = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]), Double.parseDouble(params[8]));
                    int mat_idx = Integer.parseInt(params[9]) - 1;
                    max_mat = Math.max(max_mat, mat_idx);
                    scene.addShape(new Triangle(p1, p2, p3, mat_idx));
                    System.out.println(String.format("Parsed triangle (line %d)", lineNum));
                }
				else if (code.equals("lgt"))
				{
					Vector center = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
					Vector color = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));
					double specularIntensity = Double.parseDouble(params[6]);
					double shadowIntensity = Double.parseDouble(params[7]);
					double lightRadius = Double.parseDouble(params[8]);
					scene.addLight(new Light(center, color, specularIntensity, shadowIntensity, lightRadius));
					System.out.println(String.format("Parsed light (line %d)", lineNum));
				}
				else
				{
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}
		
		r.close();
		
		if(scene.camera == null) {
			System.out.println("ERROR: Did not recognize camera settings in file");
			System.exit(1);
		}
		if(max_mat >= scene.materials.size()) {
			System.out.println("ERROR: There is a shape that uses an undefined material");
			System.exit(1);
		}

		System.out.println("Finished parsing scene file " + sceneFileName);

	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName)
	{
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];

		Vector [][] colors = new Vector[scene.imageHeight][];
		
		for(int i = 0; i < scene.imageHeight; i++)
		{
			Ray[][] rowRays = new Ray[scene.imageWidth][scene.SS*scene.SS];
			
			for(int j = 0; j < scene.imageWidth; j++)
			{
				System.out.println(i);
				Ray [] ray = scene.constructRayThroughPixelArr(i, j);
				rowRays[j] = ray;
			}
			Render render = new Render(scene, rowRays);
			colors[i] = render.rendLine();
		}
		
		for(int i = 0; i < scene.imageHeight; i++) {
			Vector[] rowColors = null;
			rowColors = colors[i];
			
			for(int j = 0; j < scene.imageWidth; j++) {
				Vector color = rowColors[j];
				
				rgbData[(i * scene.imageWidth + j) * 3 + 0] = (byte) ((int)(color.x() * 255));
				rgbData[(i * scene.imageWidth + j) * 3 + 1] = (byte) ((int)(color.y() * 255));
				rgbData[(i * scene.imageWidth + j) * 3 + 2] = (byte) ((int) (color.z() * 255));
			}
		}
		
		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

                // The time is measured for your own conveniece, rendering speed will not affect your score
                // unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

                // This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);

	}


	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName)
	{
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
	    int height = buffer.length / width / 3;
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    ColorModel cm = new ComponentColorModel(cs, false, false,
	            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    SampleModel sm = cm.createCompatibleSampleModel(width, height);
	    DataBufferByte db = new DataBufferByte(buffer, width * height);
	    WritableRaster raster = Raster.createWritableRaster(sm, db, null);
	    BufferedImage result = new BufferedImage(cm, raster, false, null);

	    return result;
	}

	public static class RayTracerException extends Exception {
		private static final long serialVersionUID = 1L;

		public RayTracerException(String msg) {  super(msg); }
	}


}
