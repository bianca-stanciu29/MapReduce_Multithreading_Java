/**
 * class for the result of the reduce stage
 */
public class Result_Reduce {
	String file_name;
	float rang;
	Integer dim_max;
	Integer pos_word_max;

	public Result_Reduce(String file_name, float rang, Integer dim_max, Integer pos_word_max) {
		this.file_name = file_name;
		this.rang = rang;
		this.dim_max = dim_max;
		this.pos_word_max = pos_word_max;
	}
}
