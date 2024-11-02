import com.airline.domain.City;
import com.airline.http.cli.CommandLineInterface;
import com.airline.http.client.RESTClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CommandLineInterfaceTest {

    @Mock
    private RESTClient restClient;

    @InjectMocks
    private CommandLineInterface cli;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        cli = new CommandLineInterface(restClient);
    }

    @Test
    void testListCitiesCommand() {
        // Mock RESTClient response for getAllCities
        when(restClient.getAllCities()).thenReturn(List.of(
                new City(1L, "New York", "NY", 8000000, Set.of(), Set.of()),
                new City(2L, "Los Angeles", "CA", 4000000, Set.of(), Set.of())
        ));

        // Set test mode to skip pagination
        cli.setTestMode(true);

        // Simulate user input to list cities and exit
        String input = "1\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Run the CLI
        cli.run();

        // Verify output
        String output = outContent.toString();
        assertTrue(output.contains("New York"));
        assertTrue(output.contains("Los Angeles"));
        assertTrue(output.contains("Population: 8000000"));
        assertTrue(output.contains("Population: 4000000"));
    }}