package DataManipulationFunctions;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.IntStream;

/*Insert Record
* Insert Column
* Display Columns
* Sort
* Delete Record
* Delete Columns
* Undo
* Display CSV file
* Update Record
* Update Column
* */

public class DataManipulationFunctions {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;
        while (continueProgram) {
            System.out.println("Choose an operation:");
            System.out.println("1. Insert Record");
            System.out.println("2. Insert Column");
            System.out.println("3. Display Column Names");
            System.out.println("4. Sort Data");
            System.out.println("5. Delete Record");
            System.out.println("6. Delete Column");
            System.out.println("7. Undo");
            System.out.println("8. Display CSV");
            System.out.println("9. Update Record");
            System.out.println("10. Update Column");
            System.out.println("0. Exit");
            try {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        insertRecord();
                        break;
                    case 2:
                        insertColumn();
                        break;
                    case 3:
                        displayColumnNames();
                        break;
                    case 4:
                        sortData();
                        break;
                    case 5:
                        deleteRecord();
                        break;
                    case 6:
                        deleteColumn();
                        break;
                    case 7:
                        undo();
                        break;
                    case 8:
                        displayCSV();
                        break;
                    case 9:
                        updateRecord();
                        break;
                    case 10:
                        updateColumn();
                        break;
                    case 0:
                        System.out.println("Exiting program. Goodbye!");
                        continueProgram = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
                if (continueProgram) {
                    char continueChoice;
                    do {
                        System.out.print("Want to continue? (Y/N): ");
                        continueChoice = scanner.next().toUpperCase().charAt(0);
                        scanner.nextLine();

                        if (continueChoice != 'Y' && continueChoice != 'N') {
                            System.out.println("Invalid response. Please enter 'Y' or 'N'.");
                        }
                    } while (continueChoice != 'Y' && continueChoice != 'N');

                    continueProgram = continueChoice == 'Y';
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer choice.");
                scanner.nextLine();
            }
        }
    }
    private static final String CSV_FILE_PATH = "Downloaded_Files/CSV_Cliq.csv";
    private static Stack<UndoEntry> undoStack = new Stack<>();
    private static class UndoEntry {
        private String operationType;
        private List<String[]> state;
        public UndoEntry(String operationType, List<String[]> state) {
            this.operationType = operationType;
            this.state = state;
        }
        public String getOperationType() {
            return operationType;
        }
        public List<String[]> getState() {
            return state;
        }
    }
    public static void insertRecord() {
        try {
            saveState("Record Inserted");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            String[] header = records.get(0);
            String[] newRecord = new String[header.length];
            Scanner scanner = new Scanner(System.in);
            int numRecords;
            do {
                System.out.print("Enter the number of records to insert: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of records to insert: ");
                    scanner.next();
                }
                numRecords = scanner.nextInt();
                scanner.nextLine();
                if (numRecords < 0) {
                    System.out.println("Value must be a positive integer.");
                } else if (numRecords == 0) {
                    System.out.println("No records have been inserted.");
                    return;
                }
            } while (numRecords < 0);
            for (int j = 0; j < numRecords; j++) {
                System.out.println("Record " + (j + 1) + ":");
                for (int i = 0; i < header.length; i++) {
                    System.out.print(header[i] + ": ");
                    newRecord[i] = scanner.nextLine();
                }
                records.add(newRecord.clone());
            }
            csvReader.close();
            FileWriter fileWriter = new FileWriter(CSV_FILE_PATH);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeAll(records);
            csvWriter.close();
            fileWriter.close();
            System.out.println("Record(s) inserted successfully!");
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }




