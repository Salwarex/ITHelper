package ru.vit4liy.base.distribution;

import ru.vit4liy.rsc.entity.Entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignId {
    Class<? extends Entity> foreignClass();
}
