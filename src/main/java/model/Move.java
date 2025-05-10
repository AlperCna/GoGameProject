/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Alper
 */

import java.util.Objects;

public class Move {
    public int x, y;
    public Stone color;

    public Move(int x, int y, Stone color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

  @Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Move move = (Move) obj;
    return x == move.x && y == move.y && color == move.color;
}

@Override
public int hashCode() {
    return java.util.Objects.hash(x, y, color);
}
}