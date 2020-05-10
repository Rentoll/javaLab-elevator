package com.homework.elevator;

import java.util.Scanner;

class SignalScanner extends Thread {

    private Core core;

    SignalScanner(Core core) {
        super();
        this.core = core;
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        while (true) {
            String line = in.nextLine();
            if (line.equals("0")) {
                core.changeSignal(0);
                return;
            }
        }
    }
}
