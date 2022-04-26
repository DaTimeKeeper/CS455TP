package TP;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class App {
  public static class CustomReducer
      extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    public void reduce(Text key, Iterable<DoubleWritable> values,
        Context context) throws IOException, InterruptedException {
      double result = 0;
      for (DoubleWritable val : values) {
        result += val.get();
      }
      DoubleWritable dw = new DoubleWritable();
      dw.set(result);
      context.write(key, dw);
    }
  }
  public static class PowerPlantMapper
      extends Mapper<Object, Text, Text, DoubleWritable> {

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

      StringTokenizer itr = new StringTokenizer(value.toString(), ",");
      String fields[] = new String[3];
      fields[0] = "";
      fields[1] = "";
      fields[2] = "";
      int i = 0;
      while (itr.hasMoreTokens()) {
        String x = itr.nextToken();
        fields[i] = x;
        i++;
      }
      Text stateT = new Text();
      stateT.set(fields[1]);
      DoubleWritable dw = new DoubleWritable(1);
      if(Integer.parseInt(fields[2].substring(0, 4))>=2016){
        dw = new DoubleWritable(0);
      }
      context.write(stateT, dw);
    }
  }
  public static class TlinesMapper
      extends Mapper<Object, Text, Text, DoubleWritable> {

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString(), ",");
      String fields[] = new String[4];
      fields[0] = "";
      fields[1] = "";
      fields[2] = "";
      fields[3] = "";
      int i = 0;
      while (itr.hasMoreTokens()) {
        String x = itr.nextToken();
        fields[i] = x;
        i++;
      }
      Text stateT = new Text();
      stateT.set(fields[1]);
      DoubleWritable dw = new DoubleWritable(Double.parseDouble(fields[2]));
      if(Integer.parseInt(fields[3].substring(0, 4))>=2016){
        dw = new DoubleWritable(0);
      }
      context.write(stateT, dw);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Power");
    job.setJarByClass(App.class);
    String q = args[0];
    switch (q) {
      case "P1":
      job.setMapperClass(PowerPlantMapper.class);
      job.setReducerClass(CustomReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(DoubleWritable.class);
        break;
      case "P2":
      job.setMapperClass(TlinesMapper.class);
      job.setReducerClass(CustomReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(DoubleWritable.class);
        break;
      default:
        break;
    }

    FileInputFormat.addInputPath(job, new Path(args[1]));
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
