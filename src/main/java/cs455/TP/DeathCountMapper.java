import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import java.util.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
    Mapper: Read each line of csv data
    Grab State, Week Number and Number of Deaths
    Returns: <StateWeek, Deaths>
*/ 

public class DeathCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
        String input = value.toString();
        String[] lineSplit = input.split(",");
        String state = lineSplit[3];
        int year = Integer.parseInt(lineSplit[4]);
        int week = Integer.parseInt(lineSplit[5]);
        int deaths = Integer.parseInt(lineSplit[7]);


        // Do I need to filter by year?
        if(year == 2016){
            String stateWeek = state + "," + week;
            context.write(new Text(stateWeek), new IntWritable(deaths));
        }
    }
}