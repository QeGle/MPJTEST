import mpi.Comm;
import mpi.MPI;

public class lab4 {
	private static final int[] mass = Utils.initArray(1, (int) Math.pow(10, 7));
	private static int outmsg[];
	private static int inmsg[];
	private static int CPU = 0;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(mass.length);


		MPI.Init(args);

		CPU = MPI.COMM_WORLD.Size();
		Comm comm = MPI.COMM_WORLD;

		//инициализируем массив от 1 до CPU * 2
		outmsg = Utils.initArray(1, CPU * 2);
		inmsg = new int[CPU * 2];
		int[] result = new int[CPU * 2];

		//нулевой процессор
		int head = 0;





		System.out.println(comm.Rank() + " stop.");
		MPI.Finalize();
	}
}