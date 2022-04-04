import java.util.ArrayList;
import java.util.Map;

/**
 * class for the result of map stage
 */
public class Result_Map {
	String file_name;
	Map<Integer, Integer> map;
	ArrayList<String> words_max_dim;

	public Result_Map(String file_name, Map<Integer, Integer> map, ArrayList<String> words_max_dim) {
		this.file_name = file_name;
		this.map = map;
		this.words_max_dim = words_max_dim;
	}
}
