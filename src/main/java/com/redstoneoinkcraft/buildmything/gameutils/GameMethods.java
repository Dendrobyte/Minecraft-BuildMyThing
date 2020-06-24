package com.redstoneoinkcraft.buildmything.gameutils;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class GameMethods {

    private GameMethods instance;

    public GameMethods getInstance() {
        if(instance == null){
            instance = new GameMethods();
        }
        return instance;
    }
}
