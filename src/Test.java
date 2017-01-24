import org.la4j.Matrix;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {

		String fileName = "C:\\Users\\user\\Desktop\\Учеба\\ФОМ\\MPJTEST\\src\\file.txt";

		int n = Utils.getRowsCount(fileName);

			Matrix AM = Utils.getMatrixFromFile(fileName, n, 1);
			Matrix BM = Utils.getMatrixFromFile(fileName, n, 2 + n);

			AM=AM.multiply(BM);

		for (int i = 0; i < AM.rows(); i++) {
			System.out.println(AM.getRow(i));
		}








	}
}
