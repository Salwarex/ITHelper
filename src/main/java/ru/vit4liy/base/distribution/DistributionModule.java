package ru.vit4liy.base.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vit4liy.App;
import ru.vit4liy.base.bus.BackendDataBus;
import ru.vit4liy.base.distribution.exports.ExportService;
import ru.vit4liy.base.distribution.imports.ImportService;
import ru.vit4liy.base.util.Log;
import ru.vit4liy.frontend.util.ClientUser;
import ru.vit4liy.modular.Module;
import ru.vit4liy.modular.ModuleLoader;
import ru.vit4liy.modular.exception.ModuleInitializeException;
import ru.vit4liy.modular.exception.ModuleShutdownException;

import java.util.Set;

public class DistributionModule extends Module {
    private final ObjectMapper MAPPER = App.getObjectMapper();
    private ExportService exportService;
    private ImportService importService;
    private final BackendDataBus backendDataBus;

    public DistributionModule(ModuleLoader loader, BackendDataBus bus) {
        super(loader);
        this.backendDataBus = bus;
    }

    @Override
    public void initialize() throws ModuleInitializeException {
        this.exportService = new ExportService(MAPPER, backendDataBus);
        this.importService = new ImportService(MAPPER, backendDataBus);
    }

    @Override
    public void shutdown() throws ModuleShutdownException {

    }

    public ExportService getExportService() throws NullPointerException{
        if(exportService == null) Log.wrn("Export service is null!");
        return exportService;
    }

    public ImportService getImportService() throws NullPointerException{
        if(importService == null) Log.wrn("Import service is null!");
        return importService;
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
