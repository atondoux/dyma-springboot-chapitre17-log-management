package com.dyma.tennis.web;

import com.dyma.tennis.data.PlayerList;
import com.dyma.tennis.service.PlayerNotFoundException;
import com.dyma.tennis.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @Test
    public void shouldListAllPlayers() {
        // Given
        when(playerService.getAllPlayers()).thenReturn(PlayerList.ALL);

        // When
        var response = mockMvc.get().uri("/players")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        var json = response.assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$.length()").isEqualTo(4);
        json.extractingPath("$[0].lastName").isEqualTo("Nadal");
        json.extractingPath("$[1].lastName").isEqualTo("Djokovic");
        json.extractingPath("$[2].lastName").isEqualTo("Federer");
        json.extractingPath("$[3].lastName").isEqualTo("Murray");
    }

    @Test
    public void shouldRetrievePlayer() {
        // Given
        String playerToRetrieve = "nadal";
        when(playerService.getByLastName(playerToRetrieve)).thenReturn(PlayerList.RAFAEL_NADAL);

        // When
        var response = mockMvc.get().uri("/players/nadal")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        var json = response.assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$.lastName").isEqualTo("Nadal");
        json.extractingPath("$.rank.position").isEqualTo(1);
    }

    @Test
    public void shouldReturn404NotFound_WhenPlayerDoesNotExist() {
        // Given
        String playerToRetrieve = "doe";
        when(playerService.getByLastName(playerToRetrieve)).thenThrow(new PlayerNotFoundException(playerToRetrieve));

        // When
        var response = mockMvc.get().uri("/players/doe")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Then
        var json = response.assertThat().hasStatus(HttpStatus.NOT_FOUND).bodyJson();
        json.extractingPath("$.errorDetails").isEqualTo("Player with last name doe could not be found.");
    }
}
