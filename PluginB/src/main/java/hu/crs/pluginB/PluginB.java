package hu.crs.pluginB;


import hu.crs.pluginableapp.Plugin;

public class PluginB implements Plugin {

    @Override
    public String result() {
        return "result of PluginB";
    }
}
