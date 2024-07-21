package com.tonni.notifx.models;

public class ApiTurn {
    private int turn_number;

    public ApiTurn(int turn_number) {
        this.turn_number = turn_number;
    }

    public int getTurn_number() {
        return turn_number;
    }

    public void setTurn_number(int turn_number) {
        this.turn_number = turn_number;
    }
}
