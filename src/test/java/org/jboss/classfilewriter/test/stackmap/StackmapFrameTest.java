/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.classfilewriter.test.stackmap;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.jboss.classfilewriter.ClassFile;
import org.jboss.classfilewriter.ClassMethod;
import org.jboss.classfilewriter.code.BranchEnd;
import org.jboss.classfilewriter.code.CodeAttribute;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 *
 */
public class StackmapFrameTest {

    @Test
    public void testSubclass() throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        ClassFile classFile = new ClassFile("DigitalCalculatorSubclass", DigitalCalculator.class.getName());
        generateConstructor(classFile);
        generateMethod(classFile, Calculator.class.getMethod("ping"));
        Class<?> proxyClass = classFile.define(getClass().getClassLoader());
        DigitalCalculator calculator = (DigitalCalculator) proxyClass.newInstance();
        assertEquals("OK", calculator.ping());
    }

    private static void generateMethod(ClassFile classFile, Method superclassMethod) {
        CodeAttribute b = classFile.addMethod(superclassMethod).getCodeAttribute();
        b.iconst(0);
        BranchEnd jumpMarker = b.ifne();
        b.aload(0);
        b.invokespecial(superclassMethod);
        b.returnInstruction();
        b.branchEnd(jumpMarker);
        b.aconstNull();
        b.returnInstruction();
    }

    private static void generateConstructor(ClassFile classFile) throws NoSuchMethodException, SecurityException {
        ClassMethod constructor = classFile.addConstructor(DigitalCalculator.class.getConstructor());
        CodeAttribute b = constructor.getCodeAttribute();
        b.aload(0);
        b.invokespecial(DigitalCalculator.class.getConstructor());
        b.returnInstruction();
    }

}
