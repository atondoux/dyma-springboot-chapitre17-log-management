package com.dyma.tennis.web;

import com.dyma.tennis.Player;
import com.dyma.tennis.PlayerToSave;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlayerControllerEndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldCreatePlayer() {
        // Given
        PlayerToSave playerToCreate = new PlayerToSave(
                "Carlos",
                "Alcaraz",
                LocalDate.of(2003, Month.MAY, 5),
                4500
        );

        // When
        String url = "http://localhost:" + port + "/players";
        HttpEntity<PlayerToSave> request = new HttpEntity<>(playerToCreate);
        ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Player.class);

        // Then
        Assertions.assertThat(playerResponseEntity.getBody().lastName()).isEqualTo("Alcaraz");
        Assertions.assertThat(playerResponseEntity.getBody().rank().position()).isEqualTo(2);
    }

    @Test
    public void shouldFailToCreatePlayer_WhenPlayerToCreateIsInvalid() {
        // Given
        PlayerToSave playerToCreate = new PlayerToSave(
                "Carlos",
                null,
                LocalDate.of(2003, Month.MAY, 5),
                4500
        );

        // When
        String url = "http://localhost:" + port + "/players";
        HttpEntity<PlayerToSave> request = new HttpEntity<>(playerToCreate);
        ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Player.class);

        // Then
        Assertions.assertThat(playerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldUpdatePlayerRanking() {
        // Given
        PlayerToSave playerToUpdate = new PlayerToSave(
                "Rafael",
                "NadalTest",
                LocalDate.of(1986, Month.JUNE, 3),
                1000
        );

        // When
        String url = "http://localhost:" + port + "/players";
        HttpEntity<PlayerToSave> request = new HttpEntity<>(playerToUpdate);
        ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, request, Player.class);

        // Then
        Assertions.assertThat(playerResponseEntity.getBody().lastName()).isEqualTo("NadalTest");
        Assertions.assertThat(playerResponseEntity.getBody().rank().position()).isEqualTo(3);
    }

    @Test
    public void shouldDeletePlayer() {
        // Given / When
        String url = "http://localhost:" + port + "/players";
        this.restTemplate.exchange(url + "/djokovictest", HttpMethod.DELETE, null, Player.class);
        HttpEntity<List<Player>> allPlayersResponseEntity = this.restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Player>>() {
                }
        );

        // Then
        Assertions.assertThat(allPlayersResponseEntity.getBody())
                .extracting("lastName", "rank.position")
                .containsExactly(Tuple.tuple("NadalTest", 1), Tuple.tuple("FedererTest", 2));
    }
}
