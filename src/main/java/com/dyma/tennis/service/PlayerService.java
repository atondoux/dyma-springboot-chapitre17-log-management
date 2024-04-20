package com.dyma.tennis.service;

import com.dyma.tennis.Player;
import com.dyma.tennis.PlayerToSave;
import com.dyma.tennis.Rank;
import com.dyma.tennis.data.PlayerEntity;
import com.dyma.tennis.data.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        try {
            return playerRepository.findAll().stream()
                    .map(player -> new Player(
                            player.getFirstName(),
                            player.getLastName(),
                            player.getBirthDate(),
                            new Rank(player.getRank(), player.getPoints())
                    ))
                    .sorted(Comparator.comparing(player -> player.rank().position()))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player getByLastName(String lastName) {
        Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(lastName);
        if (player.isEmpty()) {
            throw new PlayerNotFoundException(lastName);
        }
        return new Player(
                player.get().getFirstName(),
                player.get().getLastName(),
                player.get().getBirthDate(),
                new Rank(player.get().getRank(), player.get().getPoints())
        );
    }

    public Player create(PlayerToSave playerToSave) {
        Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
        if (player.isPresent()) {
            throw new PlayerAlreadyExistsException(playerToSave.lastName());
        }

        try {
            PlayerEntity playerToRegister = new PlayerEntity(
                    playerToSave.lastName(),
                    playerToSave.firstName(),
                    playerToSave.birthDate(),
                    playerToSave.points(),
                    999999999);

            PlayerEntity registeredPlayer = playerRepository.save(playerToRegister);

            RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
            List<PlayerEntity> newRanking = rankingCalculator.getNewPlayersRanking();
            playerRepository.saveAll(newRanking);

            return getByLastName(registeredPlayer.getLastName());
        } catch (DataAccessException e) {
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player update(PlayerToSave playerToSave) {
        Optional<PlayerEntity> playerToUpdate = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
        if (playerToUpdate.isEmpty()) {
            throw new PlayerNotFoundException(playerToSave.lastName());
        }

        try {
            playerToUpdate.get().setFirstName(playerToSave.firstName());
            playerToUpdate.get().setBirthDate(playerToSave.birthDate());
            playerToUpdate.get().setPoints(playerToSave.points());
            PlayerEntity updatedPlayer = playerRepository.save(playerToUpdate.get());

            RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
            List<PlayerEntity> newRanking = rankingCalculator.getNewPlayersRanking();
            playerRepository.saveAll(newRanking);

            return getByLastName(updatedPlayer.getLastName());
        } catch (DataAccessException e) {
            throw new PlayerDataRetrievalException(e);
        }
    }

    public void delete(String lastName) {
        Optional<PlayerEntity> playerDelete = playerRepository.findOneByLastNameIgnoreCase(lastName);
        if (playerDelete.isEmpty()) {
            throw new PlayerNotFoundException(lastName);
        }

        try {
            playerRepository.delete(playerDelete.get());

            RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
            List<PlayerEntity> newRanking = rankingCalculator.getNewPlayersRanking();
            playerRepository.saveAll(newRanking);

        } catch (DataAccessException e) {
            throw new PlayerDataRetrievalException(e);
        }
    }
}