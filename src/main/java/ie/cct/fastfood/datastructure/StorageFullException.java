/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ikramullah Mazhar
 */
package ie.cct.fastfood.datastructure;

/** Thrown when an item is added but all trays are already in use. */
public class StorageFullException extends StorageException {

    public StorageFullException(int capacity) {
        super("Storage is full - all " + capacity + " trays are in use. Remove an item first.");
    }
}