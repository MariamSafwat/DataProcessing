package jupai9.examples;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.*;
import tech.tablesaw.api.Table;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WuzzufJobsSparkPreprocessing {
    public static void main(String[] args) throws IOException {
        Logger.getLogger("org").setLevel(Level.ERROR);

        // Create Spark Session to create connection to Spark
        final SparkSession sparkSession = SparkSession.builder ().appName ("Spark CSV Analysis Demo").master ("local[2]")
                .getOrCreate ();

        // Get DataFrameReader using SparkSession
        final DataFrameReader dataFrameReader = sparkSession.read ();
        // Set header option to true to specify that first row in file contains
        // name of columns
        dataFrameReader.option ("header", "true");
        //1. Read data set and convert it to dataframe or Spark RDD and display some from it.
        final Dataset<Row> csvDataFrame = dataFrameReader.csv ("src/main/resources/Wuzzuf_Jobs.csv");
        Table bushTable = Table.read().csv("src/main/resources/Wuzzuf_Jobs.csv");
        System.out.println(bushTable.structure());

        // Print Schema to see column names, types and other metadata
        csvDataFrame.printSchema ();
        System.out.println("Number of records before data cleaning: " + csvDataFrame.count());
        // Create view and execute query to convert types as, by default, all columns have string types
        csvDataFrame.createOrReplaceTempView ("Wuzzuf_Jobs_Data");
        //3. Clean the data (null, duplications)
        final Dataset<Row> WuzzufJobsData = sparkSession
                .sql ("SELECT DISTINCT cast(Title as String) Title, cast(Company as string) Company, " +
                        "cast(Location as String) Location, " +
                        "cast(Type as String) Type, " +
                        "cast(Level as String) Level, " +
                        "cast(YearsExp as String) YearsExp, " +
                        "cast(Country as String) Country, " +
                        "cast(Skills as String) Skills  FROM Wuzzuf_Jobs_Data WHERE EXISTS (SELECT null FROM Wuzzuf_Jobs_Data)");

        //2. Display structure and summary of the data.
        // Print Schema to see column names, types and other metadata
        System.out.println ("Number of records after data cleaning: " +WuzzufJobsData.count ());
        WuzzufJobsData.show (20);

        //4) Number of jobs in each company displayed in descending order

        WuzzufJobsData.createOrReplaceTempView ("Job_In_Company");
        System.out.println ("4) Number of jobs in each company displayed in descending order");
        final Dataset<Row> JobCompany = sparkSession.sql ("SELECT count(Title) as NumberOfJobs, Company FROM Job_In_Company GROUP BY Company ORDER BY NumberOfJobs DESC ");
        JobCompany.show ();

        List<String> Companies = JobCompany.map(row -> String.valueOf(row.get(1)), Encoders.STRING()).collectAsList();
        List<String> TopCompanies=Companies.subList(0,10);


        List<Long> JobsInCompany = JobCompany.map(row -> (Long) row.get(0), Encoders.LONG()).collectAsList();
        List<Long> TopJobsInCompany =JobsInCompany.subList(0,10);

        //5. Show step 4 in a pie chart
        PieChart02.getChart(Companies,JobsInCompany);
        PieChart02.getChart(TopCompanies,TopJobsInCompany);




        //6. Find out What are it the most popular job titles
        WuzzufJobsData.createOrReplaceTempView ("Popular_Job_Titles");
        System.out.println ("6) Number of job title demand displayed in descending order");
        final Dataset<Row> PopularJobs =sparkSession.sql ("SELECT count(Title) as JobPopularity , Title FROM Popular_Job_Titles GROUP BY Title ORDER BY JobPopularity DESC");
        PopularJobs.show ();

        List<String> Jobs = PopularJobs.map(row -> String.valueOf(row.get(1)), Encoders.STRING()).collectAsList();
        List<String> TopJobs =Jobs.subList(0,10);

        List<Long> JobsFrequency = PopularJobs.map(row -> (Long) row.get(0), Encoders.LONG()).collectAsList();
        List<Long> TopJobsFrequency =JobsFrequency.subList(0,10);
        //7. Show step 6 in bar chart
        BarChart01.getChart(TopJobs,TopJobsFrequency,"Popular Job Titles","Job Titles","Job Frequency");


        //8. Find out the most popular areas
        WuzzufJobsData.createOrReplaceTempView ("Popular_Areas");
        System.out.println ("8) Popular Areas displayed in descending order");
        final Dataset<Row> PopularAreas = sparkSession.sql ("SELECT count(Title) as AreaPopularity, Country FROM Popular_Areas GROUP BY Country ORDER BY AreaPopularity DESC");
        PopularAreas.show();
        List<String> Areas = PopularAreas.map(row -> String.valueOf(row.get(1)), Encoders.STRING()).collectAsList();
        List<String> TopAreas=Areas.subList(0,10);

        List<Long> AreasFreq = PopularAreas.map(row -> (Long) row.get(0), Encoders.LONG()).collectAsList();
        List<Long> TopAreasFreq =AreasFreq.subList(0,10);
        //9. Show step 8 in bar chart
        BarChart01.getChart(TopAreas,TopAreasFreq,"Popular Areas","Areas","Area Frequency");

        //10. Print skills one by one and how many each repeated and order the output to find out the most important skills required
        WuzzufJobsData.createOrReplaceTempView ("Important_Skills");
        System.out.println ("10) Important job skills displayed in descending order");

        final Dataset<Row> PopularSkills = sparkSession.sql ("SELECT Skills FROM Important_Skills");
        List<String> Skills = PopularSkills.map(row -> String.valueOf(row.get(0)), Encoders.STRING()).collectAsList();
        List<String> AllSkills = new ArrayList<>();
        for (int i=0 ; i< Skills.size(); i++){
            List<String> temp = new ArrayList<String>(Arrays.asList(Skills.get(i).split(",")));
            AllSkills.addAll(temp);
        }

        Map<String, Long> counts = AllSkills.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        LinkedHashMap<String, Integer> SortedSkills = new LinkedHashMap<>();

        counts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> SortedSkills.put(x.getKey(), Math.toIntExact(x.getValue())));

        SortedSkills.forEach((skill, counter) -> {
            System.out.println(skill + " => " + counter);
        });

        //11. Factorize the YearsExp feature and convert it to numbers in new col.
        WuzzufJobsData.createOrReplaceTempView ("Years_Of_Experience");
        System.out.println ("11) Factorized Years of experience new column");

        final Dataset<Row> YearsExp1 = sparkSession.sql ("SELECT YearsExp FROM Years_Of_Experience");
        YearsExp1.show();

        List<String> Exp = YearsExp1.map(row -> String.valueOf(row.get(0)), Encoders.STRING()).collectAsList();
        List<String> ModefiedExp = new ArrayList<>();
        for (int i=0 ; i< Exp.size(); i++){
            ModefiedExp.add(Exp.get(i).replace(" Yrs of Exp",""));

        }
        System.out.println(ModefiedExp);



    }
}
