package io.github.simonmeskens.scriptloader;

import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class RemappingCompilationCustomizer extends CompilationCustomizer {
    @SuppressWarnings("UnstableApiUsage")
    private static final Logger logger = Namespace.resolve().getLogger();

    public RemappingCompilationCustomizer() {
        super(CompilePhase.SEMANTIC_ANALYSIS);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        logger.info("Hello from the remapper");
    }
}
