import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Tema2 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        //read number of workers
        int workers = Integer.parseInt(args[0]);
        //read the path of input file
        String file_in = args[1];
        //read the path of the output file
        String file_out = args[2];


        File file = new File(file_in);
        Scanner sc = new Scanner(file);

        int fragment_size = 0;
        int number_doc;
        //vector of strings in which retain the path of the number_doc file
        String[] text = new String[0];
        //line of the text
        int line = 0;

        while (sc.hasNextLine()) {
            //for line 0, extract the fragment size
            //for line 1, extract the number of the documents
            //for line >= 2, read the path for the documents
           if (line == 0) {
               fragment_size = Integer.parseInt(sc.nextLine());
               line++;
           } else if (line == 1) {
               number_doc = Integer.parseInt(sc.nextLine());
               line++;
               text = new String[number_doc];
           } else {
               text[line - 2] = sc.nextLine();
               line++;
           }
        }

        //instantiating an Executor Service with the number of workers
        ExecutorService tpe = Executors.newFixedThreadPool(workers);
        Map<Integer, Integer> map = new HashMap<Integer,  Integer>();
        //the result after Map operation
        ArrayList<Future<Result_Map>> futureArrayList = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            File f = new File("../" + text[i]);
            //initialization offset_start and offset_end
            int offset_start = 0;
            int offset_end = fragment_size - 1;
            //as long as are portions equals to the fragment size
            while (f.length() - offset_end > fragment_size - 1) {
                //return the result of the Callable task
                Future<Result_Map> future =  tpe.submit(new MyMap(offset_start, offset_end, f, map));
                //add the result in the array of result
                futureArrayList.add(future);
                //update the offset_start and offset_end
                offset_start = offset_end + 1;
                offset_end += fragment_size;
            }

            if (f.length() - offset_end < fragment_size - 1) {
                //return the result of the Callable task
                Future<Result_Map> future1 = tpe.submit(new MyMap(offset_start, offset_end, f, map));
                //add the result in the array of result
                futureArrayList.add(future1);
                //update the offset_start and offset_end
                offset_start = offset_end + 1;
                offset_end = (int) (f.length() - 1);
                //return the result of the Callable task
                Future<Result_Map> future2 = tpe.submit(new MyMap(offset_start, offset_end, f, map));
                //add the result in the arrau of the result
                futureArrayList.add(future2);
            }
        }
        //stop the ExecutorService from receiving new tasks
        tpe.shutdown();

        //instantiating an Executor Service with the number of files
        ExecutorService tpe_reduce = Executors.newFixedThreadPool(text.length);
        //the array for the result of the Reduce stage
        ArrayList<Future<Result_Reduce>> futureResult = new ArrayList<>();
        //parce the file name
        for (int i = 0; i < text.length; i++) {
            //return the result of the Callable task
            Future<Result_Reduce> future_reduce = tpe_reduce.submit(new MyReduce(text[i], futureArrayList));
            //add the result in the array of result
            futureResult.add(future_reduce);
        }
        //stop the ExecutorService from receiving new tasks
        tpe_reduce.shutdown();
        //sort the result after rang
        Collections.sort(futureResult, new Comparator<Future<Result_Reduce>>() {
            @Override
            public int compare(Future<Result_Reduce> o1, Future<Result_Reduce> o2) {
                try {
                    if (o1.get().rang > o2.get().rang) {
                        return -1;
                    } else {
                        return 1;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        //write in the output file the result
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_out));
        for (int i = 0; i < futureResult.size(); i++) {
            String[] fis = futureResult.get(i).get().file_name.split("/");
            //file name
            writer.append(fis[fis.length - 1]);
            writer.append(',');
            //the rang with two decimal
            writer.append(String.format("%.2f", futureResult.get(i).get().rang));
            writer.append(',');
            //the maximum word size in the file
            writer.append(futureResult.get(i).get().dim_max.toString());
            writer.append(',');
            //the number of the maximum number of words
            writer.append(futureResult.get(i).get().pos_word_max.toString());
            writer.append("\n");
        }
        //close the output file
        writer.close();
    }
}
