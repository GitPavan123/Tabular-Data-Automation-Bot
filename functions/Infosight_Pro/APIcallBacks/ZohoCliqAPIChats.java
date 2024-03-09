package APIcallBacks;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ZohoCliqAPIChats {
    public static void main(String[] args) {
        String clientId = "1000.PH59BVFD55ZMZZ0WE9MQDM842RRC9R";
        String clientSecret = "9560f0650eed12e5855bf0a7813d89a87f16218eea";
        String refreshToken = "1000.1927ba6de9db611743fefde794555619.2af3b55e10cd0e0da245daceca9f0485";

        // Call the newAccess function to get the access token
        String accessToken = Cliq_GenerateAccfromRef.generateAccessToken();
        System.out.println("Access Token: " + accessToken);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cliq.zoho.com/api/v2/chats"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}