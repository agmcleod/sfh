package com.agmcleod.sfh;

import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Constructor;

/**
 * Created by aaronmcleod on 15-03-06.
 */
public class ObjectMapToClass {
    public static Object getInstanceOfObject(ObjectMap<String, String> entities, String name, Game game) {
        try {
            Class<?> objectClass = Class.forName(entities.get(name));
            Constructor<?> constructor = objectClass.getConstructor(Game.class, float.class, float.class);
            return constructor.newInstance(game, 0, 0);
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println("not found");
        }
        catch (java.lang.InstantiationException e) {
            System.out.println("Instantiation exception");
        }
        catch (java.lang.IllegalAccessException e) {
            System.out.println("Illegal access");
        }
        catch (java.lang.NoSuchMethodException e) {
            System.out.println("No such method: " + e.getMessage());
        }
        catch (java.lang.reflect.InvocationTargetException e) {
            System.out.println("Could not invoke");
        }

        return null;
    }
}
