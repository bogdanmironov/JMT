package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.*;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {

    @Mock
    private DeviceStorage storage;

    @InjectMocks
    private IntelligentHomeCenter center;

    @BeforeAll
    static void setup() {

    }

    @Test
    void testRegister() throws DeviceAlreadyRegisteredException {
        IoTDevice device = Mockito.mock();

        when(device.getId()).thenReturn("1");
        when(storage.exists(device.getId())).thenReturn(false);

        center.register(device);

        verify(storage).store(device.getId(), device);
        verify(device).setRegistration(any());
    }

    @Test
    void testRegisterWhenDeviceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> center.register(null),
                "Registering null should result in IllegalArgumentException.");
    }

    @Test
    void testRegisterDuplicateDevice() {
        IoTDevice device = Mockito.mock();

        when(device.getId()).thenReturn("1");
        when(storage.exists(device.getId())).thenReturn(true);
        //Is there a difference with any

        assertThrows(DeviceAlreadyRegisteredException.class, () -> center.register(device),
                "Should not be able to register duplicate devices");
    }

    @Test
    void testUnregister() throws DeviceNotFoundException {
        IoTDevice device = Mockito.mock();

        when(device.getId()).thenReturn("1");
        when(storage.exists(device.getId())).thenReturn(true);

        center.unregister(device);

        verify(storage).delete(device.getId());
    }

    @Test
    void testUnregisterNull() {
        assertThrows(IllegalArgumentException.class, () -> center.unregister(null),
                "Unregistering null should throw exception.");
    }

    @Test
    void testUnregisterMissingDevice() {
        IoTDevice device = Mockito.mock();

        when(device.getId()).thenReturn("1");
        when(storage.exists(device.getId())).thenReturn(false);

        assertThrows(DeviceNotFoundException.class, () -> center.unregister(device),
                "Unregistering device that's not in storage should fail.");
    }

    @Test
    void testGetDeviceById() throws DeviceNotFoundException {
        String deviceId = "1";
        IoTDevice device = Mockito.mock();

        when(device.getId()).thenReturn(deviceId);
        when(storage.exists(deviceId)).thenReturn(true);
        when(storage.get(deviceId)).thenReturn(device);

        assertEquals(device, center.getDeviceById(device.getId()),
                "getDeviceById should return correct device");
    }

    @Test
    void testGetDeviceByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> center.getDeviceById(null),
                "Passing null to getDeviceById should fail");
    }

    @Test
    void testGetDeviceByIdBlank() {
        assertThrows(IllegalArgumentException.class, () -> center.getDeviceById(""),
                "Passing null to getDeviceById should fail");
    }

    @Test
    void testGetDeviceByIdNotFound() {
        String deviceId = "1";

        when(storage.exists(any())).thenReturn(false);

        assertThrows(DeviceNotFoundException.class, () -> center.getDeviceById(deviceId),
                "When storage does not find device getDeviceById should fail.");
    }

    @Test
    void testGetDeviceQuantityPerType() {
        DeviceType typeToTest = DeviceType.THERMOSTAT;
        List<IoTDevice> devices = List.of(
                new WiFiThermostat("Name1", 1.1, LocalDateTime.now()),
                new WiFiThermostat("Name2", 1.2, LocalDateTime.now()),
                new AmazonAlexa("Name3", 1.1, LocalDateTime.now()),
                new WiFiThermostat("Name4", 10.0, LocalDateTime.now())
        );

        when(storage.listAll()).thenReturn(devices);

        assertEquals(3, center.getDeviceQuantityPerType(typeToTest),
                "getDeviceQuantityPerType should return the correct amount of items");

    }


    @Test
    void testGetTopNDevicesByPowerConsumption() {
        int testTopN = 3;
        IoTDevice device1 = new WiFiThermostat("name1", 5L, LocalDateTime.now().minusHours(20));
        IoTDevice device2 = new RgbBulb("name2", 10L, LocalDateTime.now().minusHours(10));
        IoTDevice device3 = new WiFiThermostat("name3", 20L, LocalDateTime.now().minusHours(30));
        IoTDevice device4 = new AmazonAlexa("name4", 20L, LocalDateTime.now().minusHours(30));
        IoTDevice device5 = new WiFiThermostat("name5", 40L, LocalDateTime.now().minusHours(1));

        List<IoTDevice> devices = List.of(device1, device2, device3, device4, device5);
        List<String> resultDevices = List.of(device3.getId(), device4.getId(), device1.getId());

        when(storage.listAll()).thenReturn(devices);

        assertEquals(resultDevices.size(), center.getTopNDevicesByPowerConsumption(testTopN).size(),
                "getTopNDevicesByPowerConsumption should return the correct number of devices");

        assertEquals(resultDevices, center.getTopNDevicesByPowerConsumption(testTopN),
                "getTopNDevicesByPowerConsumption should return the correct devices");

    }

    @Test
    void testGetTopNDevicesByPowerConsumptionGetAll() {
        int testTopN = 3;
        IoTDevice device1 = new WiFiThermostat("name1", 5L, LocalDateTime.now().minusHours(20));
        IoTDevice device2 = new RgbBulb("name2", 10L, LocalDateTime.now().minusHours(10));

        List<IoTDevice> devices = List.of(device1, device2);
        List<String> resultDeviceIds = List.of(device1.getId(), device2.getId());

        when(storage.listAll()).thenReturn(devices);

        assertEquals(devices.size(), center.getTopNDevicesByPowerConsumption(testTopN).size(),
                "getTopNDevicesByPowerConsumption with N larger than list should return the correct number of devices");

        assertEquals(resultDeviceIds, center.getTopNDevicesByPowerConsumption(testTopN),
                "getTopNDevicesByPowerConsumption with N larger than list should return the all devices");
    }


    @Test
    void testGetTopNDevicesByPowerConsumptionNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> center.getTopNDevicesByPowerConsumption(-1),
                "Passing negative N to getTopNDevicesByPowerConsumption should fail");
    }

    @Test
    void getFirstNDevicesByRegistration() {
        int testN = 2;

        IoTDevice device1 = Mockito.mock();
        IoTDevice device2 = Mockito.mock();
        IoTDevice device3 = Mockito.mock();

        when(device1.getRegistration()).thenReturn(10L);
        when(device2.getRegistration()).thenReturn(20L);
        when(device3.getRegistration()).thenReturn(30L);

        when(storage.listAll()).thenReturn(List.of(device1, device2, device3));

        assertEquals(List.of(device3, device2), center.getFirstNDevicesByRegistration(testN),
                "getFirstNDevicesByRegistration should return correct devices");
    }

    @Test
    void getFirstNDevicesByRegistrationGetAll() {
        int testN = 2;

        IoTDevice device1 = Mockito.mock();

//        when(device1.getRegistration()).thenReturn(10L);

        when(storage.listAll()).thenReturn(List.of(device1));

        assertEquals(List.of(device1), center.getFirstNDevicesByRegistration(testN),
                "getFirstNDevicesByRegistration should return correct devices");
    }

    @Test
    void testGetFirstNDevicesByRegistrationNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> center.getFirstNDevicesByRegistration(-1),
                "Passing negative N to getFirstNDevicesByRegistration should fail");
    }
}
