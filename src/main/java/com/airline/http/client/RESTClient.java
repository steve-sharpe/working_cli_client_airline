package com.airline.http.client;

import com.airline.domain.Aircraft;
import com.airline.domain.Airport;
import com.airline.domain.City;
import com.airline.domain.Passenger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RESTClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public RESTClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.baseUrl = baseUrl;
    }

    public String fetchJsonFromEndpoint(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Failed to fetch data from " + endpoint + ": HTTP status code " + response.statusCode());
                System.err.println("Error response body: " + response.body()); // Print error response body for debugging
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<City> getAllCities() {
        return fetchList("/cities", new TypeReference<List<City>>() {});
    }

    public List<Aircraft> getAircraftByPassengerId(Long passengerId) {
        return fetchList("/passengers/" + passengerId + "/aircrafts", new TypeReference<List<Aircraft>>() {});
    }

    public List<Airport> getAllAirports() {
        return fetchList("/airports", new TypeReference<List<Airport>>() {});
    }

    public Passenger getPassengerById(Long passengerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/passengers/" + passengerId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Passenger.class);
            } else {
                System.err.println("Error: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return null;
        }
    }


    public List<Aircraft> getAllAircraft() {
        System.out.println("Fetching all aircraft from " + baseUrl + "/aircraft");
        List<Aircraft> aircraftList = fetchList("/aircraft", new TypeReference<List<Aircraft>>() {});
        if (aircraftList == null) {
            System.err.println("Failed to fetch aircraft");
            return List.of();
        }
        System.out.println("Successfully fetched " + aircraftList.size() + " aircraft");
        return aircraftList;
    }

    public City getCityById(Long cityId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/cities/" + cityId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), City.class);
            } else {
                System.err.println("Error: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return null;
        }
    }

    public List<Passenger> getAllPassengers() {
        return fetchList("/passengers", new TypeReference<List<Passenger>>() {});
    }

    private <T> T fetchObject(String endpoint, Class<T> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), clazz);
            } else {
                System.err.println("Error: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return null;
        }
    }
    
    public List<Airport> getAirportsByCityId(Long cityId) {
        try {
            String response = fetchJsonFromEndpoint("/airports/byCity/" + cityId);
            if (response == null || response.isEmpty()) {
                System.err.println("No airports found for city ID: " + cityId);
                return new ArrayList<>();
            }
            return objectMapper.readValue(response, new TypeReference<List<Airport>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private <T> List<T> fetchList(String endpoint, TypeReference<List<T>> typeReference) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), typeReference);
            } else {
                System.err.println("Error: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return null;
        }
    }
}