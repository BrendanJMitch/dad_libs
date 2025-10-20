package com.brendan.dadlibs;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RepeatRule implements TestRule {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Repeat {
        int value();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        Repeat repeat = description.getAnnotation(Repeat.class);
        if (repeat == null) {
            return base;
        }
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < repeat.value(); i++) {
                    base.evaluate();
                }
            }
        };
    }
}
