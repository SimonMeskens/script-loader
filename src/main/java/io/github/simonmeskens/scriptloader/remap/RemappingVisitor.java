package io.github.simonmeskens.scriptloader.remap;

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
            String fieldName = prop.getPropertyAsString();
            String remapped = remapper.remapFieldName(prop.getObjectExpression().getType().getTypeClass(), fieldName);

            if (!remapped.equals(fieldName)) {
                PropertyExpression remappedExpr = new PropertyExpression(transform(prop.getObjectExpression()), remapped);
                remappedExpr.setSourcePosition(expr);
                return remappedExpr;
            }
        }

        else if (expr instanceof MethodCallExpression call) {
            String methodName = call.getMethodAsString();

            if (methodName != null) {
                String remapped = remapper.remapMethodName(call.getObjectExpression().getType().getTypeClass(), methodName);

                if (!remapped.equals(methodName)) {
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
            String remapped = remapper.remapMethodName(owner, staticCall.getMethod());

            if (!remapped.equals(staticCall.getMethod())) {
                StaticMethodCallExpression remappedCall = new StaticMethodCallExpression(
                        staticCall.getOwnerType(),
                        remapped,
                        transform(staticCall.getArguments()));
                remappedCall.setSourcePosition(expr);
                return remappedCall;
            }
        }

        return super.transform(expr);
    }
}
