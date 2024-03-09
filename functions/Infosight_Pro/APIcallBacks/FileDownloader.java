package APIcallBacks;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class FileDownloader {
    public static String downloadedCSVs = "No open file";
    public static Map<String, String> fileDictionary = new HashMap<>();
    public static String accessToken = Cliq_GenerateAccfromRef.generateAccessToken();


    public static void main(String[] args) throws IOException, InterruptedException {
        sendFileToBot("SaveAsCSV");

    }







    public static String sendFileToBot(String destiFileName) throws IOException, InterruptedException {
        String botUniqueName = "projectsbot";
        String cliqApiEndpoint = "https://cliq.zoho.com/api/v2/bots/" + botUniqueName + "/files";
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String fileSendFlag = null;


        try {
            URL url = new URL(cliqApiEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setDoOutput(true);

            // Read the file content
            File file = new File("Downloaded_Files/CSV_Cliq.csv"); // Change the file path accordingly
            FileInputStream fileInputStream = new FileInputStream(file);

            // Construct the request body
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            // Add file
            writer.append("--" + boundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\"" + destiFileName + "\"").append("\r\n")
                    .append("Content-Type: text/csv").append("\r\n")
                    .append("\r\n");
            writer.flush();

            // Write file content to output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            // Add comments
            writer.append("\r\n")
                    .append("--" + boundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"comments\"").append("\r\n")
                    .append("\r\n")
                    .append("[\"Your modified csv file\",\"null\"]").append("\r\n")
                    .append("--" + boundary + "--").append("\r\n");
            writer.flush();

            // Close streams
            fileInputStream.close();
            writer.close();
            outputStream.close();

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Check for successful response (code 204 - No Content)
            if (responseCode == 200 || responseCode == 201 || responseCode == 204 ) {
                System.out.println("File shared successfully with the bot.");
                fileSendFlag = "success";

            } else {
                System.out.println("Failed to share file with the bot. Response Code: " + responseCode);
                fileSendFlag = "failure";
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSendFlag;
    }


    // Get and print response







    public static void closeCsvFile() {

        // Define the path to the file to be deleted
        Path filePath = Path.of("Downloaded_Files", "CSV_Cliq.csv");

        try {
            // Check if the file exists before attempting to delete
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("File 'CSV_Cliq.csv' deleted successfully.");
                downloadedCSVs = "No open file";
            } else {
                System.out.println("File 'CSV_Cliq.csv' does not exist.");
            }
        } catch (IOException e) {
            System.err.println("Error occurred while deleting the file: " + e.getMessage());
        }
    }


    public static void pseudoMain() {
        populateFileDictionary();

        // Display all files at once after processing chats
        System.out.println("All Files:");
        for (Map.Entry<String, String> entry : fileDictionary.entrySet()) {
            System.out.println("File Name: " + entry.getKey() + "; File ID: " + entry.getValue());
        }


    }

    public static void populateFileDictionary() {


        // Specify the API endpoint for fetching channel messages
        String chatsApiUrl = "https://cliq.zoho.com/api/v2/chats";
        String messagesApiUrl = "https://cliq.zoho.com/api/v2/chats/{chat_id}/messages"; // Replace {chat_id} with the actual chat ID

        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();

        try {
            // Build the request to fetch chats
            HttpRequest chatsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(chatsApiUrl))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> chatsResponse = client.send(chatsRequest, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response for chats
            JSONObject data = new JSONObject(chatsResponse.body());

            // Check if "chats" array exists
            if (data.has("chats")) {
                // Get the "chats" array
                JSONArray chatsArray = data.getJSONArray("chats");

                // Retrieve the list of files currently available in Zoho Cliq
                List<String> currentFiles = new ArrayList<>();

                for (int i = 0; i < chatsArray.length(); i++) {
                    JSONObject chat = chatsArray.getJSONObject(i);
                    String chatId = chat.getString("chat_id");

                    // Build the request to fetch messages for the current chat
                    HttpRequest messagesRequest = HttpRequest.newBuilder()
                            .uri(URI.create(messagesApiUrl.replace("{chat_id}", chatId)))
                            .header("Authorization", "Bearer " + accessToken)
                            .build();

                    HttpResponse<String> messagesResponse = client.send(messagesRequest, HttpResponse.BodyHandlers.ofString());

                    // Parse the JSON response for messages
                    JSONObject messagesData = new JSONObject(messagesResponse.body());

                    // Extract files from the current chat and update the file dictionary and currentFiles list
                    extractFilesFromChat(messagesData.getJSONArray("data"), currentFiles);
                }

                // Check the number of files stored in the dictionary


                // Add "n. Exit iteration" to the fileDictionary
                // Replace "exit_iteration_value" with the appropriate value

                // Remove files from fileDictionary that are not present in currentFiles
                for (String filename : new ArrayList<>(fileDictionary.keySet())) {

                    if (!currentFiles.contains(filename)) {
                        fileDictionary.remove(filename);
                    }
                }


            }

        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
            // Handle different exceptions: network-related errors, interrupted exception, JSON parsing errors
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any other unhandled exceptions
        }
    }


    private static void extractFilesFromChat(JSONArray dataArray, List<String> currentFiles) throws JSONException {
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject message = dataArray.getJSONObject(i);

            if ("file".equals(message.getString("type"))) {
                JSONObject fileContent = message.getJSONObject("content").getJSONObject("file");
                String fileName = fileContent.getString("name");
                String fileId = fileContent.getString("id");

                if (fileName.toLowerCase().endsWith(".csv")) {
                    // Add to the fileDictionary
                    fileDictionary.put(fileName, fileId);
                    // Add filename to the currentFiles list
                    currentFiles.add(fileName);
                }
            }
        }
    }

    public static Map<String, String> fileDictionaryFDJ = fileDictionary;

    public static void openCsvFile(String fileName) {

        closeCsvFile();


        pseudoMain();
        populateFileDictionary();

        String fileID = fileDictionaryFDJ.get(fileName);

        if (fileID != null) {
            Path destinationPath = Path.of("Downloaded_Files", "CSV_Cliq.csv"); // Change destination file name if needed

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://cliq.zoho.com/api/v2/files/" + fileID))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            try {
                Files.createDirectories(destinationPath.getParent());

                HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(destinationPath));

                if (response.statusCode() == 200) {
                    System.out.println("File downloaded successfully: " + fileName);

                    downloadedCSVs = fileName;

                } else {
                    System.out.println("Failed to download the file. Status code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File with name '" + fileName + "' not found in file dictionary.");
        }
    }
}




