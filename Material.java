

public class Material
{
	Vector diffuseColor, specularColor, reflectionColor;
	double phong; 
	double trans;
	
	public Material(Vector diffuseColor, Vector specularColor, Vector reflectionColor,
			double phong, double trans)
	{
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.reflectionColor = reflectionColor;
		this.phong = phong;
		this.trans = trans;
	}
}

