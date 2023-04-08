package com.codeoftheweb.salvo.core.util;

import java.util.HashMap;
import java.util.Map;

public class ShipTypes {
    public static final Map<String, Integer> shipTypesAndSizes;

    static {
        shipTypesAndSizes = new HashMap<>();
        shipTypesAndSizes.put("carrier", 5);
        shipTypesAndSizes.put("battleship", 4);
        shipTypesAndSizes.put("submarine", 3);
        shipTypesAndSizes.put("destroyer", 3);
        shipTypesAndSizes.put("patrol boat", 2);
    }
}
