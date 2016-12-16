public class Utils {


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
		int returnMassive[] = new int[(end/step)+1];
		for (int i = 0; i < returnMassive.length; i++) {
			returnMassive[i] = start+(i * step);
		}
		return returnMassive;
	}
}

