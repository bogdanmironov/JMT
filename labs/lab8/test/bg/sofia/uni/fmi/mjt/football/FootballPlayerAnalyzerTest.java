package bg.sofia.uni.fmi.mjt.football;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FootballPlayerAnalyzerTest {
    private final String correctDataset = """
            name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
            L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
            C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
            P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
            L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
            K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
            V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
            K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
            S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
            M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right
            E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;89;89;60000000;200000;Right
            Sergio Busquets;Sergio Busquets i Burgos;7/16/1988;30;187.96;76.2;CDM,CM;Spain;89;89;51500000;315000;Right
            T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
            M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right
            A. Griezmann;Antoine Griezmann;3/21/1991;27;175.26;73;CF,ST;France;89;90;78000000;145000;Left
            """;

    private final String simplifiedCorrectDataset = """
            name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
            C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
            P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
            """;

    @Test
    public void testGetAllPlayers() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        List<Player> players = analyzer.getAllPlayers();

        assertEquals("L. Messi", players.getFirst().name());
        assertEquals("C. Eriksen", players.get(1).name());
        assertEquals("A. Griezmann", players.getLast().name());
    }
    @Test
    public void testGetAllPlayersEmpty() {
        var analyzer = new FootballPlayerAnalyzer(StringReader.nullReader());

        assertTrue(analyzer.getAllPlayers().isEmpty());
    }

    @Test
    public void testPlayerGeneration() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        List<Player> players = analyzer.getAllPlayers();
        Player playerAguero = players.get(7);

        assertEquals("S. Agüero", playerAguero.name());
        assertEquals("Sergio Leonel Agüero del Castillo", playerAguero.fullName());
        assertEquals(LocalDate.of(1988, 6, 2), playerAguero.birthDate());
        assertEquals(30, playerAguero.age());
        assertEquals(172.72, playerAguero.heightCm(), 0.5);
        assertEquals(69.9, playerAguero.weightKg());
        assertEquals(List.of(Position.ST), playerAguero.positions());
        assertEquals("Argentina", playerAguero.nationality());
        assertEquals(89, playerAguero.overallRating());
        assertEquals(89, playerAguero.potential());
        assertEquals(64_500_000, playerAguero.valueEuro());
        assertEquals(300_000, playerAguero.wageEuro());
        assertEquals(Foot.RIGHT, playerAguero.preferredFoot());
    }

    @Test
    public void testBrokenReader() throws IOException {
        Reader brokenReader = mock(Reader.class);
        when(brokenReader.read(any(), anyInt(), anyInt())).thenThrow(new IOException("Exception"));

        assertThrows(UncheckedIOException.class, () -> new FootballPlayerAnalyzer(brokenReader));
    }

    @Test
    public void testGetPlayersUnmodifiableList() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        List<Player> players = analyzer.getAllPlayers();

        assertThrows(UnsupportedOperationException.class, () -> players.remove(1));
    }

    @Test
    public void testGetAllNationalities() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        Set<String> nationalities = analyzer.getAllNationalities();

        assertEquals(Set.of("Argentina", "Denmark", "France", "Belgium", "Spain", "Uruguay", "Netherlands", "Germany", "Italy", "Senegal"), nationalities);
    }

    @Test
    public void testGetAllNationalitiesEmpty() {
        var analyzer = new FootballPlayerAnalyzer(StringReader.nullReader());

        assertTrue(analyzer.getAllNationalities().isEmpty());
    }

    @Test
    public void testGetAllNationalitiesUnmodifiableList() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        Set<String> nationalities = analyzer.getAllNationalities();

        assertThrows(UnsupportedOperationException.class, () -> nationalities.remove(1));
    }

    @Test
    public void testGetHighestPaidPlayerByNationality() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertEquals("L. Messi", analyzer.getHighestPaidPlayerByNationality("Argentina").name());
    }

    @Test
    public void testGetHighestPaidPlayerByNationalityMissing() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(NoSuchElementException.class, () -> analyzer.getHighestPaidPlayerByNationality("Bulgaria"));
    }

    @Test
    public void testGetHighestPaidPlayerByNationalityNull() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(IllegalArgumentException.class, () -> analyzer.getHighestPaidPlayerByNationality(null));
    }

    @Test
    public void testGroupByPosition() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(simplifiedCorrectDataset));
        Map<Position, Set<Player>> result = analyzer.groupByPosition();

        assertEquals(Set.of(Position.CM, Position.CAM, Position.RM), result.keySet());
        assertTrue(result.get(Position.CM).stream().map(Player::name).toList().containsAll(Set.of("C. Eriksen", "P. Pogba")));
        assertEquals(2, result.get(Position.CM).size());
        assertTrue(result.get(Position.CAM).stream().map(Player::name).toList().containsAll(Set.of("C. Eriksen", "P. Pogba")));
        assertEquals(2, result.get(Position.CAM).size());
        assertTrue(result.get(Position.RM).stream().map(Player::name).toList().contains("C. Eriksen"));
        assertEquals(1, result.get(Position.RM).size());
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudget() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertEquals("K. Mbappé", analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 1000000000).get().name());
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudgetNegativeBudget() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, -1));
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudgetNullPosition() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(IllegalArgumentException.class, () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 3));
    }

    @Test
    public void testGetTopProspectPlayerForPositionInBudgetNoMatches() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertFalse(analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 3).isPresent());
    }

    @Test
    public void testGetSimilarPlayers() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        //L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
        var player = new Player("L.Mossi", "Mossito", LocalDate.of(2002, 5, 5),
                2, 300, 150, List.of(Position.ST), "Mexico", 96, 97,
                50, 1, Foot.LEFT);

        assertEquals("L. Messi", analyzer.getSimilarPlayers(player).stream().findFirst().get().name());
    }

    @Test
    public void testGetSimilarPlayersNull() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(IllegalArgumentException.class, () -> analyzer.getSimilarPlayers(null));
    }

    @Test
    public void testGetPlayersByFullNameKeyword() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));
        Set<String> resultNames = analyzer.getPlayersByFullNameKeyword("é").stream().map(Player::fullName).collect(Collectors.toSet());

        assertEquals(3, resultNames.size());
        assertTrue(resultNames.containsAll(Set.of("Kylian Mbappé", "Lionel Andrés Messi Cuccittini", "Marc-André ter Stegen")));
    }

    @Test
    public void testGetPlayersByFullNameKeywordNull() {
        var analyzer = new FootballPlayerAnalyzer(new StringReader(correctDataset));

        assertThrows(IllegalArgumentException.class, () -> analyzer.getPlayersByFullNameKeyword(null));
    }
}
