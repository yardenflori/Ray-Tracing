public class Matrix {
	double[][] matrix;
	int height, width;
	
	public Matrix(double[][] matrix) {
		this.matrix = matrix;
		this.height = this.matrix.length;
		this.width = this.matrix[0].length;
	}
	
	public Matrix(int height, int width) {
		this.matrix = new double[height][height];
		this.height = height;
		this.width = width;
	}
	
	public Matrix(int size) {
		this(size, size);
	}
	
	public static Matrix multiply(Matrix m1, Matrix m2) {
		if(m1.width != m2.height) {
			System.out.println("Matrixs's size do not feet to this operation!");
			System.exit(1);
		}
		
		Matrix m = new Matrix(m1.height, m2.width);
		for(int i = 0; i < m.height; i++) {
			for(int j = 0; j < m.width; j++) {
				for(int c = 0; c < m1.width; c++) {
					m.matrix[i][j] += m1.matrix[i][c] * m2.matrix[c][j];
				}
			}
		}
		return m;
	}
	
	public Vector solve(Vector x) {
		if(this.width < x.length) {
			System.out.println("Can't multiply matrix and vector!");
			System.exit(2);
		}
		
		x = x.copy(this.width);
		double[] res = new double[this.height];
		for(int i = 0; i < res.length; i++) {
			for(int c = 0; c < x.length; c++) {
				res[i] += this.matrix[i][c] * x.coords[c];
			}
		}
		
		return new Vector(res);
	}
	
}
