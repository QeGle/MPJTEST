import mpi.MPI;
import org.la4j.Matrix;

import java.io.IOException;
import java.util.Arrays;

/**
 * Вычисление произведения матриц
 * при помощи библиотеки MPJ Express
 * <br>
 * Mahaev Sergey
 * 15:54 25/12/2016
 */

public class lab4 {

	public static void main(String[] args) throws IOException {
		MPI.Init(args);
		int ind;
		int temp;
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int rows[] = new int[1];
		String fileName = "C:\\Users\\user\\Desktop\\Учеба\\ФОМ\\MPJTEST\\src\\file.txt";

		//забираем размер матрицы
		if (rank == 0) {
			rows[0] = Utils.getRowsCount(fileName);
		}
		//рассылаем размер матрицы
		MPI.COMM_WORLD.Bcast(rows, 0, 1, MPI.INT, 0);

		//присваем его не массиву, чтобы не возиться со скобочками
		int rowsCount = rows[0];

		//Задаем размеры массивам
		int[] A = new int[rowsCount * rowsCount];
		int[] B = new int[rowsCount * rowsCount];
		int[] C = new int[rowsCount * rowsCount];

		//инициализируем массив
		if (rank == 0) {

			//Сделано через матрицы, так как есть код для считывания
			Matrix AM = Utils.getMatrixFromFile(fileName, rowsCount, 1);
			Matrix BM = Utils.getMatrixFromFile(fileName, rowsCount, 2 + rowsCount);
			BM = BM.transpose();

			//Забиваем данные уже в наш массив
			for (int i = 0; i < rowsCount; i++) {
				for (int j = 0; j < rowsCount; j++) {
					A[i * rowsCount + j] = (int) AM.get(i, j);
					B[i * rowsCount + j] = (int) BM.get(i, j);
				}
			}
		}


		//количество строк на проц
		int rowsOnProc = rowsCount / size;

		//количество элементов на проц
		int elementsOnProc = rowsOnProc * rowsCount;

		//создаем промежуточные массивы
		int[] bufA = new int[elementsOnProc];
		int[] bufB = new int[elementsOnProc];
		int[] bufC = new int[elementsOnProc];


		//рассылаем части массивов по процам
		//Таким образом у каждого проца свои, полностью заполненные bufA u bufB
		MPI.COMM_WORLD.Scatter(A, 0, elementsOnProc, MPI.INT, bufA, 0, elementsOnProc, MPI.INT, 0);
		MPI.COMM_WORLD.Scatter(B, 0, elementsOnProc, MPI.INT, bufB, 0, elementsOnProc, MPI.INT, 0);

		//вычисляем элементы диагонали матрицы
		//вычисляются в зависимости от количества строк на проц
		//если строк больше, чем одна - не совсем диагональ получается
		//Соответственоо на этом этапе bufC остается почти пустым на каждом проце
		temp = 0;
		for (int i = 0; i < rowsOnProc; i++) {
			for (int j = 0; j < rowsOnProc; j++) {
				for (int k = 0; k < rowsCount; k++)
					temp += bufA[i * rowsCount + k] * bufB[j * rowsCount + k];
				bufC[i * rowsCount + j + rowsOnProc * rank] = temp;
				temp = 0;
			}
		}

		System.out.println(rank + "bufC " + Arrays.toString(bufC));


		//считаем все оставшиеся элементы
		int nextProc;
		int prevProc;
		for (int p = 1; p < size; p++) {
			nextProc = rank + 1;
			if (rank == size - 1)
				nextProc = 0;
			prevProc = rank - 1;
			if (rank == 0)
				prevProc = size - 1;
			//обмениваемся элементами из bufВ
			MPI.COMM_WORLD.Sendrecv_replace(bufB, 0, elementsOnProc, MPI.INT, nextProc, 0, prevProc, 0);
			temp = 0;
			//вычисляем аналогично диагональным,
			//только со сдвигом в зависимости от номера проца
			for (int i = 0; i < rowsOnProc; i++) {
				for (int j = 0; j < rowsOnProc; j++) {
					for (int k = 0; k < rowsCount; k++) {
						temp += bufA[i * rowsCount + k] * bufB[j * rowsCount + k];
					}
					if (rank - p >= 0)
						ind = rank - p;
					else ind = (size - p + rank);
					bufC[i * rowsCount + j + ind * rowsOnProc] = temp;
					temp = 0;
				}
			}
		}

		if (rank==0)
			System.out.println("--------");
		MPI.COMM_WORLD.Barrier();
		System.out.println(rank + "bufC " + Arrays.toString(bufC));

		//объединяем все результаты в один массив С
		MPI.COMM_WORLD.Gather(bufC, 0, elementsOnProc, MPI.INT, C, 0, elementsOnProc, MPI.INT, 0);


		//выводим все
		if (MPI.COMM_WORLD.Rank() == 0)
			for (int i = 0; i < C.length; i++) {
				System.out.print(C[i] + " ");
				if ((i + 1) % rowsCount == 0)
					System.out.println("\n");
			}
		MPI.Finalize();
	}
}