package conversor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class ConversorDeMoedas {

    // Classe para mapear resposta JSON da API ExchangeRate-API v6
    static class ExchangeRateApiResponse {
        String result;              // Deve ser "success"
        String base_code;          // Moeda base
        Map<String, Double> conversion_rates; // Map com as taxas de câmbio
    }

    // Método que obtém a taxa de câmbio entre duas moedas
    public static double obterTaxa(String from, String to) {
        try {
            String chave = "235b08b0a9852ad6b3fd124b";
            String url = "https://v6.exchangerate-api.com/v6/" + chave + "/latest/" + from;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Para ajudar a debugar, pode imprimir o JSON retornado
            // System.out.println("Resposta da API: " + response.body());

            Gson gson = new Gson();
            ExchangeRateApiResponse data = gson.fromJson(response.body(), ExchangeRateApiResponse.class);

            if (!"success".equals(data.result)) {
                System.out.println("Erro na API ao tentar obter taxas.");
                return 0;
            }

            Double taxa = data.conversion_rates.get(to);
            if (taxa == null) {
                System.out.println("Moeda destino não encontrada.");
                return 0;
            }

            return taxa;

        } catch (Exception e) {
            System.out.println("Erro ao obter taxa de câmbio: " + e.getMessage());
            return 0;
        }
    }

    // Método que converte o valor usando a taxa obtida
    public static double converterMoeda(String from, String to, double amount) {
        double taxa = obterTaxa(from, to);
        if (taxa == 0) return 0;
        return amount * taxa;
    }

    // Método principal com menu para interação via console
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== CONVERSOR DE MOEDAS ===");
            System.out.println("1 - BRL (Real) para USD (Dólar Americano)");
            System.out.println("2 - USD (Dólar Americano) para BRL (Real)");
            System.out.println("3 - BRL (Real) para EUR (Euro)");
            System.out.println("4 - EUR (Euro) para BRL (Real)");
            System.out.println("5 - BRL (Real) para GBP (Libra Esterlina)");
            System.out.println("6 - GBP (Libra Esterlina) para BRL (Real)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            if (opcao == 0) {
                System.out.println("Programa encerrado.");
                break;
            }

            String from = null;
            String to = null;

            switch (opcao) {
                case 1 -> { from = "BRL"; to = "USD"; }
                case 2 -> { from = "USD"; to = "BRL"; }
                case 3 -> { from = "BRL"; to = "EUR"; }
                case 4 -> { from = "EUR"; to = "BRL"; }
                case 5 -> { from = "BRL"; to = "GBP"; }
                case 6 -> { from = "GBP"; to = "BRL"; }
                default -> {
                    System.out.println("Opção inválida.");
                    continue;
                }
            }

            System.out.print("Digite o valor a ser convertido: ");
            double valor = scanner.nextDouble();

            double resultado = converterMoeda(from, to, valor);
            System.out.printf("%.2f %s = %.2f %s%n", valor, from, resultado, to);
        }

        scanner.close();
    }
}
