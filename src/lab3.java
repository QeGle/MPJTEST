import mpi.Comm;
import mpi.MPI;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.DenseVector;

import java.io.IOException;
import java.util.Arrays;

public class lab3 {
	public static void main(String[] args) throws InterruptedException, IOException {

		MPI.Init(args);

		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		Comm comm = MPI.COMM_WORLD;

		//нулевой процессор
		int head = 0;
		String fileName = "C:\\Users\\user\\Desktop\\ФОМ\\MPJTEST\\src\\file.txt";

		if (comm.Rank() == head) {
			int N = Utils.getRowsCount(fileName);
			int col = Utils.getColumnsCount(fileName);
			double[][] A = new double[N][col];
			double[] B = new double[col];
			//считываем Матрицу
			//2- дважды вызывался методы, которые считывали инфу с файла.
			// Каждый метод читал по 1 символу
			Matrix AM = Utils.getMatrixFromFile(fileName, N, 2);

			//Считываем вектор
			DenseVector BV = Utils.getVectorFromFile(fileName,col,AM.rows()+2).toDenseVector();

			//мотрим что приняли. Вектор, матрица
			System.out.println("-----------");
			for (int i = 0; i < BV.length(); i++) {
				System.out.println(Arrays.toString(BV.toArray()));
			}
			System.out.println("-----------");
			for (int i = 0; i < AM.rows(); i++) {
				System.out.println(Arrays.toString(AM.getRow(i).toDenseVector().toArray()));
			}
			System.out.println("-----------");

			//количество строк на 1 ядро
			long n = Math.round((double) BV.length() / size);
			System.out.println("n=" + n);
			//посылаем вектор
			for (int i = 1; i < size; i++) {
				System.out.println(rank + " send vector to " + i);
				comm.Send(BV.toArray(), 0, BV.length(), MPI.DOUBLE, i, i);
			}

			//производим рассылку
			for (int i = 1; i < size; i++) {
				long count[] = new long[1];
				count[0] = n;
				//сначала рассылаем n
				comm.Send(count, 0, 1, MPI.LONG, i, i);


				//теперь рассылаем части матрицы
				for (int j = 0; j < n; j++) {
					System.out.println("j=" + j);
					System.out.println(rank + " send to " + i + " row#" + (n * (i - 1) + j));
					Vector row = AM.getRow((int) (n * (i - 1) + j));

					//отсылаем i*j строку в матрице
					comm.Send(row.toDenseVector().toArray(), 0, row.length(), MPI.DOUBLE, i, i);
				}

			}
			double[] res = new double[N];

			//Считаем остатки на нулевом проце
			for (int i = 0; i < AM.columns() - (int) n * (size - 1); i++) {
				res[i + (int) n * (size - 1)] = AM.getRow(i + (int) n * (size - 1)).multiply(BV.toColumnMatrix()).sum();
			}
			System.out.println("On 0 = " + Arrays.toString(res));


			//Получаем обратно посчитанные результаты
			for (int i = 1; i < size; i++) {
				int l = comm.Probe(i, i).Get_elements(MPI.DOUBLE);
				System.out.println(l);
				double[] sRow = new double[l];
				System.out.println("test");
				comm.Recv(sRow, 0, sRow.length, MPI.DOUBLE, MPI.ANY_SOURCE, i);
				System.out.println("res 0 = " + Arrays.toString(sRow));
				for (int j = 0; j < l; j++) {
					res[j + (i - 1)] = sRow[j];
				}
				System.out.println("Result" + Arrays.toString(res));
			}

		} else {
			int l = comm.Probe(0, rank).Get_elements(MPI.DOUBLE);
			double[] B = new double[l];
			//получаем вектор
			comm.Recv(B, 0, B.length, MPI.DOUBLE, 0, rank);
			Vector BV = Vector.fromArray(B);

			//получаем количество элементов
			long[] n = new long[1];
			comm.Recv(n, 0, 1, MPI.LONG, 0, rank);

			double[] res = new double[(int) n[0]];
			for (long i = 0; i < n[0]; i++) {
				double[] row = new double[l];
				comm.Recv(row, 0, row.length, MPI.DOUBLE, 0, rank);
				Vector AV = Vector.fromArray(row);
				res[(int) i] = AV.multiply(BV.toColumnMatrix()).sum();
			}
			System.out.println("On " + rank + " = " + Arrays.toString(res));

			//отправляем результаты вычислений на нулевой
			comm.Ssend(res, 0, res.length, MPI.DOUBLE, 0, rank);
			System.out.println(comm.Rank() + " sent to 0 " + Arrays.toString(res));
		}

		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}
}