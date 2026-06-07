package io.github.simonmeskens.scriptloader.events.init;

import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

import java.lang.invoke.MethodHandles;

public class InitListener {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @EventListener
    private static void serverInit(InitEvent event) {
        GroovyScriptLoader.runDirectory("init");
    }

    @EventListener
    private static void serverInitFinished(InitFinishedEvent event) {
        GroovyScriptLoader.runDirectory("finished");
    }
}
