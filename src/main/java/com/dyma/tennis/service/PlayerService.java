package com.dyma.tennis.service;

import com.dyma.tennis.Player;
import com.dyma.tennis.PlayerToSave;
import com.dyma.tennis.Rank;
import com.dyma.tennis.data.PlayerEntity;
import com.dyma.tennis.data.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        log.info("Invoking getAllPlayers()");
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
            log.error("Could not retrieve players", e);
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player getByLastName(String lastName) {
        log.info("Invoking getByLastName with lastName={}", lastName);
        try {
            Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(lastName);
            if (player.isEmpty()) {
                log.warn("Could not find player with lastName={}", lastName);
                throw new PlayerNotFoundException(lastName);
            }
            return new Player(
                    player.get().getFirstName(),
                    player.get().getLastName(),
                    player.get().getBirthDate(),
                    new Rank(player.get().getRank(), player.get().getPoints())
            );
        } catch (DataAccessException e) {
            log.error("Could not find player with lastName={}", lastName, e);
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player create(PlayerToSave playerToSave) {
        log.info("Invoking create with playerToSave={}", playerToSave);
        try {
            Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
            if (player.isPresent()) {
                log.warn("Player to create with lastName={} already exists", playerToSave.lastName());
                throw new PlayerAlreadyExistsException(playerToSave.lastName());
            }

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
            log.error("Could not create player={}", playerToSave, e);
            throw new PlayerDataRetrievalException(e);
        }
    }

    public Player update(PlayerToSave playerToSave) {
        log.info("Invoking update with playerToSave={}", playerToSave);
        try {
            Optional<PlayerEntity> playerToUpdate = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
            if (playerToUpdate.isEmpty()) {
                log.warn("Could not find player to update with lastName={}", playerToSave.lastName());
                throw new PlayerNotFoundException(playerToSave.lastName());
            }

            playerToUpdate.get().setFirstName(playerToSave.firstName());
            playerToUpdate.get().setBirthDate(playerToSave.birthDate());
            playerToUpdate.get().setPoints(playerToSave.points());
            PlayerEntity updatedPlayer = playerRepository.save(playerToUpdate.get());

            RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
            List<PlayerEntity> newRanking = rankingCalculator.getNewPlayersRanking();
            playerRepository.saveAll(newRanking);

            return getByLastName(updatedPlayer.getLastName());
        } catch (DataAccessException e) {
            log.error("Could not update player={}", playerToSave, e);
            throw new PlayerDataRetrievalException(e);
        }
    }

    public void delete(String lastName) {
        log.info("Invoking delete with lastName={}", lastName);
        try {
            Optional<PlayerEntity> playerDelete = playerRepository.findOneByLastNameIgnoreCase(lastName);
            if (playerDelete.isEmpty()) {
                log.warn("Could not find player to delete with lastName={}", lastName);
                throw new PlayerNotFoundException(lastName);
            }

            playerRepository.delete(playerDelete.get());

            RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
            List<PlayerEntity> newRanking = rankingCalculator.getNewPlayersRanking();
            playerRepository.saveAll(newRanking);

        } catch (DataAccessException e) {
            log.error("Could not delete player with lastName={}", lastName, e);
            throw new PlayerDataRetrievalException(e);
        }
    }
}