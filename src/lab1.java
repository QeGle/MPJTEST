import mpi.Comm;
import mpi.MPI;

public class lab1 {

	public static void main(String[] args) throws InterruptedException {

		MPI.Init(args);

		//записываем количество процессоров/частей на которые будет делиться массив
		int size = MPI.COMM_WORLD.Size();
		int rank = MPI.COMM_WORLD.Rank();

		//нулевой процессор
		int head = 0;
		Comm comm = MPI.COMM_WORLD;
		long sum[] = new long[1];
		//на нулевом
		if (comm.Rank() == head) {
			System.out.println("I'm " + head + " CPU. Current number of CPU`s: " + size);
			//инициализируем массив
			int[] outmsg = Utils.initArray(1, (int) Math.pow(10, 7));
			//int[] outmsg = Utils.initArray(1, 20);
			//берем его длину
			int msgLength = outmsg.length;
			//считаем кол-во символов в одной части
			int symbInPartMsg = (msgLength / size);

			//рассылаем части массива и считаем оставшуюся часть
			for (int i = 1; i < size; i++) {

				//отправляем части остальным процессорам. Все, кроме последней.
				comm.Send(outmsg, (i - 1) * symbInPartMsg, symbInPartMsg, MPI.INT, i, i);
				//System.out.println(head + " send to " + i);

				//считаем сумму в последней части
				if (i == 1)
					for (int j = symbInPartMsg * (size - 1); j < msgLength; j++) {
						sum[0] += outmsg[j];
					}
			}

			//выводим сумму в консоль
			System.out.println("Sum on 0 " + sum[0] + " end " + head + ".");

		} else {
			//смотрим кол-во получаемых элементов
			int inMsgLength = comm.Probe(0, MPI.ANY_TAG).Get_elements(MPI.INT);
			//создаем массив такого объема
			int[] inMsg = new int[inMsgLength];

			//получаем сообщение
			comm.Recv(inMsg, 0, inMsgLength, MPI.INT, 0, MPI.ANY_TAG);

			//считаем сумму
			sum[0] = 0L;
			for (int anInMsg : inMsg) {
				sum[0] += anInMsg;
			}

			//выводим сумму в консоль
			System.out.println("Sum on " + rank + " " + sum[0] + " end " + rank + ".");
		}


		//ТОЛЬКО ЕСЛИ CPU - 2 В КАКОЙ-ЛИБО СТЕПЕНИ
		//Math.log(CPU)/Math.log(2) - логарифм CPU по основанию 2 - необходимое кол-во передач
		for (int step = 0; step < (int) (Math.log(size) / Math.log(2)); step++) {
			for (int i = (int) Math.pow(2, step); i < size; i += (int) Math.pow(2, step+1)) {
				if (comm.Rank() == i) {
					comm.Ssend(sum, 0, 1, MPI.LONG, i - (int) Math.pow(2, step), 0);
					System.out.println(i + " send  " + sum[0] + " to " + (i - (int) Math.pow(2, step)));
				}
				if (comm.Rank() == i - (int) Math.pow(2, step)) {
					long[] res = new long[1];
					comm.Recv(res, 0, 1, MPI.LONG, i, 0);
					System.out.println((i - (int) Math.pow(2, step)) + " receive " + res[0] + " from " + i+" Sum = " +sum[0]+"+"+res[0]+"="+(sum[0]+res[0]));
					sum[0] += res[0];
				}
			}
		}

		if (rank == 0)
			System.out.println("total " + sum[0]);
		//сообщаем, что ядро закончило работу
		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}
}