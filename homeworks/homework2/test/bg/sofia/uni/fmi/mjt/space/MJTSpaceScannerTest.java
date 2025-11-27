package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.algorithm.SymmetricBlockCipher;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJTSpaceScannerTest {

    private static SpaceScannerAPI scanner;
    private static SpaceScannerAPI emptySpaceScanner;
    private static SecretKey secretKey;

    private static final String EMPTY_DATA = "";

    private static final String ROCKET_DATA = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
        3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
        4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
        5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
        6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,13.0 m
        7,Vega,https://en.wikipedia.org/wiki/Vega_(rocket),29.9 m
        8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        10,VLS-1,https://en.wikipedia.org/wiki/VLS-1,19.0 m
        11,Volna,https://en.wikipedia.org/wiki/Volna,15.0 m
        12,Voskhod,https://en.wikipedia.org/wiki/Voskhod_(rocket),31.0 m
        13,Vostok,https://en.wikipedia.org/wiki/Vostok-K,31.0 m
        14,Vostok-2,https://en.wikipedia.org/wiki/Vostok-2_(rocket),
        15,Vostok-2A,https://en.wikipedia.org/wiki/Vostok_(rocket_family),
        16,Vostok-2M,https://en.wikipedia.org/wiki/Vostok-2M,
        17,Vulcan Centaur,https://en.wikipedia.org/wiki/Vulcan_%28rocket%29,58.3 m
        18,Zenit-2,https://en.wikipedia.org/wiki/Zenit-2,57.0 m
        19,Zenit-2 FG,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,57.0 m
        20,Zenit-3 SL,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,59.6 m
        21,Zenit-3 SLB,https://en.wikipedia.org/wiki/Zenit_%28rocket_family%29,57.0 m
        22,Zenit-3 SLBF,https://en.wikipedia.org/wiki/Zenit-3F,57.0 m
        23,Zéphyr,https://fr.wikipedia.org/wiki/Z%C3%A9phyr_(fus%C3%A9e),12.3 m
        24,ZhuQue-1,https://en.wikipedia.org/wiki/LandSpace,19.0 m
        25,ZhuQue-2,https://en.wikipedia.org/wiki/LandSpace#Zhuque-2,
        26,Angara 1.1,https://en.wikipedia.org/wiki/Angara_(rocket_family),35.0 m
        27,Angara 1.2,https://en.wikipedia.org/wiki/Angara_(rocket_family),41.5 m
        28,Angara A5/Briz-M,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
        29,Angara A5/DM-03,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
        30,Angara A5M,https://en.wikipedia.org/wiki/Angara_(rocket_family)#Angara_A5,
        """;

    private static final String MISSION_DATA = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
        1735,RVSN USSR,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Aug 29, 1991",Vostok-2M | IRS-1B,StatusRetired,,Success
        2106,RVSN USSR,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Oct 03, 1985",Vostok-2M | Cosmos 1689,StatusRetired,,Success
        2239,RVSN USSR,"Site 16/2, Plesetsk Cosmodrome, Russia","Fri Oct 28, 1983",Vostok-2M | Meteor-2 n†­11,StatusRetired,,Success
        2253,RVSN USSR,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Sun Jul 24, 1983",Vostok-2M | Cosmos 1484,StatusRetired,,Success
        3775,RVSN USSR,"Site 1/5, Baikonur Cosmodrome, Kazakhstan","Fri May 12, 1967",Vostok-2 | Cosmos 157,StatusRetired,,Success
        3786,RVSN USSR,"Site 41/1, Plesetsk Cosmodrome, Russia","Tue Apr 04, 1967",Vostok-2 | Cosmos 153,StatusRetired,,Success
        3794,RVSN USSR,"Site 41/1, Plesetsk Cosmodrome, Russia","Mon Mar 13, 1967",Vostok-2 | Cosmos 147,StatusRetired,,Success
        3798,RVSN USSR,"Site 1/5, Baikonur Cosmodrome, Kazakhstan","Mon Feb 27, 1967",Vostok-2 | Cosmos 143,StatusRetired,,Partial Failure
        4277,US Navy,"LC-18A, Cape Canaveral AFS, Florida, USA","Fri Sep 18, 1959",Vanguard | Vanguard 3,StatusRetired,,Partial Failure
        4285,US Navy,"LC-18A, Cape Canaveral AFS, Florida, USA","Mon Jun 22, 1959",Vanguard | Vanguard SLV-6,StatusRetired,,Failure
        551,VKS RF,"Site 35/1, Plesetsk Cosmodrome, Russia","Wed Jul 09, 2014",Angara 1.2 | Demo Flight,StatusActive,,Success
        """;

    @BeforeAll
    static void setup() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not fetch key generator.", e);
        }

        scanner = new MJTSpaceScanner(
            new StringReader(MISSION_DATA),
            new StringReader(ROCKET_DATA),
            secretKey
        );

        emptySpaceScanner = new MJTSpaceScanner(
            new StringReader(EMPTY_DATA),
            new StringReader(EMPTY_DATA),
            secretKey
        );
    }

