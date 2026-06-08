package io.github.simonmeskens.scriptloader;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.simonmeskens.scriptloader.remap.MappingTreeRemapper;
import io.github.simonmeskens.scriptloader.remap.Remapper;
import io.github.simonmeskens.scriptloader.remap.RemappingClassLoader;
import io.github.simonmeskens.scriptloader.remap.RemappingCompilationCustomizer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.mappingio.tree.VisitableMappingTree;
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

    public static final Path scriptsDir;

    public static final Binding binding;
    public static final GroovyShell shell;

    static {
        try {
            scriptsDir = FabricLoader.getInstance().getGameDir().resolve("scripts");

            VisitableMappingTree mappings = new MemoryMappingTree();

            MappingReader.read(scriptsDir.resolve("mappings.tiny"), null, mappings);

            Remapper remapper = new MappingTreeRemapper(mappings, "named", "intermediary");

            CompilerConfiguration config = new CompilerConfiguration();
            config.addCompilationCustomizers(new RemappingCompilationCustomizer(remapper));

            ClassLoader loader = new RemappingClassLoader(GroovyScriptLoader.class.getClassLoader(), remapper);

            binding = new Binding();
            shell = new GroovyShell(loader, binding, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runDirectory(String dirName) {
        Path dir = scriptsDir.resolve(dirName);

        if (!Files.exists(dir) || !dir.toFile().isDirectory()) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                logger.error("could not create /scripts/{} directory.", dirName, e);
                return;
            }
        }

        List<Path> scripts;
        try (Stream<Path> files = Files.walk(dir)) {
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
                logger.info("Evaluating {}", script.getFileName());
                shell.evaluate(script.toFile());
            } catch (IOException e) {
                logger.error("error in {}: {}", script.getFileName(), e.getMessage());
                return;
            }
        }
    }
}
