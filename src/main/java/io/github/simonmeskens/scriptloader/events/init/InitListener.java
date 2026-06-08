package io.github.simonmeskens.scriptloader.events.init;

import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

import java.lang.invoke.MethodHandles;

public class InitListener {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    private static void serverInit(InitEvent event) {
        GroovyScriptLoader.runDirectory("init");
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    private static void serverInitFinished(InitFinishedEvent event) {
        GroovyScriptLoader.runDirectory("finished");
    }

    @EventListener(priority = ListenerPriority.LOWEST)
    public void registerRecipes(RecipeRegisterEvent event) {
        GroovyScriptLoader.runDirectory("recipes");
    }
}
