package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class App {

  private static final String API_URL =
    "https://api.exchangerate-api.com/v4/latest/";

  public static double getExchangeRate(String fromCurrency, String toCurrency) {
    try {
      String urlString = API_URL + fromCurrency;
      @SuppressWarnings("deprecation")
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        throw new RuntimeException(
          "HTTP GET Request Failed with Error Code: " + responseCode
        );
      }

      StringBuilder response;
      try (
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream())
        )
      ) {
        response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
      }

      JSONObject jsonResponse = new JSONObject(response.toString());
      return jsonResponse.getJSONObject("rates").getDouble(toCurrency);
    } catch (IOException | RuntimeException e) {
      System.out.println(
        "Error: Unable to fetch exchange rate. " + e.getMessage()
      );
      return -1;
    }
  }

  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.print("Enter base currency (e.g., USD, EUR, INR): ");
      String fromCurrency = scanner.next().toUpperCase();

      System.out.print("Enter target currency (e.g., USD, EUR, INR): ");
      String toCurrency = scanner.next().toUpperCase();

      System.out.print("Enter amount: ");
      double amount = scanner.nextDouble();

      double rate = getExchangeRate(fromCurrency, toCurrency);
      if (rate != -1) {
        double convertedAmount = amount * rate;
        System.out.printf(
          "Exchange Rate: 1 %s = %.2f %s\n",
          fromCurrency,
          rate,
          toCurrency
        );
        System.out.printf(
          "Converted Amount: %.2f %s\n",
          convertedAmount,
          toCurrency
        );
      } else {
        System.out.println("Conversion failed. Please check currency codes.");
      }
    }
  }
}