//    public MJTSpaceScannerTest() {
//        try {
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//            secretKey = keyGenerator.generateKey();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Could not fetch key generator.", e);
//        }
//
//        scanner = new MJTSpaceScanner(
//            new StringReader(MISSION_DATA),
//            new StringReader(ROCKET_DATA),
//            secretKey
//        );
//
//        emptySpaceScanner = new MJTSpaceScanner(
//            new StringReader(EMPTY_DATA),
//            new StringReader(EMPTY_DATA),
//            secretKey
//        );
//    }

    @Test
    public void testGetAllMissions() {
        int expectedSize = 21;
        String firstMissionId = "0";
        String lastMissionId = "551";

        List<Mission> result = scanner.getAllMissions().stream().toList();
        assertEquals(expectedSize, result.size(), "There are 21 missions to get");
        assertEquals(firstMissionId, result.getFirst().id(), "Verify first mission data");
        assertEquals(lastMissionId, result.get(20).id(), "Verify last mission data");

        var emptyResult = emptySpaceScanner.getAllMissions();
        assertTrue(emptyResult.isEmpty(), "Should return empty collection when there are no missions");
    }

    @Test
    public void testGetAllMissionsWithStatus() {
        int expectedSizeSuccessful = 18;
        int expectedSizeFailure = 1;
        int expectedSizePartialFailure = 2;

        assertEquals(expectedSizeSuccessful, scanner.getAllMissions(MissionStatus.SUCCESS).size(),
            "There are 18 successful missions");
        assertEquals(expectedSizeFailure, scanner.getAllMissions(MissionStatus.FAILURE).size(),
            "There is 1 mission with status failure");
        assertEquals(expectedSizePartialFailure, scanner.getAllMissions(MissionStatus.PARTIAL_FAILURE).size(),
            "There are 2 missions with status partial failure");
        assertTrue(emptySpaceScanner.getAllMissions(MissionStatus.SUCCESS).isEmpty(),
            "Should return empty collection when there are no missions");
        assertThrows(IllegalArgumentException.class, () -> emptySpaceScanner.getAllMissions(null),
            "Should accept only correct arguments");
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissions() {
        String expectedCompany = "RVSN USSR";


        assertEquals(expectedCompany, scanner.getCompanyWithMostSuccessfulMissions(LocalDate.MIN, LocalDate.MAX),
            "Verifying most successful company");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getCompanyWithMostSuccessfulMissions(null, null),
            "Should accept only correct arguments");
        assertThrows(
            TimeFrameMismatchException.class,
            () -> emptySpaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.MAX, LocalDate.MIN),
            "To should be after from");
        assertTrue(emptySpaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.MIN, LocalDate.MAX).isEmpty(),
            "Should return empty collection when there are no missions");
    }

    @Test
    public void testGetMissionsPerCountry() {
        String expectedCountryName = "Russia";
        int numberOfMissionsForExpectedCompany = 4;

        assertTrue(scanner.getMissionsPerCountry().containsKey(expectedCountryName),
            "Missions per country should return right countries");
        assertEquals(numberOfMissionsForExpectedCompany,
            scanner.getMissionsPerCountry().get(expectedCountryName).size(), "RVSN USSR has 8 missions");
        assertTrue(emptySpaceScanner.getMissionsPerCountry().isEmpty(),
            "Should return empty collection when there are no missions");
    }

    @Test
    public void testGetTopNLeastExpensiveMissions() {
        String expectedMissionId = "1";

        assertEquals(expectedMissionId,
            scanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE).getFirst().id(),
            "Should return least expensive mission");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null),
            "Should accept only correct arguments");
    }

    @Test
    public void testGetMostDesiredLocationForMissionsPerCompany() {
        String companyToCheck = "RVSN USSR";
        String mostDesiredLocation = "Site 31/6, Baikonur Cosmodrome, Kazakhstan";

        assertEquals(mostDesiredLocation, scanner.getMostDesiredLocationForMissionsPerCompany().get(companyToCheck),
            "Most desired location for RVSN USSR is in Kazakhstan");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        String expectedCompany = "US Navy";
        String expectedLocation = "";

        assertEquals(expectedLocation,
            scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.MIN, LocalDate.MAX).get(expectedCompany),
            "US Navy has no successful missions");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getCompanyWithMostSuccessfulMissions(null, null),
            "Should accept only correct arguments");
        assertThrows(
            TimeFrameMismatchException.class,
            () -> emptySpaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.MAX, LocalDate.MIN),
            "To should be after from");
    }

    @Test
    public void testGetAllRockets() {
        int expectedSize = 31;

        assertEquals(expectedSize, scanner.getAllRockets().size(), "There are 31 rockets");

    }

    @Test
    public void testGetTopNTallestRockets() {
        String tallestRocketId = "20";


        assertEquals(tallestRocketId, scanner.getTopNTallestRockets(3).getFirst().id(),
            "Tallest rocket is Zenit-3 SL, id=20");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getTopNTallestRockets(-1),
            "Should accept only correct arguments");
    }

    @Test
    public void testGetWikiPageForRocket() {
        String expectedRocket = "Tsyklon-3";
        String expectedWikiPage = "https://en.wikipedia.org/wiki/Tsyklon-3";

        assertTrue(scanner.getWikiPageForRocket().get(expectedRocket).isPresent(),
            "Should have key of expected rocket");
        assertEquals(expectedWikiPage, scanner.getWikiPageForRocket().get(expectedRocket).get(),
            "Should return correct wiki");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsExceptions() {
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-1, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, null,
                RocketStatus.STATUS_ACTIVE),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, MissionStatus.SUCCESS, null),
            "Should accept only correct arguments");
    }

    @Test
    public void testSaveMostReliableRocket() throws CipherException {
        String mostReliableRocketName = "Vostok-2M";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.saveMostReliableRocket(null, null, null),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.saveMostReliableRocket(outputStream, null, null),
            "Should accept only correct arguments");
        assertThrows(IllegalArgumentException.class,
            () -> emptySpaceScanner.saveMostReliableRocket(outputStream, LocalDate.MIN, null),
            "Should accept only correct arguments");
        assertThrows(
            TimeFrameMismatchException.class,
            () -> emptySpaceScanner.saveMostReliableRocket(outputStream, LocalDate.MAX, LocalDate.MIN),
            "To should be after from");

        scanner.saveMostReliableRocket(outputStream, LocalDate.MIN, LocalDate.MAX);

        SymmetricBlockCipher cipher = new Rijndael(secretKey);
        ByteArrayInputStream inputSteam = new ByteArrayInputStream(outputStream.toByteArray());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        cipher.decrypt(inputSteam, resultStream);
        assertEquals(mostReliableRocketName, resultStream.toString(), "Result should contain most reliable rocket");
    }

}
