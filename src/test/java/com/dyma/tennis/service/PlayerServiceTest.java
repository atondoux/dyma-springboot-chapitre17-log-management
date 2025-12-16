package com.dyma.tennis.service;

import com.dyma.tennis.Player;
import com.dyma.tennis.data.PlayerEntityList;
import com.dyma.tennis.data.PlayerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    @Test
    public void shouldReturnPlayersRanking() {
        // Given
        Mockito.when(playerRepository.findAll()).thenReturn(PlayerEntityList.ALL);

        // When
        List<Player> allPlayers = playerService.getAllPlayers();

        // Then
        Assertions.assertThat(allPlayers)
                .extracting("lastName")
                .containsExactly("Nadal", "Djokovic", "Federer", "Murray");
    }

    @Test
    public void shouldFailToReturnPlayersRanking_WhenDataAccessExceptionOccurs() {
        // Given
        Mockito.when(playerRepository.findAll()).thenThrow(new DataRetrievalFailureException("Data access error"));

        // When / Then
        Assertions.assertThatThrownBy(() -> playerService.getAllPlayers())
                .isInstanceOf(PlayerDataRetrievalException.class)
                .hasMessage("Could not retrieve player data");
    }

    @Test
    public void shouldRetrievePlayer() {
        // Given
        String playerToRetrieve = "nadal";
        Mockito.when(playerRepository.findOneByLastNameIgnoreCase(playerToRetrieve)).thenReturn(Optional.of(PlayerEntityList.RAFAEL_NADAL));

        // When
        Player retrievedPlayer = playerService.getByLastName(playerToRetrieve);

        // Then
        Assertions.assertThat(retrievedPlayer.lastName()).isEqualTo("Nadal");
    }

    @Test
    public void shouldFailToRetrievePlayer_WhenPlayerDoesNotExist() {
        // Given
        String unknownPlayer = "doe";
        Mockito.when(playerRepository.findOneByLastNameIgnoreCase(unknownPlayer)).thenReturn(Optional.empty());

        // When / Then
        Assertions.assertThatThrownBy(() -> playerService.getByLastName(unknownPlayer))
                .isInstanceOf(PlayerNotFoundException.class)
                .hasMessage("Player with last name doe could not be found.");
    }
}
