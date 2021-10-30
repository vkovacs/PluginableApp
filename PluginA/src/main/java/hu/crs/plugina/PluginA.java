package hu.crs.plugina;

import hu.crs.pluginableapp.Plugin;

public class PluginA implements Plugin {
    @Override
    public String result() {
        return "A plugin result";
    }
}
