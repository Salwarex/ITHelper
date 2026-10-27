package ru.vit4liy.base.bus;

import ru.vit4liy.rsc.entity.Entity;
import ru.vit4liy.rsc.service.GenericDatabaseBusService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectEntity {
    Class<? extends GenericDatabaseBusService<? extends Entity>> serviceInterface();
}
