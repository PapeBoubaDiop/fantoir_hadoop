package com.fantoir.mapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class FantoirDriver {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Fantoir Voies par Commune");

        job.setJarByClass(FantoirDriver.class);
        job.setMapperClass(FantoirMapper.class);
        job.setReducerClass(FantoirReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Répertoire d'entrée
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Répertoire de sortie

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
