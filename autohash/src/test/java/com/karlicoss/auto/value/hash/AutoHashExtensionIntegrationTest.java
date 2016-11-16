package com.karlicoss.auto.value.hash;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.reflect.ConstructorUtils.invokeConstructor;

public class AutoHashExtensionIntegrationTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private Object hashCodeCallsCaptor;

    private Object integrationTestHelper;

    @Before
    public void beforeEachTest() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        JavaFileObject input = JavaFileObjects.forResource("input/IntegrationTestHelper.java");

        // compile source code from resources and put the output classfiles in a temporary directory
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        CompilationTask task = compiler.getTask(
                null,
                null,
                null,
                asList("-d", tmpFolder.getRoot().toString()),
                null,
                singletonList(input)
        );
        task.setProcessors(singletonList(new AutoValueProcessor()));
        boolean success = task.call();
        if (!success) {
            throw new RuntimeException("Couldn't compile the input data for test!");
        }

        // load the generated classfiles
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { tmpFolder.getRoot().toURI().toURL() });
        Class<?> integrationClass = Class.forName("test.AutoValue_IntegrationTestHelper", true, classLoader);
        Class<?> captorClass = Class.forName("test.IntegrationTestHelper$HashCodeCaptor", true, classLoader);

        hashCodeCallsCaptor = invokeConstructor(captorClass);

        // AutoValues constructor is package private, and it's probably the easiest way to create the class
        Constructor<?> declaredConstructor = integrationClass.getDeclaredConstructor(captorClass);
        declaredConstructor.setAccessible(true);
        integrationTestHelper = declaredConstructor.newInstance(hashCodeCallsCaptor);
    }

    private int getNumberOfHashCodeCalls() {
        try {
            return (int) FieldUtils.readField(hashCodeCallsCaptor, "hashCodeCalls");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void hashCode_isValid() {
        assertThat(integrationTestHelper.hashCode()).isEqualTo(123_456);

        // hashcode should remain the same
        assertThat(integrationTestHelper.hashCode()).isEqualTo(123_456);
        assertThat(integrationTestHelper.hashCode()).isEqualTo(123_456);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void hashCode_isCached() {
        // sanity check that initially there were no hash code calls
        assertThat(getNumberOfHashCodeCalls()).isEqualTo(0);

        integrationTestHelper.hashCode();

        assertThat(getNumberOfHashCodeCalls()).isEqualTo(1);

        // few more calls
        integrationTestHelper.hashCode();
        integrationTestHelper.hashCode();
        integrationTestHelper.hashCode();

        // hashcode should be cached at this point and there should be no more calls to the underlying fields
        assertThat(getNumberOfHashCodeCalls()).isEqualTo(1);
    }
}
