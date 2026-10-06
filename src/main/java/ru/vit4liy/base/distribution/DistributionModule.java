package ru.vit4liy.base.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vit4liy.App;
import ru.vit4liy.base.distribution.exports.ExportService;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;

import java.util.Set;

public class DistributionModule extends Module {
    private final ObjectMapper MAPPER = App.getObjectMapper();
    private ExportService exportService;

    public DistributionModule(ModuleLoader loader) {
        super(loader);
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        this.exportService = new ExportService(MAPPER);
    }

    @Override
    public void shutdown() throws ModuleShutdownException {

    }

    public ExportService getExportService() throws NullPointerException{
        if(exportService == null) Log.wrn("Export service is null!");
        return exportService;
    }

    @Override
    public String moduleName() {
        return "Distribution";
    }

    @Override
    protected Set<Class<? extends Module>> dependencies() {
        return Set.of();
    }
}
