package ru.vit4liy.frontend.util;

import ru.vit4liy.backend.base.model.location.Location;

import java.util.ArrayList;
import java.util.List;


public final class LocationPathUtil {
    private LocationPathUtil() {}

    public static String buildPath(Location loc, List<Location> allLocations) {
        if (loc == null) {
            return "Не назначено";
        }

        List<String> path = new ArrayList<>();
        Location current = loc;

        while (current != null) {
            path.add(0, current.getDisplayName());

            if (current.getParentId() == null || current.getParentId() == 0) {
                break;
            }

            Location finalCurrent = current;
            current = allLocations.stream()
                    .filter(l -> l.getId() != null && l.getId().equals(finalCurrent.getParentId()))
                    .findFirst()
                    .orElse(null);
        }

        return path.isEmpty() ? "Неизвестно" : String.join(" > ", path);
    }
}