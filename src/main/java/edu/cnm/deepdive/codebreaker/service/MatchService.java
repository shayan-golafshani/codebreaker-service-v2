package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.MatchRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchService {

  private final MatchRepository matchRepository;
  private final Random rng;

  @Autowired
  public MatchService(MatchRepository matchRepository, Random rng) {
    this.matchRepository = matchRepository;
    this.rng = rng;
  }

  public Match startMatch(Match match, User user) {
    char[] poolCharacters = match.getPool().toCharArray();
    match.setOriginator(user);
    Set<Game> games = match.getGames();
    int codeLength = match.getCodeLength();
    StringBuilder builder = new StringBuilder(codeLength);
    for (int i = 0; i < match.getGameCount(); i++) {
      Game game = new Game();
      game.setMatch(match);
      for (int j = 0; j < codeLength; j++) {
        builder.append(poolCharacters[rng.nextInt(poolCharacters.length)]);
      }
      game.setCode(builder.toString());
      builder.setLength(0);
      games.add(game);
    }
    return matchRepository.save(match);
  }

  public Optional<Match> get(UUID matchId) {
    return matchRepository.findById(matchId);
  }

  public Optional<Set<Game>> getGames(UUID matchId) {
    return get(matchId)
        .map(Match::getGames);
  }

  public static class MatchNotFoundException extends ResponseStatusException {

    private static final String NOT_FOUND_REASON = "Match not found";

    public MatchNotFoundException() {
      super(HttpStatus.NOT_FOUND, NOT_FOUND_REASON);
    }

  }

}
