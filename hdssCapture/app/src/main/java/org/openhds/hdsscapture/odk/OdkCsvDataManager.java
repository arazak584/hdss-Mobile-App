package org.openhds.hdsscapture.odk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.openhds.hdsscapture.entity.Individual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OdkCsvDataManager {
    private static final String TAG = "OdkCsvDataManager";
    private static final String CSV_DIR_NAME = "odk_csv_data";
    private static final String DEFAULT_CSV_NAME = "individuals_data.csv";

    // ODK directory paths where CSV files should be placed for pulldata() access
    private static final String[] ODK_CSV_PATHS = {
            "odk/forms",                                           // Standard ODK location
            "Android/data/org.odk.collect.android/files/forms",   // Scoped storage location
            "odk/forms-db",                                        // Alternative location
            "Android/data/org.odk.collect.android/files/projects/forms", // Multi-project location
            "odk/media"                                            // Media files location
    };

    private Context context;

    public OdkCsvDataManager(Context context) {
        this.context = context;
    }

    /**
     * Create CSV file for a single individual
     */
    public CsvCreationResult createIndividualCsv(Individual individual, String csvFileName) {
        List<Individual> individuals = new ArrayList<>();
        individuals.add(individual);
        return createIndividualsCsv(individuals, csvFileName);
    }

    /**
     * Create CSV file for multiple individuals
     */
    public CsvCreationResult createIndividualsCsv(List<Individual> individuals, String csvFileName) {
        if (individuals == null || individuals.isEmpty()) {
            Log.e(TAG, "No individuals provided for CSV creation");
            return new CsvCreationResult(false, "No individuals provided", null, null);
        }

        // Validate and prepare CSV filename
        String finalCsvName = prepareCsvFileName(csvFileName);

        try {
            // Create CSV content
            String csvContent = generateCsvContent(individuals);

            // Create CSV in app's directory
            String appCsvPath = createCsvInAppDirectory(finalCsvName, csvContent);
            if (appCsvPath == null) {
                return new CsvCreationResult(false, "Failed to create CSV in app directory", null, null);
            }

            // Copy to ODK directories
            List<String> odkCsvPaths = copyToOdkDirectories(appCsvPath, finalCsvName);

            Log.d(TAG, "Successfully created CSV for " + individuals.size() + " individuals");
            Log.d(TAG, "App CSV path: " + appCsvPath);
            Log.d(TAG, "ODK CSV paths: " + odkCsvPaths.size() + " locations");

            return new CsvCreationResult(true, "CSV created successfully", appCsvPath, odkCsvPaths);

        } catch (Exception e) {
            Log.e(TAG, "Error creating CSV file", e);
            return new CsvCreationResult(false, "Error: " + e.getMessage(), null, null);
        }
    }

    /**
     * Create CSV for individual with form-specific name
     */
    public CsvCreationResult createIndividualCsvForForm(Individual individual, OdkForm form) {
        String csvName = (form != null && form.getCsv() != null && !form.getCsv().isEmpty())
                ? form.getCsv()
                : DEFAULT_CSV_NAME;

        return createIndividualCsv(individual, csvName);
    }

    /**
     * Update existing CSV file with new individual data
     */
    public CsvCreationResult updateCsvWithIndividual(String csvFileName, Individual individual) {
        try {
            // Read existing individuals from CSV
            List<Individual> existingIndividuals = readIndividualsFromCsv(csvFileName);

            // Remove existing individual with same extId if present
            existingIndividuals.removeIf(ind -> ind.getExtId().equals(individual.getExtId()));

            // Add the new/updated individual
            existingIndividuals.add(individual);

            // Recreate CSV with updated data
            return createIndividualsCsv(existingIndividuals, csvFileName);

        } catch (Exception e) {
            Log.e(TAG, "Error updating CSV file", e);
            return new CsvCreationResult(false, "Update failed: " + e.getMessage(), null, null);
        }
    }

    /**
     * Generate CSV content from individuals list
     */
    private String generateCsvContent(List<Individual> individuals) {
        StringBuilder csvContent = new StringBuilder();

        // CSV Header
        csvContent.append("permid,firstname,lastname,gender,dob,village,hhid,compno,individual_uuid,interview_date,created_at\n");

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // CSV Data rows
        for (Individual individual : individuals) {
            csvContent.append(escapeCsv(individual.getExtId())).append(",");
            csvContent.append(escapeCsv(individual.getFirstName())).append(",");
            csvContent.append(escapeCsv(individual.getLastName())).append(",");
            csvContent.append(escapeCsv(individual.getGender() != null ? individual.getGender().toString() : "")).append(",");
            csvContent.append(escapeCsv(individual.getDob() != null ? individual.getDob() : "")).append(",");
            csvContent.append(escapeCsv(individual.getVillage() != null ? individual.getVillage() : "")).append(",");
            csvContent.append(escapeCsv(individual.getHohID() != null ? individual.getHohID() : "")).append(",");
            csvContent.append(escapeCsv(individual.getCompno() != null ? individual.getCompno() : "")).append(",");
            csvContent.append(escapeCsv(individual.getUuid() != null ? individual.getUuid() : "")).append(",");
            csvContent.append(currentDate).append(",");
            csvContent.append(currentDateTime).append("\n");
        }

        return csvContent.toString();
    }

    /**
     * Create CSV file in app's directory
     */
    private String createCsvInAppDirectory(String csvFileName, String csvContent) {
        try {
            File csvDir = new File(context.getExternalFilesDir(null), CSV_DIR_NAME);
            if (!csvDir.exists() && !csvDir.mkdirs()) {
                Log.e(TAG, "Failed to create CSV directory: " + csvDir.getAbsolutePath());
                return null;
            }

            File csvFile = new File(csvDir, csvFileName);

            FileWriter writer = new FileWriter(csvFile);
            writer.write(csvContent);
            writer.close();

            Log.d(TAG, "Created CSV file: " + csvFile.getAbsolutePath());
            return csvFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error creating CSV in app directory", e);
            return null;
        }
    }

    /**
     * Copy CSV file to all accessible ODK directories
     */
    private List<String> copyToOdkDirectories(String sourceCsvPath, String csvFileName) {
        List<String> successfulPaths = new ArrayList<>();
        File sourceFile = new File(sourceCsvPath);

        if (!sourceFile.exists()) {
            Log.e(TAG, "Source CSV file does not exist: " + sourceCsvPath);
            return successfulPaths;
        }

        for (String odkPath : ODK_CSV_PATHS) {
            try {
                File odkDir = new File(Environment.getExternalStorageDirectory(), odkPath);

                // Try to create directory if it doesn't exist
                if (!odkDir.exists() && !odkDir.mkdirs()) {
                    Log.w(TAG, "Cannot create/access ODK directory: " + odkDir.getAbsolutePath());
                    continue;
                }

                File targetFile = new File(odkDir, csvFileName);

                // Copy file
                java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                successfulPaths.add(targetFile.getAbsolutePath());
                Log.d(TAG, "Successfully copied CSV to: " + targetFile.getAbsolutePath());

            } catch (Exception e) {
                Log.w(TAG, "Failed to copy CSV to ODK path: " + odkPath + " - " + e.getMessage());
            }
        }

        return successfulPaths;
    }

    /**
     * Read individuals from existing CSV file
     */
    public List<Individual> readIndividualsFromCsv(String csvFileName) throws IOException {
        List<Individual> individuals = new ArrayList<>();

        File csvDir = new File(context.getExternalFilesDir(null), CSV_DIR_NAME);
        File csvFile = new File(csvDir, prepareCsvFileName(csvFileName));

        if (!csvFile.exists()) {
            Log.w(TAG, "CSV file does not exist: " + csvFile.getAbsolutePath());
            return individuals;
        }

        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        String line = reader.readLine(); // Skip header

        while ((line = reader.readLine()) != null) {
            String[] columns = parseCsvLine(line);

            if (columns.length >= 9) { // Minimum required columns
                Individual individual = new Individual();
                individual.setExtId(columns[0]);
                individual.setFirstName(columns[1]);
                individual.setLastName(columns[2]);

                if (!columns[3].isEmpty()) {
                    try {
                        int genderValue = Integer.parseInt(columns[3].trim());
                        individual.setGender(genderValue);
                    } catch (NumberFormatException e) {
                        Log.w(TAG, "Invalid gender value (not a number): " + columns[3]);
                    }
                }

                individual.setDob(columns[4]);
                individual.setVillage(columns[5]);
                individual.setHohID(columns[6]);
                individual.setCompno(columns[7]);
                individual.setUuid(columns[8]);

                individuals.add(individual);
            }
        }

        reader.close();
        Log.d(TAG, "Read " + individuals.size() + " individuals from CSV");
        return individuals;
    }

    /**
     * Validate CSV file structure and content
     */
    public CsvValidationResult validateCsvFile(String csvFileName) {
        try {
            File csvDir = new File(context.getExternalFilesDir(null), CSV_DIR_NAME);
            File csvFile = new File(csvDir, prepareCsvFileName(csvFileName));

            if (!csvFile.exists()) {
                return new CsvValidationResult(false, "CSV file does not exist", 0);
            }

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String headerLine = reader.readLine();

            if (headerLine == null) {
                reader.close();
                return new CsvValidationResult(false, "CSV file is empty", 0);
            }

            // Validate header structure
            String expectedHeader = "permid,firstname,lastname,gender,dob,village,hhid,compno,individual_uuid,interview_date,created_at";
            if (!headerLine.trim().equals(expectedHeader)) {
                reader.close();
                return new CsvValidationResult(false, "Invalid CSV header structure", 0);
            }

            int rowCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = parseCsvLine(line);
                if (columns.length < 11) {
                    reader.close();
                    return new CsvValidationResult(false, "Row " + (rowCount + 2) + " has insufficient columns", rowCount);
                }

                // Validate required fields
                if (columns[0].trim().isEmpty()) {
                    reader.close();
                    return new CsvValidationResult(false, "Row " + (rowCount + 2) + " missing required permid", rowCount);
                }

                rowCount++;
            }

            reader.close();
            return new CsvValidationResult(true, "CSV file is valid", rowCount);

        } catch (IOException e) {
            Log.e(TAG, "Error validating CSV file", e);
            return new CsvValidationResult(false, "Error reading CSV file: " + e.getMessage(), 0);
        }
    }

    /**
     * Clean up old CSV files
     */
    public CleanupResult cleanupOldCsvFiles(int maxAgeHours) {
        int deletedCount = 0;
        long cutoffTime = System.currentTimeMillis() - (maxAgeHours * 60 * 60 * 1000L);

        try {
            // Clean app directory
            File csvDir = new File(context.getExternalFilesDir(null), CSV_DIR_NAME);
            if (csvDir.exists()) {
                File[] csvFiles = csvDir.listFiles((file) -> file.getName().endsWith(".csv"));
                if (csvFiles != null) {
                    for (File file : csvFiles) {
                        if (file.lastModified() < cutoffTime) {
                            if (file.delete()) {
                                deletedCount++;
                                Log.d(TAG, "Deleted old CSV file: " + file.getName());
                            }
                        }
                    }
                }
            }

            // Clean ODK directories
            for (String odkPath : ODK_CSV_PATHS) {
                try {
                    File odkDir = new File(Environment.getExternalStorageDirectory(), odkPath);
                    if (odkDir.exists()) {
                        File[] csvFiles = odkDir.listFiles((file) ->
                                file.getName().endsWith(".csv") &&
                                        file.getName().startsWith("individuals_") ||
                                        file.getName().equals(DEFAULT_CSV_NAME));

                        if (csvFiles != null) {
                            for (File file : csvFiles) {
                                if (file.lastModified() < cutoffTime) {
                                    if (file.delete()) {
                                        deletedCount++;
                                        Log.d(TAG, "Deleted old ODK CSV file: " + file.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error cleaning ODK directory: " + odkPath, e);
                }
            }

            return new CleanupResult(true, "Cleaned up " + deletedCount + " old CSV files", deletedCount);

        } catch (Exception e) {
            Log.e(TAG, "Error during CSV cleanup", e);
            return new CleanupResult(false, "Cleanup failed: " + e.getMessage(), deletedCount);
        }
    }

    /**
     * Get list of available CSV files
     */
    public List<CsvFileInfo> getAvailableCsvFiles() {
        List<CsvFileInfo> csvFiles = new ArrayList<>();

        try {
            File csvDir = new File(context.getExternalFilesDir(null), CSV_DIR_NAME);
            if (csvDir.exists()) {
                File[] files = csvDir.listFiles((file) -> file.getName().endsWith(".csv"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            int recordCount = countCsvRecords(file.getAbsolutePath());
                            Date lastModified = new Date(file.lastModified());
                            long fileSize = file.length();

                            csvFiles.add(new CsvFileInfo(
                                    file.getName(),
                                    file.getAbsolutePath(),
                                    recordCount,
                                    lastModified,
                                    fileSize
                            ));
                        } catch (Exception e) {
                            Log.w(TAG, "Error reading CSV file info: " + file.getName(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error listing CSV files", e);
        }

        return csvFiles;
    }

    /**
     * Create CSV with specific individuals for a household
     */
    public CsvCreationResult createHouseholdCsv(String householdId, List<Individual> householdMembers, String csvFileName) {
        if (householdMembers == null || householdMembers.isEmpty()) {
            return new CsvCreationResult(false, "No household members provided", null, null);
        }

        // Filter individuals for this household if needed
        List<Individual> filteredMembers = new ArrayList<>();
        for (Individual individual : householdMembers) {
            if (individual.getHohID() != null && individual.getHohID().equals(householdId)) {
                filteredMembers.add(individual);
            }
        }

        if (filteredMembers.isEmpty()) {
            // If no filtering by household ID, use all provided members
            filteredMembers = householdMembers;
        }

        String finalCsvName = csvFileName != null ? csvFileName : ("household_" + householdId + "_data.csv");
        return createIndividualsCsv(filteredMembers, finalCsvName);
    }

    // Helper methods

    private String prepareCsvFileName(String csvFileName) {
        if (csvFileName == null || csvFileName.trim().isEmpty()) {
            return DEFAULT_CSV_NAME;
        }

        String cleanName = csvFileName.trim();
        if (!cleanName.endsWith(".csv")) {
            cleanName += ".csv";
        }

        // Sanitize filename
        cleanName = cleanName.replaceAll("[^a-zA-Z0-9._-]", "_");

        return cleanName;
    }

    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(ch);
            }
        }

        result.add(currentField.toString());
        return result.toArray(new String[0]);
    }

    private int countCsvRecords(String csvFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        int count = 0;
        reader.readLine(); // Skip header

        while (reader.readLine() != null) {
            count++;
        }

        reader.close();
        return count;
    }

    // Result classes

    public static class CsvCreationResult {
        public final boolean success;
        public final String message;
        public final String appCsvPath;
        public final List<String> odkCsvPaths;

        public CsvCreationResult(boolean success, String message, String appCsvPath, List<String> odkCsvPaths) {
            this.success = success;
            this.message = message;
            this.appCsvPath = appCsvPath;
            this.odkCsvPaths = odkCsvPaths != null ? odkCsvPaths : new ArrayList<>();
        }

        public boolean hasOdkPaths() {
            return !odkCsvPaths.isEmpty();
        }

        public int getOdkPathCount() {
            return odkCsvPaths.size();
        }
    }

    public static class CsvValidationResult {
        public final boolean isValid;
        public final String message;
        public final int recordCount;

        public CsvValidationResult(boolean isValid, String message, int recordCount) {
            this.isValid = isValid;
            this.message = message;
            this.recordCount = recordCount;
        }
    }

    public static class CleanupResult {
        public final boolean success;
        public final String message;
        public final int deletedCount;

        public CleanupResult(boolean success, String message, int deletedCount) {
            this.success = success;
            this.message = message;
            this.deletedCount = deletedCount;
        }
    }

    public static class CsvFileInfo {
        public final String fileName;
        public final String filePath;
        public final int recordCount;
        public final Date lastModified;
        public final long fileSize;

        public CsvFileInfo(String fileName, String filePath, int recordCount, Date lastModified, long fileSize) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.recordCount = recordCount;
            this.lastModified = lastModified;
            this.fileSize = fileSize;
        }
    }
}