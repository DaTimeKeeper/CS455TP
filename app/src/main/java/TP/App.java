package TP;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.eclipse.jetty.util.ArrayUtil;

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
  public static class AvgDayReduce extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException{
        long pmSum = 0;
        int numScores = 0;

        for(DoubleWritable pm : values){
            pmSum += pm.get();
            numScores++;
        }

        DoubleWritable pmAvg = new DoubleWritable((double)pmSum / numScores);
        context.write(key, pmAvg);
    }
  }
  public static class AvgDayMap extends Mapper<Object, Text, Text, DoubleWritable> {
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
      // String[] elements = value.toString().split(",");

      // String date = elements[1].trim();
      // String fips = elements[2].trim();
      StringTokenizer itr = new StringTokenizer(value.toString(), ",");
      String fields[] = new String[9];
      fields[0] = "";
      fields[1] = "";
      fields[2] = "";
      fields[3] = "";
      fields[4] = "";
      fields[5] = "";
      fields[6] = "";
      fields[7] = "";
      fields[8] = "";
      int i = 0;
      while (itr.hasMoreTokens()) {
        String x = itr.nextToken();
        fields[i] = x;
        fields[i].trim();
        i++;
      }
      DoubleWritable pmConcentrate = new DoubleWritable(Double.parseDouble(fields[7]));
      Text stateDate= new Text(""+fields[1] + ":" + fields[2]);
      context.write(stateDate, pmConcentrate);
    }
  }
  public static class AvgWeekMap extends Mapper<Object, Text, Text, DoubleWritable> {
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
      // String[] elements = value.toString().split(",");

      // String date = elements[1].trim();
      // String fips = elements[2].trim();
      int days [] = { 31, 29, 31, 30, 31, 30,
        31, 31, 30, 31, 30, 31 };
      String months[] = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        List<String> states = Arrays.asList("AL","AK","","AZ","AR","CA","","CO","CT","DE","","FL","GA","","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","","RI","SC","SD","TN","TX","UT","VT","VA","","WA","WV","WI","WY");
      StringTokenizer itr = new StringTokenizer(value.toString(), "\t");
      String fields[] = new String[2];
      fields[0] = "";
      fields[1] = "";
      
      int i = 0;
      while (itr.hasMoreTokens()) {
        String x = itr.nextToken();
        fields[i] = x;
        fields[i].trim();
        i++;
      }
      String date=fields[0].substring(0, 9);
      String monthS = date.substring(2, 5);
      System.out.println(monthS);
      int month=Arrays.asList(months).indexOf(monthS);
      int day = Integer.parseInt(date.substring(0,2));
        // Add the days in the previous months
      while (month > 0)
      {
        day = day + days[month - 1];
        month--;
      }
      System.out.println(day);
      day-=2;
      if(day>0){
        int week = (int) Math.floor(day/7);
        DoubleWritable pmConcentrate = new DoubleWritable(Double.parseDouble(fields[1]));
        Text stateDate= new Text(""+states.get((Integer.parseInt(fields[0].substring(10)))-1)+":"+week);
        context.write(stateDate, pmConcentrate);
      }
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

  public class DeathCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
        String input = value.toString();
        String[] lineSplit = input.split(",");
        String state = lineSplit[3];
        int year = Integer.parseInt(lineSplit[4]);
        int week = Integer.parseInt(lineSplit[5]);
        int deaths = Integer.parseInt(lineSplit[7]);

        if(state.equals("YC")){
            state = "NY";
        }

        if(year == 2016){
            if(!state.equals("PR")){
                String stateWeek = state + "," + week;
                context.write(new Text(stateWeek), new IntWritable(deaths));
            }
        }
    }
  }

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
      case "AvgDay":
      job.setMapperClass(AvgDayMap.class);
      job.setReducerClass(AvgDayReduce.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(DoubleWritable.class);
      case "AvgWeek":
      job.setMapperClass(AvgWeekMap.class);
      job.setReducerClass(AvgDayReduce.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(DoubleWritable.class);
        break;
      case "Death":
      job.setMapperClass(DeathCountMapper.class);
      job.setReducerClass(DeathCountReducer.class);
      job.setMapOutputKeyClass(Text.class);
      job.setMapOutputValueClass(IntWritable.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputKeyClass(DoubleWritable.class);
      default:
        break;
    }

    FileInputFormat.addInputPath(job, new Path(args[1]));
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
