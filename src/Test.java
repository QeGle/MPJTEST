import org.la4j.Matrix;
import org.la4j.vector.DenseVector;
import org.la4j.vector.dense.BasicVector;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
	/*	double[] B = new double[5];

		//Инициализируем вектор B
		for (int i = 0; i < 5; i++) {
			B[i] = (i+1)*5;
			System.out.println(B[i]);
		}
		double[] A = new double[5];

		//Инициализируем вектор B
		for (int i = 0; i < 5; i++) {
			A[i] = i+1;
			System.out.println(A[i]);
		}

		Vector BV = Vector.fromArray(B);


		Vector AV = Vector.fromArray(A);
*/
		double[][] A = new double[5][5];
		double[] B = new double[5];
		//инициализируем массив A
		for (int i = 1; i < 6; i++) {
			for (int j = 1; j < 6; j++) {
				A[i-1][j-1]=i*j;
				System.out.print(i*j+" ");
			}
			System.out.println();
		}
		System.out.println("-----------");
		//Инициализируем вектор B
		System.arraycopy(A[0],0,B,0,5);
		for (int i = 0; i < 5; i++) {
			System.out.println(B[i]);
		}


		Matrix AM = Matrix.from2DArray(A);
		BasicVector BV = BasicVector.fromArray(B);
DenseVector DV = (DenseVector) AM.multiply(BV);
for (int i = 0; i < AM.columns(); i++) {
			System.out.println(DV.get(i));

		}


	}
}
