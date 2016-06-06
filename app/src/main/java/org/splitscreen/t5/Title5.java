package org.splitscreen.t5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IPat (Local) on 28.04.2016.
 */
public class Title5 {

    public final static String LOG_TAG = "title.5";

    public static Random random;
    public static List<String> titleList;

    // TODO: There can be static data like pictures etc here. The idea is that this doesn't get deleted on Runtime
    // and can be used as a center for different Classes.

    public static void prepareRand() {
        random = new Random();
    }

    public static boolean fetchTitleList() {
        titleList = new ArrayList<>();
        titleList.add("Adventure");
        titleList.add("Secret Agent");
        titleList.add("Nature");
        titleList.add("Water");
        titleList.add("Sports");
        titleList.add("M");
        titleList.add("Why is this title that long? Like, shit. Damn son.");
        return true;
    }
}
