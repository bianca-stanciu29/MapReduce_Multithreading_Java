import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MyReduce implements Callable {
	//file name
	String file;
	//array for the result of map stage
	ArrayList<Future<Result_Map>> futureArrayList = new ArrayList<>();
	//final result
	Result_Reduce result;

	public MyReduce(String file, ArrayList<Future<Result_Map>> futureArrayList) {
		this.file = file;
		this.futureArrayList = futureArrayList;
	}

	//the Fibonacci algorithm-> recursive method
	static int fib (int n) {
		if (n <= 1)
			return n;
		return fib(n - 1) + fib(n - 2);
	}

	@Override
	public Result_Reduce call() throws Exception {
		String fs = "../" + file;
		//the sum for the rank formula
		int sum = 0;
		//initialization the maximum
		int max = -100000;
		//number of words from the file
		int number_w = 0;
		//the position of the word with maximum size
		int number_max = 0;
		//parse the array result after map stage
		for (int i = 0; i < futureArrayList.size(); i++) {
			//when the file the task is working on is equal to the file name in the array result
			if (futureArrayList.get(i).get().file_name.equals(fs)) {
				//parse the keys who are the dimension of words
				for (int keys : futureArrayList.get(i).get().map.keySet()) {
					//find the maximum dimension
					if (keys > max) {
						max = keys;
					}
					//update the number of words
					number_w += futureArrayList.get(i).get().map.get(keys);
					//calculate the sum
					sum += fib(keys + 1) * futureArrayList.get(i).get().map.get(keys);
				}
			}
		}
		//the result of the rank
		float r = (float) sum / number_w;
		//parse the future array and find the maximum number of words
		for (int i = 0; i < futureArrayList.size(); i++) {
			if (futureArrayList.get(i).get().file_name.equals(fs)) {
				for (int keys : futureArrayList.get(i).get().map.keySet()) {
						if (keys == max) {
							number_max += futureArrayList.get(i).get().map.get(keys);
						}
				}
			}
		}
		//put in the result name file, rank, maximum dimension and maximum number of words
		result = new Result_Reduce(fs, r, max, number_max);

		//return the result
		return result;
	}
}
