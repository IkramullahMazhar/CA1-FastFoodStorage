/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ikramullah Mazhar
 */
package ie.cct.fastfood.datastructure;

/**
 * The two ways the chef can use the storage unit (from the brief):
 *
 * STACK - the front side is used for adding AND removing (Last In, First Out).
 * QUEUE - the front side is used for adding and the opposite (back) side
 *         for removing (First In, First Out).
 */
public enum StorageMode {

    STACK("Stack (LIFO) - add at front, remove from front"),
    QUEUE("Queue (FIFO) - add at front, remove from back");

    private final String description;

    StorageMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}