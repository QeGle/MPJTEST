import mpi.Comm;
import mpi.MPI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

public class lab2 {

	public static void main(String[] args) throws InterruptedException, IOException {

		MPI.Init(args);
		Comm comm = MPI.COMM_WORLD;

		System.out.println(comm.Rank() + " start");
		//записываем количество процессоров/частей
		int size = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank();
		//нулевой процессор
		int head = 0;
		double pi[] = new double[1];
		pi[0] = 0;
		int[] accuracy = new int[1];

		//на нулевом
		if (comm.Rank() == head) {
			System.out.println("I'm " + head + " CPU. Current number of CPU`s: " + size);

			//считываем с файла количество знаков после запятой
			String str;
			long n;
			try (BufferedReader reader = new BufferedReader(
					new FileReader("C:\\Users\\user\\Desktop\\ФОМ\\MPJTEST\\src\\file.txt"))) {
				str = reader.readLine();
				reader.close();
			}
			accuracy[0] = Integer.parseInt(str);
			System.out.println("Accuracy = " + accuracy[0]);


			//считаем сколько вычислений будет приходится на 1 ядро
			n = Math.round((double) accuracy[0] / size);

			//отпарвляем ядрам/процам количество выпавших на их долю итераций
			//считаем pi

			//если точность 1
			if ((accuracy[0] != 1)) {
				//если процов/ядер больше, чем чисел после запятой
				if (n != 0) {
					long[] mass = new long[1];
					mass[0] = n;
					for (int i = 1; i < size; i++) {
						System.out.println(rank + " sent to " + i + ": " + mass[0]);
						comm.Send(mass, 0, 1, MPI.LONG, i, i);
					}
					pi[0] = getPi(n * (size - 1), accuracy[0]);
					System.out.println("On " + rank + " pi=" + pi[0]);

				} else
					for (int i = 1; i < accuracy[0]; i++) {
						long[] mass = new long[1];
						mass[0] = i;
						System.out.println(rank + " sent to " + i + ": " + mass[0]);
						comm.Send(mass, 0, 1, MPI.LONG, i, i);
					}
			}else{
				pi[0]=getPi(0,1);
				System.out.println("Pi with accuracy=" + accuracy[0] + "  = " + new BigDecimal(pi[0]).setScale(accuracy[0], BigDecimal.ROUND_HALF_DOWN));
				System.exit(0);
			}
		} else {
			System.out.println(comm.Rank() + " ready to receive");

			long[] inMsg = new long[1];

			//получаем сообщение
			comm.Recv(inMsg, 0, 1, MPI.LONG, 0, MPI.ANY_TAG);

			//считаем pi
			pi[0] = getPi(inMsg[0] * (rank - 1), inMsg[0] * rank);
			System.out.println("On " + rank + " pi=" + pi[0]);
		}


		//ТОЛЬКО ЕСЛИ size - 2 В КАКОЙ-ЛИБО СТЕПЕНИ
		//Math.log(CPU)/Math.log(2) - логарифм size по основанию 2 - необходимое кол-во передач
		for (int step = 0; step < (int) (Math.log(size) / Math.log(2)); step++) {
			for (int i = (int) Math.pow(2, step); i < size; i += (int) Math.pow(2, step + 1)) {
				if (comm.Rank() == i) {
					comm.Ssend(pi, 0, 1, MPI.DOUBLE, i - (int) Math.pow(2, step), 0);
					System.out.println(i + " send  " + pi[0] + " to " + (i - (int) Math.pow(2, step)));
				}
				if (comm.Rank() == i - (int) Math.pow(2, step)) {
					double[] res = new double[1];
					comm.Recv(res, 0, 1, MPI.DOUBLE, i, 0);
					System.out.println((i - (int) Math.pow(2, step)) + " receive " + res[0] + " from " + i);
					pi[0] += res[0];
				}
			}
		}

		if (rank == 0)
			System.out.println("Pi with accuracy=" + accuracy[0] + "  = " + new BigDecimal(pi[0]).setScale(accuracy[0], BigDecimal.ROUND_HALF_DOWN));
		//сообщаем, что ядро закончило работу
		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}



	static double getPi(long start, long stop) {
		double pi = 0;
		for (long i = start; i < stop; i++) {
			pi += setPi(i);
		}
		return pi;
	}

	private static double setPi(long itter){
		double pi = 0;
		double a = (double) 1 / Math.pow(16, itter);
		double b = (double) 4 / (8 * itter + 1);
		double c = (double) 2 / (8 * itter + 4);
		double d = (double) 1 / (8 * itter + 5);
		double e = (double) 1 / (8 * itter + 6);
		pi += a * (b - d - c - e);
		return pi;
	}
}