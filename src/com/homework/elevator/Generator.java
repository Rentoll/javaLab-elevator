package com.homework.elevator;

class Generator extends Thread {

    private int floorsAmount;
    private Core core;

    Generator(Core core) {
        this.floorsAmount = core.getFloorsAmount();
        this.core = core;
    }

    public void run() {
        try {
            while (true) {
                int source = (int) (Math.random() * this.floorsAmount + 1);
                int target = (int) (Math.random() * this.floorsAmount + 1);
                if (source == target) {
                    continue;
                }
                else {
                    core.addClient(source, target);
                }
                Thread.sleep(1000);
            }
        }
        catch (InterruptedException e) {
            return;
        }
    }

}
