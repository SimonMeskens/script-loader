package io.github.simonmeskens.scriptloader.remap;

import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
import lombok.Getter;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.control.SourceUnit;

public class RemappingVisitor extends ClassCodeExpressionTransformer {
    @Getter(onMethod_ = {@Override})
    private final SourceUnit sourceUnit;
    @Getter
    private final Remapper remapper;

    public RemappingVisitor(SourceUnit sourceUnit, Remapper remapper) {
        this.sourceUnit = sourceUnit;
        this.remapper = remapper;
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr == null) return null;

        else if (expr instanceof PropertyExpression prop) {
            Class<?> object = prop.getObjectExpression().getType().getTypeClass();
            String fieldName = prop.getPropertyAsString();

            if (fieldName != null && !MappingUtil.hasField(object, fieldName)) {
                GroovyScriptLoader.logger.info("Compile-time remapping field {} ({})", fieldName, object.getName());
                String remapped = remapper.remapFieldName(object, fieldName);

                if (remapped != null && !remapped.equals(fieldName)) {
                    PropertyExpression remappedExpr = new PropertyExpression(transform(prop.getObjectExpression()), remapped);
                    remappedExpr.setSourcePosition(expr);
                    return remappedExpr;
                }
            }
        }

        else if (expr instanceof MethodCallExpression call) {
            Class<?> object = call.getObjectExpression().getType().getTypeClass();
            String methodName = call.getMethodAsString();

            if (methodName != null && !MappingUtil.hasMethod(object, methodName)) {
                GroovyScriptLoader.logger.info("Compile-time remapping method {} ({})", methodName, object.getName());
                String remapped = remapper.remapMethodName(object, methodName);

                if (remapped != null && !remapped.equals(methodName)) {
                    MethodCallExpression remappedCall = new MethodCallExpression(
                            transform(call.getObjectExpression()),
                            remapped,
                            transform(call.getArguments()));
                    remappedCall.setSourcePosition(expr);
                    remappedCall.setImplicitThis(call.isImplicitThis());
                    remappedCall.setSafe(call.isSafe());
                    remappedCall.setSpreadSafe(call.isSpreadSafe());
                    return remappedCall;
                }
            }
        }

        else if (expr instanceof StaticMethodCallExpression staticCall) {
            Class<?> owner = staticCall.getOwnerType().getTypeClass();
            String methodName = staticCall.getMethod();

            if (methodName != null && !MappingUtil.hasMethod(owner, methodName)) {
                GroovyScriptLoader.logger.info("Compile-time remapping static method {} ({})", methodName, owner.getName());
                String remapped = remapper.remapMethodName(owner, methodName);

                if (remapped != null && !remapped.equals(staticCall.getMethod())) {
                    StaticMethodCallExpression remappedCall = new StaticMethodCallExpression(
                            staticCall.getOwnerType(),
                            remapped,
                            transform(staticCall.getArguments()));
                    remappedCall.setSourcePosition(expr);
                    return remappedCall;
                }
            }
        }

        return super.transform(expr);
    }
}
