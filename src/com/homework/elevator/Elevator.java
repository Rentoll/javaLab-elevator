package com.homework.elevator;

import java.util.HashSet;

class Elevator extends Thread {

    private int currentFloor;
    private int movement;
    private HashSet<Integer> route;
    private Core core;

    Elevator(Core core) {
        super();
        route = new HashSet<>();
        currentFloor = 1;
        movement = 0;
        this.core = core;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getMovement() {
        return movement;
    }

    public void addRoutePoint(int floor) {
        synchronized (this){
            if (this.movement == 1 && floor > this.currentFloor || this.movement == -1 && floor < this.currentFloor || this.movement == 0) {
                route.add(floor);
            }
        }

        if (route.size() == 1) {
            if (floor < currentFloor) {
                movement = -1;
            }
            else {
                movement = 1;
            }
        }
    }

    public String drawRoute() {
        return route.toString();
    }

    public void goDown() throws InterruptedException {
        Thread.sleep(1000);
        this.currentFloor -= 1;
    }

    public void goUp() throws InterruptedException {
        Thread.sleep(1000);
        this.currentFloor += 1;
    }

    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    if (route.contains(currentFloor)) {
                        Thread.sleep(1000);
                        route.remove(currentFloor);
                    }

                    if (movement == -1) {
                        int[] clients = core.takeDownClientsByFloor(currentFloor);
                        if (clients.length != 0) {
                            Thread.sleep(1000);
                            for (int client : clients) {
                                route.add(client);
                            }
                        }
                        if (route.size() != 0) {
                            this.goDown();
                            continue;
                        }

                        boolean[] floorsWithClients = core.getDownClients();
                        for (int i = 0; i < floorsWithClients.length && i < currentFloor; i++) {
                            if (floorsWithClients[i]) {
                                route.add(i);
                            }
                        }

                        if (route.size() != 0) {
                            this.goDown();
                            continue;
                        }

                        movement = 0;
                    } else if (movement == 1) {
                        int[] clients = core.takeUpClientsByFloor(currentFloor);
                        if (clients.length != 0) {
                            Thread.sleep(1000);
                            for (int client : clients) {
                                route.add(client);
                            }
                        }
                        if (route.size() != 0) {
                            this.goUp();
                            continue;
                        }

                        boolean[] floorsWithClients = core.getUpClients();
                        for (int i = currentFloor; i < floorsWithClients.length; i++) {
                            if (floorsWithClients[i]) {
                                route.add(i);
                            }
                        }
                        if (route.size() != 0) {
                            this.goUp();
                            continue;
                        }

                        movement = 0;
                    } else {
                        boolean needToWorkFlag = false;

                        int []localDownClients = core.takeDownClientsByFloor(currentFloor);
                        if (localDownClients.length != 0) {
                            for (int i : localDownClients) {
                                route.add(i);
                                movement = -1;
                                needToWorkFlag = true;
                            }
                        }

                        if (needToWorkFlag) {
                            continue;
                        }

                        int []localUpClients = core.takeDownClientsByFloor(currentFloor);
                        if (localDownClients.length != 0) {
                            for (int i : localDownClients) {
                                route.add(i);
                                movement = 1;
                                needToWorkFlag = true;
                            }
                        }

                        if (needToWorkFlag) {
                            continue;
                        }

                        boolean[] upClients = core.getUpClients();
                        boolean[] downClients = core.getDownClients();

                        for (int i = 0; i < upClients.length; i++) {
                            if (upClients[i]) {
                                while (i > currentFloor) {
                                    goUp();
                                }
                                while (i < currentFloor) {
                                    goDown();
                                }
                                movement = 1;
                                needToWorkFlag = true;
                                break;
                            }
                        }
                        if (needToWorkFlag) {
                            continue;
                        }

                        for (int i = downClients.length - 1; i >= 0; i--) {
                            if (downClients[i]) {
                                while (i > currentFloor) {
                                    goUp();
                                }
                                while (i < currentFloor) {
                                    goDown();
                                }
                                movement = -1;
                                needToWorkFlag = true;
                                break;
                            }
                        }

                        if (needToWorkFlag) {
                            continue;
                        }

                        if (movement == 0) {
                            wait();
                        }
                    }
                }

            }
        }
        catch (InterruptedException e) {
            return;
        }
    }
}
