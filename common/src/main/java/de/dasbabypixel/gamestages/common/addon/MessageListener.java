package de.dasbabypixel.gamestages.common.addon;

public interface MessageListener<A extends Addon> {
    void handle(A addon, Object message);
}