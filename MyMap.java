import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MyMap implements Callable {
	int offset_start;
	int offset_end;
	File f;
	Map<Integer, Integer> map;
	Result_Map result_map;
	ArrayList<String> words_max_dim = new ArrayList<>();

	public MyMap(int offset_start, int offset_end, File f, Map<Integer, Integer> map) {
		this.offset_start = offset_start;
		this.offset_end = offset_end;
		this.f = (File) f;
		this.map = new HashMap<>();
	}

	@Override
	public Result_Map call() throws Exception {
		//the string for the delimitators in the file
		String delimitators = " ;:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"|\t\r\n";
		//for read the contents of the sent file as a parameter
		RandomAccessFile file = null;
		StringBuilder s = new StringBuilder();
		try {
			file = new RandomAccessFile(f, "r");
			/*
			if the offset end is not at the end of the text and the next character and
			the current character are the letter, increase the offset size
			 */
			if (offset_end != f.length() - 1) {
				file.seek(offset_end + 1);
				int c1 = file.read();
				if (delimitators.indexOf((char)c1) == -1) {
					file.seek(offset_end);
					int c2 = file.read();
					if (delimitators.indexOf((char)c2) == -1) {
						while (delimitators.indexOf((char)c2) == -1) {
							offset_end++;
							if (offset_end >= f.length() - 1) {
								break;
							}
							file.seek(offset_end);
							c2 = file.read();

						}
					}
				}
			}
			/*
			if the offset start is not at the beginning of the text and the current character and
			 the character of before are the letter, increase the offset size
			 */
			if (offset_start != 0) {
				file.seek(offset_start - 1);
				int s1 = file.read();
				if (delimitators.indexOf(s1) == -1) {
					file.seek(offset_start);
					int s2 = file.read();
					if (delimitators.indexOf(s2) == -1) {
						file.seek(offset_start);
						int s3 = file.read();
						while (delimitators.indexOf(s3) == -1) {
							offset_start++;
							file.seek(offset_start);
							if (offset_start >= f.length() - 1)
								break;
							s3 = file.read();
						}
					}
				}
			}
			//repositioning at the starting position
			file.seek(offset_start);
			//read character from the start and end
			for (int i = offset_start; i <= offset_end; i++) {
				int b = file.read();

				if (b != -1 && offset_start != offset_end) {
					s.append((char)b);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//split the new part of the text
		String[] words = s.toString().split("[^A-Za-z0-9]");
		//initialization the maximum
		int max = -1000;
		//parse the words array and find the maximum
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > max)
				max = words[i].length();
		}
		//after find the maximum, put the word from the maximum position
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() == max) {
				words_max_dim.add(words[i]);
			}
		}
		//parse the words array and put in the map length of word and number of appearances
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() != 0) {
				if (map.containsKey(words[i].length())) {
					map.put(words[i].length(), map.get(words[i].length()) + 1);
				} else {
					map.put(words[i].length(), 1);
				}
			}
		}
		//put in the result_map file name, the map and the maximum word
		result_map = new Result_Map(f.toString(), map, words_max_dim);
		//return the result
		return result_map;
	}
}
