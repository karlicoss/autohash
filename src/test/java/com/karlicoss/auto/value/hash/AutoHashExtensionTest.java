package com.karlicoss.auto.value.hash;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Collections.singletonList;

public class AutoHashExtensionTest {

    @Test
    public void doesNotProcessUnannotatedClass() {
        JavaFileObject input = JavaFileObjects.forResource("input/Unannotated.java");
        JavaFileObject output = JavaFileObjects.forResource("output/Unannotated.java");

        assertAbout(javaSources())
                .that(singletonList(input))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}