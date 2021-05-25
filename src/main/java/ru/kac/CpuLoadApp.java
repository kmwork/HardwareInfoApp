package ru.kac;


import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

@Slf4j
@NoArgsConstructor
public class CpuLoadApp {

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
            log.info("avgCpu = {}", avgCpu);
            Thread.sleep(1000);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        CpuLoadApp demoApp = new CpuLoadApp();
        demoApp.run();
    }

}
