package ru.kac;


import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

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


    @SneakyThrows
    public void run() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();

        for (int i = 0; i < 100000; i++) {
            int avgCpu = percentLoadingCpu(cpu);
            int avgRam = percentMemFree(hal.getMemory());
            log.info("avgCpu = {}", avgCpu);
            log.info("avgRam = {}", avgRam);
            Thread.sleep(1000);
        }
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
