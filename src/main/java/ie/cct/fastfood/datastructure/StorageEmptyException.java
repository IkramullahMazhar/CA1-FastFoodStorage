/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.cct.fastfood.datastructure;

/**
 *
 * @author Ikramullah Mazhar
 */


/** Thrown when we try to remove, peek or search but there is nothing stored. */
public class StorageEmptyException extends StorageException {

    public StorageEmptyException() {
        super("Storage is empty - there are no food items in the system.");
    }
}
