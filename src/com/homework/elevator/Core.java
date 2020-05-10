package com.homework.elevator;

import java.util.ArrayList;
import java.util.HashSet;

class Core extends Thread {
    private ArrayList<HashSet<Integer>> upClients;
    private ArrayList<HashSet<Integer>> downClients;
    ArrayList<Elevator> lifts;
    private int floorsAmount;
    private int signal;

    Core(int floorsAmount, int liftsAmount){
        this.signal = 1;
        this.floorsAmount = floorsAmount;
        lifts = new ArrayList<>(liftsAmount);
        upClients = new ArrayList<>(floorsAmount);
        downClients = new ArrayList<>(floorsAmount);
        for (int i = 0; i < floorsAmount; i++){

            upClients.add(new HashSet<Integer>());
            downClients.add(new HashSet<Integer>());
        }
        for (int i = 0; i < liftsAmount; i++){
            lifts.add(i, new Elevator(this));
        }
    }

    public void run(){
        for (Elevator elevator : this.lifts){
            elevator.start();
        }
        SignalScanner signalScanner = new SignalScanner(this);
        Generator generator = new Generator(this);
        signalScanner.start();
        generator.start();
        while (signal != 0)
        {
            try {
                Thread.sleep(1000);
                this.draw();
            } catch (InterruptedException e) {}
        }
        signalScanner.interrupt();
        generator.interrupt();
        for (Elevator elevator : lifts){
            elevator.interrupt();
        }
    }

    public void draw() {
        int[] currentFloors = new int[lifts.size()];
        for (int i = 0; i < lifts.size(); i++) {
            currentFloors[i] = lifts.get(i).getCurrentFloor();
        }

        for (int i = floorsAmount - 1; i >= 0; i--) {
            System.out.print(i + " floor");
            System.out.print(": ");
            for (int j = 0; j < currentFloors.length; j++) {
                if (i == currentFloors[j]){
                    System.out.print('█');
                }
                else{
                    System.out.print('-');
                }
            }
            System.out.print(' ');
            System.out.print(this.downClients.get(i));
            System.out.print(this.upClients.get(i));
            System.out.print('\n');
        }

        System.out.print("Lifts state:\n");
        for (Elevator elevator : lifts){
            System.out.print("First lift " + elevator.getMovement());
            System.out.print(": ");
            System.out.print(elevator.drawRoute());
            System.out.print('\n');
        }
        System.out.print("------------------------------------\n");
    }



    public int getFloorsAmount() {
        return floorsAmount;
    }

    public boolean[] getUpClients() {
        boolean booleanArrayOfUpClients[] = new boolean[floorsAmount];
        synchronized (this.upClients) {
            for (int i = 0; i < floorsAmount; i++) {
                if (upClients.get(i).size()>0) {
                    booleanArrayOfUpClients[i] = true;
                }
                else{
                    booleanArrayOfUpClients[i] = false;
                }
            }
        }
        return booleanArrayOfUpClients;
    }

    public boolean[] getDownClients() {
        boolean booleanArrayOfDownClients[] = new boolean[floorsAmount];
        synchronized (this.downClients) {
            for (int i = 0; i < floorsAmount; i++) {
                if (downClients.get(i).size() > 0) {
                    booleanArrayOfDownClients[i] = true;
                }
                else {
                    booleanArrayOfDownClients[i] = false;
                }
            }
        }
        return booleanArrayOfDownClients;
    }

    public void addClient(int source, int target) {
        source--;
        target--;
        if (source > target) {
            synchronized (downClients) {
                downClients.get(source).add(target);
            }
        }
        if (source < target) {
            synchronized (upClients) {
                upClients.get(source).add(target);
            }
        }
        for (Elevator elevator : lifts) {
            synchronized (elevator) {
                elevator.notify();
            }
        }
    }

    public int[] takeUpClientsByFloor(int floor) {
        synchronized (upClients) {
            int result[] = new int [upClients.get(floor).size()];
            int index = 0;
            for (int elem : upClients.get(floor)){
                result[index] = elem;
            }
            upClients.get(floor).clear();
            return result;
        }
    }

    public int[] takeDownClientsByFloor(int floor) {
        synchronized (downClients) {
            int result[] = new int [downClients.get(floor).size()];
            int index = 0;
            for (int elem : downClients.get(floor)){
                result[index] = elem;
            }
            downClients.get(floor).clear();
            return result;
        }
    }

    public void changeSignal(int signal){
        this.signal = signal;
    }
}