package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DeviceTest {

    IoTDeviceBase baseDevice;
    AmazonAlexa alexaDevice;
    WiFiThermostat thermostatDevice;
    RgbBulb bulbDevice;
    LocalDateTime timeToTest;

    DeviceTest() {
        timeToTest = LocalDateTime.now();
        baseDevice = new RgbBulb("name1", 1.1, timeToTest.minusHours(10));
        alexaDevice = new AmazonAlexa("name2", 2.1, timeToTest.minusHours(20));
        thermostatDevice = new WiFiThermostat("name3", 3.1, timeToTest.minusHours(30));
        bulbDevice = new RgbBulb("name4", 4.1, timeToTest.minusHours(40));
    }

    @Test
    void testGetName() {
        assertEquals("name1", baseDevice.getName());
    }

    @Test
    void testGetPowerConsumption() {
        assertEquals(1.1, baseDevice.getPowerConsumption());
    }

    @Test
    void testGetInstallationTime() {
        assertEquals(timeToTest.minusHours(10), baseDevice.getInstallationDateTime());
    }

    @Test
    void testRegistration() {
        LocalDateTime registration = timeToTest.minusHours(10);

        baseDevice.setRegistration(registration);

        //Could fail
        assertEquals(Duration.between(registration, LocalDateTime.now()).toHours(), baseDevice.getRegistration());
    }

    @Test
    void testGetType() {
        assertEquals(DeviceType.BULB, bulbDevice.getType());
    }
}
