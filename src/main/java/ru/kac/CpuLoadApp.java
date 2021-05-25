package ru.kac;


import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class CpuLoadApp {


    @SneakyThrows
    public void run() {

    }


    @SneakyThrows
    public static void main(String[] args) {
        CpuLoadApp demoApp = new CpuLoadApp();
        demoApp.run();
    }

}
