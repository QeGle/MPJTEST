import mpi.Comm;
import mpi.MPI;

public class Main {
	private static final int[] mass = Utils.initArray(1, (int) Math.pow(10, 7));

	public static void main(String[] args) throws InterruptedException {
		System.out.println(mass.length);


		MPI.Init(args);

		int CPU = MPI.COMM_WORLD.Size();

		//нулевой процессор
		int head = 0;
		Comm comm = MPI.COMM_WORLD;

		//инициализируем массив от 1 до (CPU-1) * 2
		int[] outmsg = Utils.initArray(1, (CPU - 1) * 2);
		int[] inmsg = new int[(CPU - 1) * 2];
		int[] result = new int[(CPU -1) * 2];

		//на нулевом
		if (comm.Rank() == head) {
			System.out.println("I'm " + head + " CPU. Current number of CPU`s: " + CPU);

			//рассылаем части сообщения
			for (int i = 1; i < CPU; i++) {
				comm.Send(outmsg, (i-1)*2, 2, MPI.INT, i, i);
			}

			//собираем результаты
			int res = 0;
			for (int i = 1; i < CPU; i++) {
				comm.Recv(result, i, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				System.out.println("I'm " + head + " CPU. Receive: " + result[i] + ". "
						+ result[i] + "+" + res + "=" + (res + result[i]));
				res += result[i];
			}

			//выводим результат
			System.out.println("---I'm " + head + " CPU. Sum: " + res);
		}

		//вычисляем сумму и передаем результат 0 процу
		int[] recv = new int[CPU];
		for (int i = 1; i < CPU; i++) {
			if (comm.Rank() == i) {
				comm.Recv(inmsg,i, 2, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				recv[i] = inmsg[i] + inmsg[i + 1];
				System.out.println("I'm " + i + " CPU. Receive&Send: " + inmsg[i] + "+"
						+ inmsg[i + 1] + "=" + recv[i] + "\n");
				comm.Ssend(recv, i, 1, MPI.INT, head, 0);
				System.out.println("I'm " + i + " CPU. Send: " + recv[i]);
			}
		}

		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}
}
