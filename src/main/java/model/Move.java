/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Alper
 */


public class Move {
    public int x, y;
    public Stone color;

    public Move(int x, int y, Stone color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
