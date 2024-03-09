package APIcallBacks;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ZohoCliqAPIBots {
    public static void main(String[] args) {
        String clientId = "1000.PH59BVFD55ZMZZ0WE9MQDM842RRC9R";
        String clientSecret = "9560f0650eed12e5855bf0a7813d89a87f16218eea";
        String refreshToken = "1000.6f282a0d9a276cccfe50807b6931bee5.54c771addd03fe458b36541fe54f2d1c";

        // Call the newAccess function to get the access token
        String accessToken = Cliq_GenerateAccfromRef.generateAccessToken();
        System.out.println("Access Token: " + accessToken);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://cliq.zoho.com/api/v2/bots"))
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