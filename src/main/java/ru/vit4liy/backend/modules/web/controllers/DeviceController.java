package ru.vit4liy.backend.modules.web.controllers;

import io.javalin.Javalin;
import ru.vit4liy.web.Controller;

public class DeviceController extends Controller {
    public DeviceController(Javalin app) {
        super(app);
    }

    @Override
    protected void initEndpoints() {

    }

    @Override
    protected String path() {
        return "model/device";
    }
}
