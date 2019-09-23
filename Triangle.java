

public class Triangle extends Shape
{

    Vector p0, p1, p2;

    public Triangle(Vector p0, Vector p1, Vector p2, int mat_idx)
    {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.mat_idx = mat_idx;
    }

    private Vector getNormal()
    {
        Vector V = Vector.pointsToVector(p0, p1);
        Vector W = Vector.pointsToVector(p0, p2);
        return Vector.crossNormalized(V, W);
    }

    @Override
    public Intersection getIntersection(Scene scene, Ray ray)
    {
        double epsilon = 0.000001;
        Vector normal = getNormal();
        double offset = Vector.dot(normal, p0);
        double angle = Vector.dot(ray.direction, normal);

        if(Math.abs(angle) < epsilon)
            return null;

        double t = (offset - Vector.dot(ray.originPoint, normal)) / angle;
        if(t < 0) // object is behind the camera
            return null;

        Vector pointOfContact = Vector.add(ray.originPoint, ray.direction.multiply(t));

        Vector u = Vector.pointsToVector(p0, p1);
        Vector v = Vector.pointsToVector(p0, p2);
        Vector w = Vector.pointsToVector(p0, pointOfContact);

        double a = (Vector.dot(u, v)*Vector.dot(w, v) - Vector.dot(v, v)*Vector.dot(w, u)) / (Math.pow(Vector.dot(u, v), 2) - Vector.dot(u, u)*Vector.dot(v, v));
        double b = (Vector.dot(u, v)*Vector.dot(w, u) - Vector.dot(u, u)*Vector.dot(w, v)) / (Math.pow(Vector.dot(u, v), 2) - Vector.dot(u, u)*Vector.dot(v, v));

        if (a >= 0 && b >= 0 && a+b <= 1)
            return new Intersection(scene, this, ray, pointOfContact);
        return null;
    }

    @Override
    public Vector normalAtPOC(Intersection hit, Ray hittingRay)
    {
        Vector N = getNormal();

        Vector v = hittingRay.direction.multiply(-1).normalize();
        if(Vector.dot(N, v) < 0.0)
            return N.multiply(-1);
        return N;
    }
}
