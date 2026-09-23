package de.dasbabypixel.gamestages.neoforge.v1_21_1.integration.kubejs.probejs;

import moe.wolfgirl.probejs.typescript.document.ClassDecl;
import moe.wolfgirl.probejs.typescript.document.base.Type;
import moe.wolfgirl.probejs.typescript.document.members.MethodDecl;
import moe.wolfgirl.probejs.typescript.document.members.ParamDecl;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class ClassTransformerRegistry {
    private final Map<Class<?>, ClassTransformer.Entry> transformerMap = new HashMap<>();
    private boolean frozen = false;

    private void freeze() {
        frozen = true;
    }

    private ClassTransformerRegistry modify() {
        if (frozen) throw new IllegalStateException("frozen");
        return this;
    }

    public void register(Class<?> cls, ClassTransformer classTransformer) {
        register(cls).addClassTransformer(classTransformer);
    }

    public void register(Class<?> cls, MethodTransformer methodTransformer, String... names) {
        for (var name : names) {
            register(cls).addMethodTransformer(name, methodTransformer);
        }
    }

    public void register(Class<?> cls, MethodTransformer methodTransformer) {
        register(cls).addMethodTransformer(methodTransformer);
    }

    public ClassTransformer.Entry register(Class<?> cls) {
        return modify().transformerMap.computeIfAbsent(cls, ignored -> new ClassTransformer.Entry());
    }

    public void transform(Class<?> cls, ClassDecl classDecl) {
        freeze();

        var entry = transformerMap.get(cls);
        if (entry != null) {
            entry.transform(classDecl);
        }
    }

    public interface ClassTransformer {
        void transform(ClassDecl classDecl);

        class Entry {
            private final List<ClassTransformer> classTransformers = new ArrayList<>(0);
            private final Map<String, MethodTransformer.Entry> methodTransformerMap = new HashMap<>();
            private final MethodTransformer.Entry methodTransformers = new MethodTransformer.Entry();

            @SuppressWarnings("DataFlowIssue")
            private void transform(ClassDecl classDecl) {
                if (!classTransformers.isEmpty()) {
                    for (var classTransformer : classTransformers) {
                        classTransformer.transform(classDecl);
                    }
                }
                for (var member : classDecl.members) {
                    switch (member) {
                        case MethodDecl methodDecl -> {
                            methodTransformers.transform(classDecl, methodDecl);

                            var methodEntry = methodTransformerMap.get(methodDecl.name);
                            if (methodEntry != null) {
                                methodEntry.transform(classDecl, methodDecl);
                            }
                        }
                        default -> {
                        }
                    }
                }
            }

            private MethodTransformer.Entry methodEntry(String name) {
                return methodTransformerMap.computeIfAbsent(name, ignored -> new MethodTransformer.Entry());
            }

            public void addClassTransformer(ClassTransformer classTransformer) {
                this.classTransformers.add(classTransformer);
            }

            public void addMethodTransformer(String name, MethodTransformer methodTransformer) {
                methodEntry(name).addMethodTransformer(methodTransformer);
            }

            public void addMethodTransformer(MethodTransformer methodTransformer) {
                methodTransformers.addMethodTransformer(methodTransformer);
            }
        }
    }

    public interface MethodTransformer {
        static MethodTransformer replaceTypes(TypeReplacer typeReplacer) {
            var paramTransformer = ParamTransformer.replaceTypes(typeReplacer);
            return (classDecl, methodDecl) -> {
                if (methodDecl.returnType instanceof Type type) {
                    methodDecl.returnType = typeReplacer.replace(type);
                }
                for (var paramDecl : Objects.requireNonNull(methodDecl.params)) {
                    paramTransformer.transform(classDecl, methodDecl, Objects.requireNonNull(paramDecl));
                }
            };
        }

        void transform(ClassDecl classDecl, MethodDecl methodDecl);

        class Entry implements MethodTransformer {
            private final List<MethodTransformer> methodTransformers = new ArrayList<>(0);

            public void addMethodTransformer(MethodTransformer methodTransformer) {
                this.methodTransformers.add(methodTransformer);
            }

            @Override
            public void transform(ClassDecl classDecl, MethodDecl methodDecl) {
                if (!methodTransformers.isEmpty()) {
                    for (var methodTransformer : methodTransformers) {
                        methodTransformer.transform(classDecl, methodDecl);
                    }
                }
            }
        }
    }

    public interface ParamTransformer {
        static ParamTransformer replaceTypes(TypeReplacer typeReplacer) {
            return (classDecl, methodDecl, paramDecl) -> {
                paramDecl.typeInfo = typeReplacer.replace(Objects.requireNonNull(paramDecl.typeInfo));
            };
        }

        void transform(ClassDecl classDecl, MethodDecl methodDecl, ParamDecl paramDecl);
    }

    public interface TypeReplacer {
        Type replace(Type type);
    }
}
