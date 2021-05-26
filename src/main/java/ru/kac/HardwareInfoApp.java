package ru.kac;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

@Slf4j
@NoArgsConstructor
public class HardwareInfoApp {

    long[] prevTicks = new long[CentralProcessor.TickType.values().length];

    public int percentLoadingCpu(CentralProcessor cpu) {
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = cpu.getSystemCpuLoadTicks();
        System.out.println("cpuLoad : " + cpuLoad);
        return (int) Math.round(cpuLoad);
    }


    @AllArgsConstructor
    @ToString
    private static class EthernetTraffic {
        private long allTrafficBytes;
        private long nanoTime;
        private long speedBitsOnSec;
    }


    @SneakyThrows
    public void run() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        NetworkIF currentEthernet = getNetByKey(hal.getNetworkIFs(), new String[]{"enp", "wlfx", "Ethernet"});
        EthernetTraffic prevTraffic = calcTraffic(currentEthernet);
        for (int i = 0; i < 100000; i++) {
            int avgCpu = percentLoadingCpu(hal.getProcessor());
            int avgRam = percentMemFree(hal.getMemory());

            prevTraffic = speedBitsOnSec(currentEthernet, prevTraffic);
            long speedBitsOnSec = prevTraffic == null ? -1 : prevTraffic.speedBitsOnSec;
            log.info("avgCpu = {}", avgCpu);
            log.info("avgRam = {}", avgRam);
            log.info("speedBitsOnSec = {} bits per second", speedBitsOnSec);
            Thread.sleep(1000);
        }
    }


    private NetworkIF getNetByKey(List<NetworkIF> networkIFs, String[] keysOfNet) {
        for (NetworkIF n : networkIFs) {
            String name = n.getDisplayName();
            if (name != null) {
                for (String key : keysOfNet) {
                    if (name.indexOf(key) >= 0) {
                        return n;
                    }
                }
            }
        }
        return null;
    }

    private EthernetTraffic calcTraffic(NetworkIF currentEthernet) {
        if (currentEthernet == null)
            return null;
        currentEthernet.updateAttributes();
        return new EthernetTraffic(currentEthernet.getBytesRecv() + currentEthernet.getBytesSent(), System.nanoTime(), 0);
    }

    private EthernetTraffic speedBitsOnSec(NetworkIF currentEthernet, EthernetTraffic prevTraffic) {
        if (prevTraffic == null)
            return null;
        EthernetTraffic currentTraffic = calcTraffic(currentEthernet);
        long deltaTime = currentTraffic.nanoTime - prevTraffic.nanoTime;
        long deltaBytes = currentTraffic.allTrafficBytes - prevTraffic.allTrafficBytes;
        double deltaBits = deltaBytes * 8;
        deltaBits = deltaBits * 1_000_000_000 / deltaTime;
        currentTraffic.speedBitsOnSec = (long) Math.round(deltaBits);
        return currentTraffic;
    }


    private int percentMemFree(GlobalMemory memory) {
        double used = memory.getTotal() - memory.getAvailable();
        double result = used * 100 / memory.getTotal();
        log.debug("[RAM] Available = {}, Total = {}", memory.getAvailable(), memory.getTotal());
        return (int) Math.round(result);
    }

    @SneakyThrows
    public static void main(String[] args) {
        HardwareInfoApp demoApp = new HardwareInfoApp();
        demoApp.run();
    }

}
