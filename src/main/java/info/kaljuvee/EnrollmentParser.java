package info.kaljuvee;

import info.kaljuvee.domain.EnrollmentRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Enrollment file parser that reads files from master enrollment files and allows writing the records to separate files
 * for each insurance company.  The records will be sorted by last and the last name, keeping only the latest version of
 * each user record.  The input file is assumed to have a header record and the header names are assumed to have a
 * specific set of column names defined by constants.
 *
 * @author Oliver Kaljuvee
 */
public class EnrollmentParser {
    private final static Logger log = Logger.getLogger(EnrollmentParser.class.getName());
    private final static String USERID_COL_KEY = "user_id";
    private final static String NAME_COL_KEY = "name";
    private final static String VERSION_COL_KEY = "version";
    private final static String INSURANCE_COL_KEY = "insurance_company";

    private String inputFile;
    private String outputDir;
    private String[] header;
    private Collection<EnrollmentRecord> records;

    /**
     * Constructor for the parser.
     *
     * @param inputFile Input file which could be a file name with absolute or relative path.
     * @param outputDir Output directory ending with "/" which will contain file for each insurance company.
     */
    public EnrollmentParser(String inputFile, String outputDir) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
        this.header = new String[] {USERID_COL_KEY, NAME_COL_KEY, VERSION_COL_KEY, INSURANCE_COL_KEY};
        records = new LinkedList<>();
    }

    /**
     * Parses input file and stores to internal collection of records.
     */
    public void parseInput() {
        parseInput(CSVFormat.DEFAULT);
    }

    /**
     * Parses input file and stores to internal collection of records.
     *
     * @param format Specifies the input file format.
     */
    public void parseInput(CSVFormat format) {
        Iterable<CSVRecord> csv = null;

        try {
            csv = format.withHeader(header).withFirstRecordAsHeader().parse(new FileReader(inputFile));
        } catch(FileNotFoundException e) {
            log.info(String.format("Input file %s not found", inputFile));
            e.printStackTrace();
        } catch(IOException e) {
            log.info("General IO exception occurred while reading the input file");
            e.printStackTrace();
        }
        csv.iterator().forEachRemaining(r -> records.add(constructRecord(r)));
        log.info(String.format("Parsed %s records", records.size()));
    }

    /**
     * Writes the output file for each insurance company.
     */
    public void writeOutput()  {
        if(records.isEmpty()) {
            log.info("No records to write");
            return;
        }
        // Construct a map keyed by insurance company name to map of user identifiers to versioned user records
        Map<String, Map<String, Queue<EnrollmentRecord>>> recordMap = constructRecordMap();

        for(Map.Entry<String, Map<String, Queue<EnrollmentRecord>>> entry : recordMap.entrySet()) {
            Map<String, Queue<EnrollmentRecord>> insuranceCompanyMap = entry.getValue();
            // Collect the final list of records for each insurance company
            List<EnrollmentRecord> result = new LinkedList<>();

            for(Queue<EnrollmentRecord> versions : insuranceCompanyMap.values()) {
                // Only add the head of the queue to the result, which will be the record with the highest version
                result.add(versions.poll());
            }
            Collections.sort(result);
            writeInsuranceFile(entry.getKey(), result);
        }
    }

    /**
     * Writes list of records to file named by insurance company name.
     *
     * @param insuranceCompany Insurance company name for which the file will be written for
     * @param records Records to be written for the give insurance company
     */
    private void writeInsuranceFile(String insuranceCompany, List<EnrollmentRecord> records) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
        String fileName = getFileName(insuranceCompany);
        log.info(String.format("--------- Writing file for: %s ---------", insuranceCompany));

        try {
            fileWriter = new FileWriter(outputDir + fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            csvFilePrinter.printRecord(Arrays.asList(header));

            for(EnrollmentRecord er : records) {
                csvFilePrinter.printRecord(er.getRecordLine());
                log.info(er.toString());
            }
        } catch (Exception e) {
            log.info("Error while while writing the CSV output");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                log.info("Error while closing IO resources");
                e.printStackTrace();
            }
        }
        log.info("Finished writing file: " + fileName);
    }

    /**
     * Constructs a map of records from the original list of input records.  The map is keyed by company name. The
     * value is map keyed by user identifier, which will have a value of records represented by a priority queue.  The
     * priority queue is prioritized by version (see the compareTo implementation of the record for reference).
     *
     * @return Map of user records keyed by insurance company name.
     */
    private Map<String, Map<String, Queue<EnrollmentRecord>>> constructRecordMap() {
        Map<String, Map<String, Queue<EnrollmentRecord>>> recordMap = new HashMap<>();

        for(EnrollmentRecord er : records) {
            Map<String, Queue<EnrollmentRecord>> enrolleeMap =
                    recordMap.getOrDefault(er.getInsuranceCompany(), new HashMap<>());
            Queue<EnrollmentRecord> collection = enrolleeMap.getOrDefault(er.getUserId(), new PriorityQueue<>());
            collection.offer(er);
            enrolleeMap.put(er.getUserId(), collection);
            recordMap.put(er.getInsuranceCompany(), enrolleeMap);
        }
        return recordMap;
    }

    /**
     * Constructs a model object from the CSV line item.
     *
     * @param csvRecord CSV line item represented by a record
     * @return Model representation of enrollment record.
     */
    private EnrollmentRecord constructRecord(CSVRecord csvRecord) {
        return new EnrollmentRecord(csvRecord.get(USERID_COL_KEY),
                Integer.parseInt(csvRecord.get(VERSION_COL_KEY)),
                csvRecord.get(NAME_COL_KEY),
                csvRecord.get(INSURANCE_COL_KEY));
    }

    /**
     * Returns output filename based on insurance name.  Converts the insurance name to all lower case characters,
     * removing all duplicate spaces and dashes.
     *
     * @param name Insurance company name
     * @return Output CSV file name
     */
    private String getFileName(String name) {
        String result = name.toLowerCase().replaceAll(" ", "-") + ".csv";
        result = result.replaceAll("(\\.)\\1+", "$1");
        result = result.replaceAll("(\\-)\\1+", "$1");
        return result;
    }

    private static final String RESOURCE_DIR = "src/main/resources/";

    /**
     * Main program for the parser.
     *
     * @param args Program arguments containing the input file name and output directory.  Examples:
     *             > java EnrollerParser enrollments.csv output/
     *             > java EnrollerParser C:/tmp/enrollments.csv C:/tmp/
     */
    public static void main(String... args) {
        if(args.length != 2) {
            log.info("Please provide arguments for input file name and output directory. Exiting.");
            System.exit(-1);
        }
        String inputFile = args[0];
        String outputDir = args[1];

        // If no directory separators are used, we assume the input file is in resources/ folder of the project
        if(!inputFile.contains("/")) {
            inputFile = RESOURCE_DIR + inputFile;
        }
        log.info("Parsing file: " + inputFile);
        EnrollmentParser parser = new EnrollmentParser(inputFile, outputDir);
        parser.parseInput();
        parser.writeOutput();
        log.info("Complete");
    }
}
