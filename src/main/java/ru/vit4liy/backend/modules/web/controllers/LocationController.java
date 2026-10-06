package ru.vit4liy.backend.modules.web.controllers;

import io.javalin.Javalin;
import ru.vit4liy.web.Controller;

public class LocationController extends Controller {
    public LocationController(Javalin app) {
        super(app);
    }

    @Override
    protected void initEndpoints() {

    }

    @Override
    protected String path() {
        return "model/location";
    }
}
