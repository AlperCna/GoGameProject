/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Alper
 */

/*
 * Move.java
 *
 * Represents a single move in the Go game.
 * A move consists of x and y coordinates and the color of the stone placed.
 */


import java.util.Objects;

public class Move {
    public int x, y;
    public Stone color;

    /**
     * Creates a new Move object with coordinates and stone color.
     *
     * @param x      x-coordinate on the board
     * @param y      y-coordinate on the board
     * @param color  The color of the stone (BLACK or WHITE)
     */
    public Move(int x, int y, Stone color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    /**
     * Checks equality of two Move objects.
     * Two moves are equal if they have the same coordinates and stone color.
     *
     * @param obj Another object to compare
     * @return true if both moves are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return x == move.x && y == move.y && color == move.color;
    }

    /**
     * Generates a hash code for this move.
     * Useful when storing moves in hash-based collections.
     *
     * @return integer hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }
}