import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FantoirMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

    private final static LongWritable ONE = new LongWritable(1);
    private Text codeInsee = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        
        // Supposons que le fichier FANTOIR est délimité par des tabulations
        String[] columns = value.toString().split("\t");
        
        // Extraire le Code INSEE (première colonne)
        String inseeCode = columns[0];
        
        // Émettre (Code INSEE, 1)
        codeInsee.set(inseeCode);
        context.write(codeInsee, ONE);
    }
}
