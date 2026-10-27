package ru.vit4liy.base.util;

import ru.vit4liy.App;
import ru.vit4liy.it72h.lib.logger.log.Logger;
import ru.vit4liy.it72h.lib.logger.output.Output;
import ru.vit4liy.it72h.lib.logger.output.OutputPreset;

public class Log {
    private final static Logger LOGGER = App.getLogger();
    private final static boolean DEBUG_MOD = App.getConfig().getBoolean("debug", false);

    public static void out(String text){
        if(DEBUG_MOD) LOGGER.out(Output.of(OutputPreset.DEFAULT, text));
    }

    public static void err(String text){
        if(DEBUG_MOD) LOGGER.out(Output.of(OutputPreset.ERROR, text));
    }

    public static void wrn(String text){
        if(DEBUG_MOD) LOGGER.out(Output.of(OutputPreset.WARN, text));
    }
}