    public static void insertColumn() {
        try {
            saveState("Column Inserted");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            csvReader.close();
            Scanner scanner = new Scanner(System.in);
            int numColumns;
            do {

                System.out.print("Enter the number of columns to insert: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of columns to insert: ");
                    scanner.next();
                }
                numColumns = scanner.nextInt();
                scanner.nextLine();

                if (numColumns < 0) {
                    System.out.println("Value must be a positive integer.");
                } else if (numColumns == 0) {
                    System.out.println("No columns have been inserted.");
                    return;
                }
            } while (numColumns < 0);

            for (int j = 0; j < numColumns; j++) {
                boolean columnInserted = false;
                while (!columnInserted) {
                    System.out.print("Enter the name of the new column: ");
                    String newColumnName = scanner.nextLine();
                    System.out.println("Existing Column Names:");
                    for (String columnName : records.get(0)) {
                        System.out.println(columnName);
                    }
                    String referenceColumnName;
                    int referenceColumnIndex;
                    do {
                        System.out.print("Enter the name of the reference column: ");
                        referenceColumnName = scanner.nextLine();
                        String finalReferenceColumnName = referenceColumnName;
                        referenceColumnIndex = IntStream.range(0, records.get(0).length)
                                .filter(i -> records.get(0)[i].equalsIgnoreCase(finalReferenceColumnName))
                                .findFirst()
                                .orElse(-1);
                        if (referenceColumnIndex == -1) {
                            System.out.println("Reference column not found. Please enter a valid reference column name.");
                        }
                    } while (referenceColumnIndex == -1);
                    String[] header = records.get(0);
                    String[] newHeader = new String[header.length + 1];
                    System.arraycopy(header, 0, newHeader, 0, header.length);
                    newHeader[header.length] = newColumnName;
                    records.set(0, newHeader);
                    for (int i = 1; i < records.size(); i++) {
                        String referenceValue = records.get(i)[referenceColumnIndex];
                        System.out.print("Enter value for " + newColumnName + " in row " + i + " (based on " + referenceColumnName + " = " + referenceValue + "): ");
                        String columnValue = scanner.nextLine();
                        String[] record = records.get(i);
                        String[] newRecord = new String[record.length + 1];
                        System.arraycopy(record, 0, newRecord, 0, record.length);
                        newRecord[record.length] = columnValue;
                        records.set(i, newRecord);
                    }
                    columnInserted = true;
                }
            }
            writeCSV(records);
            System.out.println("Column(s) inserted successfully!");
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }


