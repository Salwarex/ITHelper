package ru.vit4liy.backend.modules.web.controllers;

import io.javalin.Javalin;
import ru.vit4liy.web.Controller;

public class TypeController extends Controller {
    public TypeController(Javalin app) {
        super(app);
    }

    @Override
    protected void initEndpoints() {

    }

    @Override
    protected String path() {
        return "model/type";
    }
}
