package bg.sofia.uni.fmi.mjt.intelligenthome.storage;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageTest {

    private final MapDeviceStorage storage;
    private final IoTDevice device1, device2, device3, deviceNew;

    StorageTest() {
        storage = new MapDeviceStorage();
        device1 = new AmazonAlexa("name1", 1.1, LocalDateTime.now().minusHours(10));
        device2 = new RgbBulb("name2", 1.1, LocalDateTime.now().minusHours(20));
        device3 = new WiFiThermostat("name3", 1.1, LocalDateTime.now().minusHours(30));
        deviceNew = new WiFiThermostat("name4", 1.1, LocalDateTime.now().minusHours(40));
    }

    @BeforeEach
    void setUpStorage() {
        storage.store(device1.getId(), device1);
        storage.store(device2.getId(), device2);
        storage.store(device3.getId(), device3);
    }

    @Test
    void testStoreDevice() {
        storage.store(deviceNew.getId(), deviceNew);

        assertEquals(deviceNew, storage.get(deviceNew.getId()));
    }

    @Test
    void testGetDevice() {
        assertEquals(device1, storage.get(device1.getId()));
        assertEquals(device2, storage.get(device2.getId()));
    }

    @Test
    void testExists() {
        assertTrue(storage.exists(device1.getId()));
        assertFalse(storage.exists(deviceNew.getId()));
    }

    @Test
    void testDelete() {
        assertTrue(storage.delete(device1.getId()));
        assertFalse(storage.delete(deviceNew.getId()));
    }

    @Test
    void testListAll() {
        Set<IoTDevice> devices = Set.of(device2, device1, device3);
        Collection<IoTDevice> result = storage.listAll();

        assertTrue(devices.size() == result.size() && devices.containsAll(result) && result.containsAll(devices));
    }

}
