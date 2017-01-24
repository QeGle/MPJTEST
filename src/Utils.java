import org.la4j.Matrix;
import org.la4j.Vector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

	public static void main(String[] args) throws IOException {
		Matrix A = getMatrixFromFile("C:\\Users\\user\\Desktop\\ФОМ\\MPJTEST\\src\\file.txt", 5,2);


		for (int i = 0; i < A.rows(); i++) {
			System.out.println(A.getRow(i));
		}
	}


	/**
	 * Инициализируем массив числами от start до end включительно.
	 *
	 * @param start число в массиве под индексом 0
	 * @param end   последнее число в массиве. Индекс - end-1
	 * @return возвращает массив int[end-1], где элементы = {start..end}
	 */
	static int[] initArray(int start, int end) {
		start = start - 1;
		int mass = end - start;
		int returnMassive[] = new int[mass];
		for (int i = 0; i < returnMassive.length; i++) {
			returnMassive[i] = i + start + 1;
		}
		return returnMassive;
	}

	/**
	 * Инициализируем массив числами от start до end включительно.
	 *
	 * @param start число в массиве под индексом 0
	 * @param end   последнее число в массиве. Индекс - end-1
	 * @return возвращает массив int[end-1], где элементы = {start..end}
	 */
	static long[] initArray(long start, long end) {
		start = start - 1;
		long mass = end - start;
		long returnMassive[] = new long[(int) mass];
		for (int i = 0; i < returnMassive.length; i++) {
			returnMassive[i] = i + start + 1;
		}
		return returnMassive;
	}

	/**
	 * Инициализируем массив числами от start до end включительно.
	 *
	 * @param start число в массиве под индексом 0
	 * @param end   последнее число в массиве.
	 * @param step  шаг, через который идут числа в массивее
	 * @return возвращает массив int[end-1], где элементы = {start+(0*step), start+(1*step)..end}
	 */
	static int[] initArray(int start, int end, int step) {
		//+1 - чтобы элемент end также включался в массив.
		int returnMassive[] = new int[(end / step) + 1];
		for (int i = 0; i < returnMassive.length; i++) {
			returnMassive[i] = start + (i * step);
		}
		return returnMassive;
	}

	static int getRowsCount(String fileName) throws IOException {
		int count = 0;
		try (BufferedReader reader = new BufferedReader(
				new FileReader(fileName))) {
			count = Integer.parseInt(reader.readLine());
			reader.close();
		}

		return count;
	}

	static int getColumnsCount(String fileName) throws IOException {
		int count = 0;
		try (BufferedReader reader = new BufferedReader(
				new FileReader(fileName))) {
			//т.к. мы считываем файл заново
			//при первом чтении мы брали кол-во строк
			reader.readLine();
			count = Integer.parseInt(reader.readLine());
			reader.close();
		}

		return count;
	}

	static Vector getVectorFromFile(String fileName, int row, int before) throws IOException {
		String str;
		String strArr[];
		double[] A = new double[row];
		try (BufferedReader reader = new BufferedReader(
				new FileReader(fileName))) {
			for (int i = 0; i < before; i++) {
				reader.readLine();
			}
			for (int i = 0; i < row; i++) {
				A[i] = Integer.parseInt(reader.readLine());

			}
			reader.close();
		}

		return Vector.fromArray(A);


	}

	static Matrix getMatrixFromFile(String fileName, int row, int before) throws IOException {
		String str;
		String strArr[];
		double[][] A = new double[row][row];
		try (BufferedReader reader = new BufferedReader(
				new FileReader(fileName))) {
			for (int i = 0; i < before; i++) {
				reader.readLine();
			}
			for (int i = 0; i < row; i++) {
				str = reader.readLine();
				strArr = str.split(" ");
				for (int j = 0; j < strArr.length; j++) {
					A[i][j] = Integer.parseInt(strArr[j]);
				}
			}
			reader.close();
		}
		return Matrix.from2DArray(A);
	}
}

