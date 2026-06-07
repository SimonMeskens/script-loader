package io.github.simonmeskens.scriptloader;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.fabricmc.loader.api.FabricLoader;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class GroovyScriptLoader {
    @SuppressWarnings("UnstableApiUsage")
    public static final Logger logger = Namespace.resolve().getLogger("GroovyLoader");

    public static final Binding binding;
    public static final GroovyShell shell;

    static {
        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(
                new StaticCompilationCustomizer(),
                new RemappingCompilationCustomizer()
        );

        ClassLoader loader = GroovyScriptLoader.class.getClassLoader();

        binding = new Binding();
        shell = new GroovyShell(loader, binding, config);
    }

    public static void runDirectory(String dirName) {
        Path scriptsDir = FabricLoader.getInstance()
                .getGameDir()
                .resolve("scripts")
                .resolve(dirName);

        if (!Files.exists(scriptsDir) || !scriptsDir.toFile().isDirectory()) {
            try {
                Files.createDirectories(scriptsDir);
            } catch (IOException e) {
                logger.error("could not create /scripts/{} directory.", dirName, e);
                return;
            }
        }

        List<Path> scripts;
        try (Stream<Path> files = Files.walk(scriptsDir)) {
            scripts = files.filter(p -> p.toString().endsWith(".groovy")).toList();
        } catch (IOException e) {
            logger.error("could not scan /scripts/{} directory.", dirName, e);
            return;
        }

        if (scripts.isEmpty()) {
            logger.info("no scripts found in /scripts/{} directory.", dirName);
            return;
        }

        for (Path script : scripts) {
            try {
                shell.evaluate(script.toFile());
            } catch (IOException e) {
                logger.error("error in {}: {}", script.getFileName(), e.getMessage());
                return;
            }
        }
    }
}
