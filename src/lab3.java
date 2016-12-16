import mpi.Comm;
import mpi.MPI;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.dense.BasicVector;

import java.util.Arrays;

public class lab3 {
	public static void main(String[] args) throws InterruptedException {

		MPI.Init(args);

		int rank= MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		Comm comm = MPI.COMM_WORLD;

		//нулевой процессор
		int head = 0;

		if (comm.Rank()==head){

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

			//количество строк на 1 ядро
			long n = Math.round((double)BV.length()/size);

			//посылаем вектор
			for (int i = 1; i < size; i++) {
				System.out.println(rank + " send vector to " + i);
				comm.Send(BV.toArray(),0,BV.length()-1,MPI.DOUBLE,i,i);
			}

			//производим рассылку
			for (int i = 1; i < size; i++) {
				long count[] = new long[1];
				count[0] = n;
				//сначала рассылаем n
				comm.Send(count,0,1,MPI.LONG,i,i);

				//теперь рассылаем части матрицы
				for (int j = 1; j < n+1; j++) {
					System.out.println(rank + " send to " +  i +" row#"+j*i);
					Vector row = AM.getRow(j*i);
					//отсылаем i*j строку в матрице
					comm.Send(row.toDenseVector().toArray(),0,row.length()-1,MPI.DOUBLE,i,i);
				}
			}
			double[] res = new double[AM.columns()-(int)n*size];

			//Считаем остатки на нулевом проце
			for (int i = 0; i < AM.columns()-(int)n*size; i++) {
				res[i] = BV.multiply(AM.getRow(i+(int)n*size).toColumnMatrix()).sum();
			}
			System.out.println("On 0 ="+Arrays.toString(res));


			//Получаем обратно посчитанные результаты
			for (int i = 1; i < size; i++) {
				int l = comm.Probe(0,rank).Get_elements(MPI.DOUBLE);
				System.out.println(l);
				double[] x = new double[l];
				comm.Recv(x,0,x.length,MPI.DOUBLE,0,i);
				System.out.println("On 0 ="+Arrays.toString(x));
			}

		}else{
			int l = comm.Probe(0,rank).Get_elements(MPI.DOUBLE);
			double[] B = new double[l];
			//получаем вектор
			comm.Recv(B,0,B.length,MPI.DOUBLE,0,rank);
			Vector BV = Vector.fromArray(B);


			//получаем количество элементов
			long[] n = new long[1];
			comm.Recv(n,0,1,MPI.LONG,0,rank);

			double[] res = new double[(int)n[0]];
			for (long i = 0; i < n[0]; i++) {
				double[] row = new double[l];
				comm.Recv(row,0,row.length,MPI.DOUBLE,0,rank);
				Vector AV = Vector.fromArray(row);
				res[(int)i]= AV.multiply(BV.toColumnMatrix()).sum();
			}
			System.out.println("On " + rank + " = " + Arrays.toString(res));
			//отправляем результаты вычислений на нулевой
			comm.Ssend(res,0,res.length,MPI.DOUBLE,0,rank);

		}




		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}
}