    public static void displayColumnNames() {
        try {
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] header = csvReader.readNext();
            System.out.println("Column Names:");
            for (String columnName : header) {
                System.out.println(columnName);
            }
            csvReader.close();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    public static void sortData() {
        Scanner scanner = new Scanner(System.in);
        displayColumnNames();
        System.out.print("Enter the name of the column to sort: ");
        String columnName = scanner.nextLine();
        try {
            saveState("Sorted");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            int columnIndex = IntStream.range(0, records.get(0).length)
                    .filter(i -> records.get(0)[i].equalsIgnoreCase(columnName))
                    .findFirst()
                    .orElse(-1);
            if (columnIndex != -1) {
                Comparator<String[]> comparator = Comparator.comparing(record -> record[columnIndex]);
                Collections.sort(records.subList(1, records.size()), comparator);
                writeCSV(records);
                System.out.println("Data sorted successfully!");
            } else {
                System.out.println("Column not found. Please enter a valid column name.");
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    public static void deleteRecord() {
        try {
            saveState("Record Deleted");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            Scanner scanner = new Scanner(System.in);
            int numRecordsToDelete;
            do {
                System.out.print("Enter the number of records to delete: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of records to delete: ");
                    scanner.next();
                }
                numRecordsToDelete = scanner.nextInt();
                scanner.nextLine();
                if (numRecordsToDelete < 0) {
                    System.out.println("Value must be a positive integer.");
                } else if (numRecordsToDelete == 0) {
                    System.out.println("No records have been deleted.");
                    return;
                }
            } while (numRecordsToDelete < 0);
            int columnIndexToDelete = -1;
            displayColumnNames();
            while (columnIndexToDelete == -1) {
                System.out.print("Enter the name of the column to delete records from: ");
                String columnNameToDelete = scanner.nextLine();
                String[] header = records.get(0);
                for (int i = 0; i < header.length; i++) {
                    if (header[i].equalsIgnoreCase(columnNameToDelete)) {
                        columnIndexToDelete = i;
                        break;
                    }
                }
                if (columnIndexToDelete == -1) {
                    System.out.println("Field " + "'" + columnNameToDelete + "'" + " is not found. Please enter a valid Column Name.");
                }
            }
            int deletedCount = 0;
            while (deletedCount < numRecordsToDelete) {
                System.out.print("Enter the value in the specified column to delete record: ");
                String columnValueToDelete = scanner.nextLine();
                boolean recordDeleted = false;
                for (int i = 1; i < records.size(); i++) {
                    if (records.get(i)[columnIndexToDelete].equalsIgnoreCase(columnValueToDelete)) {
                        records.remove(i);
                        i--;
                        deletedCount++;
                        recordDeleted = true;
                        break;
                    }
                }
                if (!recordDeleted) {
                    System.out.println("No records found with the specified value in the column.");
                }
            }
            writeCSV(records);
            System.out.println("Record(s) deleted successfully!");
            csvReader.close();
            fileReader.close();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }




    public static void deleteColumn() {
        try {
            saveState("Column(s) Deleted");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            Scanner scanner = new Scanner(System.in);
            int numColumnsToDelete;
            do {
                System.out.print("Enter the number of columns to delete: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of columns to delete: ");
                    scanner.next();
                }
                numColumnsToDelete = scanner.nextInt();
                scanner.nextLine();
                if (numColumnsToDelete < 0 ) {
                    System.out.println("Value must be a positive integer.");
                } else if (numColumnsToDelete == 0) {
                    System.out.println("No columns have been deleted.");
                    return;
                }
            } while (numColumnsToDelete < 0);
            displayColumnNames();
            for (int j = 0; j < numColumnsToDelete; j++) {
                boolean columnDeleted = false;
                while (!columnDeleted) {
                    System.out.print("Enter the name of the column to delete: ");
                    String columnToDelete = scanner.nextLine();
                    int columnIndexToDelete = -1;
                    String[] header = records.get(0);
                    for (int i = 0; i < header.length; i++) {
                        if (header[i].equalsIgnoreCase(columnToDelete)) {
                            columnIndexToDelete = i;
                            break;
                        }
                    }
                    if (columnIndexToDelete != -1) {
                        String[] updatedHeader = removeElement(header, columnIndexToDelete);
                        List<String[]> updatedRecords = new ArrayList<>();
                        updatedRecords.add(updatedHeader);
                        for (int i = 1; i < records.size(); i++) {
                            String[] record = removeElement(records.get(i), columnIndexToDelete);
                            updatedRecords.add(record);
                        }
                        records = updatedRecords;
                        columnDeleted = true;
                    } else {
                        System.out.println("Column '" + columnToDelete + "' not found. Please enter a valid column name.");
                    }
                }
            }
            writeCSV(records);
            System.out.println("Column(s) deleted successfully!");
            csvReader.close();
            fileReader.close();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }



    public static void undo() {
        if (!undoStack.isEmpty()) {
            UndoEntry undoEntry = undoStack.pop();
            writeCSV(undoEntry.getState());
            System.out.println("Undo successful for " + undoEntry.getOperationType());
        } else {
            System.out.println("No changes to undo.");
        }
    }

    private static void saveState(String operationType) {
        try {
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> currentState = csvReader.readAll();
            csvReader.close();
            fileReader.close();
            undoStack.push(new UndoEntry(operationType, new ArrayList<>(currentState)));
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private static void writeCSV(List<String[]> data) {
        try {
            FileWriter fileWriter = new FileWriter(CSV_FILE_PATH);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeAll(data);
            csvWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void displayCSV() {
        try {
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            String[] header = records.get(0);
            for (String columnName : header) {
                System.out.printf("%-20s", columnName);
            }
            System.out.println();
            for (int i = 0; i < header.length * 20; i++) {
                System.out.print("-");
            }
            System.out.println();
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                for (String value : record) {
                    System.out.printf("%-20s", value);
                }
                System.out.println();
            }
            csvReader.close();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }


    public static void updateRecord() {
        try {
            saveState("Record Updated");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            csvReader.close();
            Scanner scanner = new Scanner(System.in);
            int numRecordsToUpdate;
            do {
                System.out.print("Enter the number of records to update: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of records to update: ");
                    scanner.next();
                }
                numRecordsToUpdate = scanner.nextInt();
                scanner.nextLine();
                if (numRecordsToUpdate < 0) {
                    System.out.println("Value must be a positive integer.");
                } else if (numRecordsToUpdate == 0) {
                    System.out.println("No records have been updated.");
                    return;
                }
            } while (numRecordsToUpdate < 0);
            displayColumnNames();
            System.out.print("Enter the name of the column to update a record: ");
            String columnNameToUpdate = scanner.nextLine();
            int columnIndexToUpdate = -1;
            String[] header = records.get(0);
            for (int i = 0; i < header.length; i++) {
                if (header[i].equalsIgnoreCase(columnNameToUpdate)) {
                    columnIndexToUpdate = i;
                    break;
                }
            }
            if (columnIndexToUpdate != -1) {
                for (int j = 0; j < numRecordsToUpdate; j++) {
                    System.out.print("Enter the value in the specified column to update record: ");
                    String recordValueToUpdate = scanner.nextLine();
                    boolean recordFound = false;
                    for (int i = 1; i < records.size(); i++) {
                        if (records.get(i)[columnIndexToUpdate].equalsIgnoreCase(recordValueToUpdate)) {
                            System.out.println("Enter new values for the record:");
                            String[] record = records.get(i);
                            for (int k = 0; k < record.length; k++) {
                                System.out.print(header[k] + ": ");
                                record[k] = scanner.nextLine();
                            }
                            recordFound = true;
                            break;
                        }
                    }
                    if (!recordFound) {
                        System.out.println("No records found with the specified value in the column.");
                    }
                }
                writeCSV(records);
                System.out.println("Record(s) updated successfully!");
            } else {
                System.out.println("Field '" + columnNameToUpdate + "' is not found. Please enter a valid Column Name.");
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public static void updateColumn() {
        try {
            saveState("Column(s) Updated");
            FileReader fileReader = new FileReader(CSV_FILE_PATH);
            CSVReader csvReader = new CSVReader(fileReader);
            List<String[]> records = csvReader.readAll();
            csvReader.close();
            Scanner scanner = new Scanner(System.in);
            int numColumnsToUpdate;
            do {
                System.out.print("Enter the number of columns to update: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    System.out.print("Enter the number of columns to update: ");
                    scanner.next();
                }
                numColumnsToUpdate = scanner.nextInt();
                scanner.nextLine();

                if (numColumnsToUpdate < 0) {
                    System.out.println("Value must be a positive integer.");
                } else if (numColumnsToUpdate == 0) {
                    System.out.println("No columns have been updated.");
                    return;
                }
            } while (numColumnsToUpdate < 0);
            for (int j = 0; j < numColumnsToUpdate; j++) {
                boolean columnUpdated = false;
                while (!columnUpdated) {
                    System.out.print("Enter the name of the column to update: ");
                    String columnToUpdate = scanner.nextLine();
                    int columnIndexToUpdate = IntStream.range(0, records.get(0).length)
                            .filter(i -> records.get(0)[i].equalsIgnoreCase(columnToUpdate))
                            .findFirst()
                            .orElse(-1);
                    if (columnIndexToUpdate != -1) {
                        System.out.println("Existing Column Names:");
                        for (String columnName : records.get(0)) {
                            System.out.println(columnName);
                        }
                        String referenceColumnName;
                        int referenceColumnIndex;
                        do {
                            System.out.print("Enter the name of the reference column: ");
                            referenceColumnName = scanner.nextLine();
                            String finalReferenceColumnName = referenceColumnName;
                            referenceColumnIndex = IntStream.range(0, records.get(0).length)
                                    .filter(i -> records.get(0)[i].equalsIgnoreCase(finalReferenceColumnName))
                                    .findFirst()
                                    .orElse(-1);
                            if (referenceColumnIndex == -1) {
                                System.out.println("Reference column not found. Please enter a valid reference column name.");
                            }
                        } while (referenceColumnIndex == -1);
                        for (int i = 1; i < records.size(); i++) {
                            String referenceValue = records.get(i)[referenceColumnIndex];
                            System.out.print("Enter new value for " + columnToUpdate + " in row " + i + " (based on " + referenceColumnName + " = " + referenceValue + "): ");
                            String newColumnValue = scanner.nextLine();
                            records.get(i)[columnIndexToUpdate] = newColumnValue;
                        }
                        columnUpdated = true;
                    } else {
                        System.out.println("Column to update not found. Please enter a valid column name.");
                    }
                }
            }
            writeCSV(records);
            System.out.println("Column(s) updated successfully!");
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }



    private static String[] removeElement(String[] array, int index) {
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        return newArray;
    }

    private static List<String[]> readCsvFile() throws IOException, CsvException {
        FileReader fileReader = new FileReader(CSV_FILE_PATH);
        CSVReader csvReader = new CSVReader(fileReader);
        List<String[]> records = csvReader.readAll();
        csvReader.close();
        return records;
    }

    private static void writeCsvFile(List<String[]> records) throws IOException {
        FileWriter fileWriter = new FileWriter(CSV_FILE_PATH);
        CSVWriter csvWriter = new CSVWriter(fileWriter);
        csvWriter.writeAll(records);
        csvWriter.close();
        fileWriter.close();
    }



}
