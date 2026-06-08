package io.github.simonmeskens.scriptloader;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class StaticCompilationCustomizer  extends CompilationCustomizer {
    public StaticCompilationCustomizer() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        classNode.addAnnotation(new AnnotationNode(new ClassNode(groovy.transform.CompileStatic.class)));
    }
}
