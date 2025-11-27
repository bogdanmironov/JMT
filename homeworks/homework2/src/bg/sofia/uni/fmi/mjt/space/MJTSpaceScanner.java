package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final List<Rocket> rockets;
    private final List<Mission> missions;
    private final SecretKey secretKey;

    //Can I move it to be a local variable in the function, seems a bit random here
    private static final String NO_COMPANY_WITH_SUCCESSFUL_MISSIONS_RETURN = "";
    private static final String NO_MISSIONS_FOUND_FOR_COMPANY = "";
    private static final String LOCATION_DELIMITER = ",";
    private static final double ROCKET_RELIABILITY_NO_MISSIONS = 0.0;
    private static final int ROCKET_RELIABILITY_SUCCESSFUL_MULTIPLIER = 2;
    private static final int ROCKET_RELIABILITY_ALL_MULTIPLIER = 2;
    private static final int NUMBER_OF_LINES_WITH_METADATA = 1;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {

        var bufferedReaderRockets = new BufferedReader(rocketsReader);
        rockets = bufferedReaderRockets
            .lines()
            .skip(NUMBER_OF_LINES_WITH_METADATA)
            .map(Rocket::of)
            .toList();
        var bufferedReaderMissions = new BufferedReader(missionsReader);
        missions = bufferedReaderMissions
            .lines()
            .skip(NUMBER_OF_LINES_WITH_METADATA)
            .map(Mission::of)
            .toList();

        this.secretKey = secretKey;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }

        return missions.stream()
            .filter(mission -> missionStatus.equals(mission.missionStatus()))
            .toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("End date cannot be before start date");
        }

        return missions.stream()
            .filter(mission -> missionIsBetween(mission, from, to))
            .collect(Collectors.groupingBy(Mission::company))
            .entrySet().stream()
            .max(Comparator.comparingInt(entry -> entry.getValue().size()))
            .map(Map.Entry::getKey)
            .orElse(NO_COMPANY_WITH_SUCCESSFUL_MISSIONS_RETURN);
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
            .collect(Collectors.groupingBy(this::getCountry, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        return missions.stream()
            .filter(mission -> missionStatus.equals(mission.missionStatus()))
            .filter(mission -> rocketStatus.equals(mission.rocketStatus()))
            .sorted(Comparator.comparing(e -> e.cost().orElse(Double.MAX_VALUE)))
            .filter(mission -> mission.cost().isPresent())
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
            .collect(Collectors.groupingBy(Mission::company))
            .entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), getMostUsedLocationForCompany(entry.getValue())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("End date cannot be before start date");
        }

        return missions.stream()
            .filter(mission -> missionIsBetween(mission, from, to))
            .collect(Collectors.groupingBy(Mission::company))
            .entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), getMostSuccessfulLocation(entry.getValue())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        return rockets.stream()
            .sorted(Comparator.comparingDouble((Rocket rocket) -> rocket.height().orElse(Double.MIN_VALUE)).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets
            .stream()
            .collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status cannot be null");
        }

        if (n <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        return missions.stream()
            .filter(mission -> missionStatus.equals(mission.missionStatus()))
            .filter(mission -> rocketStatus.equals(mission.rocketStatus()))
            .sorted(Comparator.comparing((Mission mission) -> mission.cost().orElse(Double.MAX_VALUE)).reversed())
            .map(this::getRocketForMission)
            .map(rocket ->
                rocket.flatMap(Rocket::wiki)
                    .orElse(null)
            )
            .filter(Objects::nonNull)
            .limit(n)
            .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("End date cannot be before start date");
        }

        Rocket mostReliable = missions
            .stream()
            .filter(mission -> missionIsBetween(mission, from, to))
            .map(this::getRocketForMission)
            .flatMap(Optional::stream)
            .max(Comparator.comparingDouble(this::getReliabilityOfRocket))
            .orElseThrow(() -> new IllegalArgumentException("No missions found"));

        SymmetricBlockCipher cypher = new Rijndael(this.secretKey);

        InputStream mostReliableRocketName = new ByteArrayInputStream(mostReliable.name().getBytes());

        cypher.encrypt(mostReliableRocketName, outputStream);
    }

    private static boolean missionIsBetween(Mission mission, LocalDate from, LocalDate to) {
        return mission.date().isAfter(from) && mission.date().isBefore(to);
    }

    private String getCountry(Mission mission) {
        String lastTokenLocation = Arrays.stream(mission.location().split(LOCATION_DELIMITER))
            .reduce((_, second) -> second).orElseThrow(() -> new IllegalArgumentException("Mission has no location"));

        return lastTokenLocation.trim();
    }

    private static String getMostUsedLocationForCompany(List<Mission> missionsForCompany) {
        return missionsForCompany
            .stream()
            .map(Mission::location)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(NO_MISSIONS_FOUND_FOR_COMPANY);
        //Is empty string better return than null

    }

    private String getMostSuccessfulLocation(List<Mission> missions) {
        List<Mission> successfulMissions = missions.stream()
            .filter(it -> it.missionStatus().equals(MissionStatus.SUCCESS))
            .toList();

        return getMostUsedLocationForCompany(successfulMissions);
    }

    private Optional<Rocket> getRocketForMission(Mission mission) {
        return rockets
            .stream()
            .filter(rocket -> mission.detail().rocketName().equals(rocket.name()))
            .findFirst();
    }

    private Double getReliabilityOfRocket(Rocket rocket) {
        List<Mission> rocketMissions = missions
            .stream()
            .filter(mission -> rocket.name().equals(mission.detail().rocketName()))
            .toList();

        List<Mission> successfulMissions = rocketMissions
            .stream()
            .filter(mission -> MissionStatus.SUCCESS.equals(mission.missionStatus()))
            .toList();

        if (rocketMissions.isEmpty()) {
            return ROCKET_RELIABILITY_NO_MISSIONS;
        } else {
            double reliabilityNumerator =
                (ROCKET_RELIABILITY_SUCCESSFUL_MULTIPLIER * successfulMissions.size())
                    + (rocketMissions.size() - successfulMissions.size());

            double reliabilityDenominator = ROCKET_RELIABILITY_ALL_MULTIPLIER * rocketMissions.size();

            return reliabilityNumerator / reliabilityDenominator;
        }
    }
}
