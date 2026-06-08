package io.github.simonmeskens.scriptloader.remap;

import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MappingTreeView;
import net.fabricmc.mappingio.tree.VisitableMappingTree;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class RemappingCompilationCustomizer extends CompilationCustomizer {
    private static final Logger logger = GroovyScriptLoader.logger;

    @Getter
    private final Remapper remapper;

    public RemappingCompilationCustomizer(Remapper remapper) {
        super(CompilePhase.CANONICALIZATION);
        this.remapper = remapper;
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        new RemappingVisitor(source, remapper).visitClass(classNode);
    }
}
