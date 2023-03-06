package com.example.inventory;
/**
 * Handles the data for the InHouse type of parts
 * */
public class InHouse extends Part{
    private int machineId;
    public InHouse(int id, String name, double price, int stock, int min, int max,int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId = machineId;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }
}
