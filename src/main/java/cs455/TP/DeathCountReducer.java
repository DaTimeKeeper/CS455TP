import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.util.TreeMap;
import java.util.Map;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
    Receives: <StateWeek, Deaths>
    Sums up weekly death for each key (day of week)
    Returns total deaths for week as <StateWeek, Deaths> 
*/

public class DeathCountReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        // since there are many different causes of death, need to sum them up for each week
        double sum = 0;
        for(IntWritable val : values){
            sum += val.get();
        }
        context.write(new Text(key.toString()), new DoubleWritable(sum));
    }
}