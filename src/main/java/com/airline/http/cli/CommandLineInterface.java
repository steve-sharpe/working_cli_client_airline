package com.airline.http.cli;

import com.airline.domain.Aircraft;
import com.airline.domain.Airport;
import com.airline.domain.City;
import com.airline.domain.Passenger;
import com.airline.http.client.RESTClient;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private final RESTClient restClient;
    private final Scanner scanner; // Declare a single scanner instance

    public CommandLineInterface(String baseUrl) {
        this.restClient = new RESTClient(baseUrl);
        this.scanner = new Scanner(System.in); // Initialize the scanner once
    }

    public void run() {
        System.out.println("Welcome to the Flight Management CLI!");

        while (true) {
            System.out.println("\nAvailable commands:");
            System.out.println("1. List all cities");
            System.out.println("2. List all airports");
            System.out.println("3. List all aircraft");
            System.out.println("4. List all passengers");
            System.out.println("5. Generate passenger, aircraft, and city report");
            System.out.println("6. Exit");
            System.out.print("\nEnter your command: ");

            int command = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character after nextInt()

            switch (command) {
                case 1:
                    listAllCities();
                    break;
                case 2:
                    listAirports();
                    break;
                case 3:
                    listAircraft();
                    break;
                case 4:
                    listPassengers();
                    break;
//                case 5:
//                    generatePassengerAircraftCityReport();
//                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }

    private void listAllCities() {
        List<City> cities = restClient.getAllCities();
        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found.");
            return;
        }

        System.out.println("\n--- List of Cities ---");
        for (City city : cities) {
            System.out.println("ID: " + city.getId() + ", Name: " + city.getName() + ", State: " + city.getState() + ", Population: " + city.getPopulation());
        }

        // Display submenu options
        showCityDetailsMenu();
    }

    private void showCityDetailsMenu() {
        while (true) {
            System.out.println("\n--- City Details Menu ---");
            System.out.println("1. Enter city ID for more info");
            System.out.println("2. Return to main menu");
            System.out.print("Select an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    getCityDetails();
                    break;
                case 2:
                    return; // Return to main menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void getCityDetails() {
        System.out.print("Enter the City ID: ");
        Long cityId = Long.parseLong(scanner.nextLine());

        List<Airport> airports = restClient.getAirportsByCityId(cityId);
        if (airports.isEmpty()) {
            System.out.println("No airports found for City ID: " + cityId);
        } else {
            System.out.println("\n--- Airports for City ID: " + cityId + " ---");
            for (Airport airport : airports) {
                System.out.println("Airport ID: " + airport.getId() + ", Code: " + airport.getCode() + ", Name: " + airport.getName());
            }
        }
    }


    private void listAirports() {
        List<Airport> airports = restClient.getAllAirports();
        if (airports == null || airports.isEmpty()) {
            System.out.println("No airports found.");
            return;
        }

        System.out.println("Airports:");
        for (Airport airport : airports) {
            System.out.println("\n==============================");
            System.out.println("ID: " + airport.getId() + ", Name: " + airport.getName() + ", Code: " + airport.getCode());
        }
        paginate();
    }

    private void listAircraft() {
        List<Aircraft> aircrafts = restClient.getAllAircraft();
        if (aircrafts == null || aircrafts.isEmpty()) {
            System.out.println("No aircraft found.");
            return;
        }

        System.out.println("Aircraft:");
        for (Aircraft aircraft : aircrafts) {
            System.out.println("\n==============================");
            System.out.println("ID: " + aircraft.getId() + ", Model: " + aircraft.getType() + " (" + aircraft.getAirlineName() + ")");
        }
        paginate();
    }

    private void listPassengers() {
        List<Passenger> passengers = restClient.getAllPassengers();
        if (passengers == null) {
            System.out.println("Error fetching passengers.");
            return;
        }

        if (passengers.isEmpty()) {
            System.out.println("No passengers found.");
            return;
        }

        System.out.println("Passengers:");
        for (Passenger passenger : passengers) {
            System.out.println("\n==============================");
            String phone = passenger.getPhoneNumber();
            if (phone == null) {
                System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getFirstName() + " " + passenger.getLastName() + ", Phone: null");
            } else {
                System.out.println("ID: " + passenger.getId() + ", Name: " + passenger.getFirstName() + " " + passenger.getLastName() + ", Phone: " + phone);
            }
        }
        paginate();
    }

    private void generatePassengerAircraftCityReport() {
        List<Passenger> passengers = restClient.getAllPassengers();
        if (passengers == null) {
            System.out.println("Error fetching passengers.");
            return;
        }

        if (passengers.isEmpty()) {
            System.out.println("No passengers found.");
            return;
        }

        System.out.println("Passenger, Aircraft, and City Report:");
        for (Passenger passenger : passengers) {
            System.out.println("\n==============================");
            System.out.println("Passenger ID: " + passenger.getId() + ", Name: " + passenger.getFirstName() + " " + passenger.getLastName());
            System.out.println("Phone Number: " + passenger.getPhoneNumber());

            City city = restClient.getCityById(passenger.getCityId());
            if (city != null) {
                System.out.println("City: [ID: " + city.getId() + ", Name: " + city.getName() + "]");
            } else {
                System.out.println("City: null");
            }

            List<Aircraft> aircrafts = restClient.getAircraftByPassengerId(passenger.getId());
            if (aircrafts != null && !aircrafts.isEmpty()) {
                System.out.println("Aircrafts:");
                for (Aircraft aircraft : aircrafts) {
                    System.out.println("  Aircraft ID: " + aircraft.getId() + ", Model: " + aircraft.getType() + ", Airline: " + aircraft.getAirlineName());
                }
            } else {
                System.out.println("No aircrafts assigned.");
            }
        }
        paginate();
    }

    private void paginate() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface("http://localhost:8080");
        cli.run();
    }
}
