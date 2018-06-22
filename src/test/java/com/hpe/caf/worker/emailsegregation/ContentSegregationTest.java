/*
 * Copyright 2017-2018 Micro Focus or one of its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.worker.emailsegregation;

import jep.Jep;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ContentSegregation.class, Jep.class})
@SuppressStaticInitializationFor("jep.Jep")
public class ContentSegregationTest {

    private ContentSegregation contentSegregation;
    private Jep jep;
    private ThreadLocal<Jep> threadLocal;

    private static String expectedLineMarkers = "teeeeeeeteeeteeeteeeteeeteteteeeteteteeeteteteteeeteteeeteteteeeteteteteeeeeeeteeeeeeeteeeeeeeseteteteteteteeeeeeeteeeeeeeteteeeeeeeteeeteteeeeeeeteeeteteeeeeeeteteeeteeeeeeeteeet";
    private static List<String> expectedSignatureAndBody = new ArrayList<>();
    private static String expectedBody = "Wow. Awesome!";
    private static String expectedSignature = "--\nBob Smith";

    @Before
    public void setup() throws Exception {
        setUpJep();
        expectedSignatureAndBody.add(expectedBody);
        expectedSignatureAndBody.add(expectedSignature);
    }

    @Test
    public void testContentSegregation() throws Exception {


        PowerMockito.when(jep.getValue(Mockito.same("x"))).thenReturn(expectedLineMarkers);

        List<Integer> lineIndexes = ContentSegregation.splitEmail(emailChain);

        Assert.assertEquals("There should only be 1 new message index returned", 1, lineIndexes.size());
        Assert.assertEquals("The new message line index should match", Integer.valueOf(94), lineIndexes.get(0));
    }

    @Test
    public void testSignatureExtract() throws Exception {
        setUpJep();
        PowerMockito.when(jep.getValue(Mockito.same("signature"))).thenReturn(expectedSignatureAndBody);

        EmailStructure emailStructure = ContentSegregation.extractSignature(emailWithSig);

        Assert.assertNotNull(emailStructure);
        Assert.assertEquals("Extracted body should match", expectedBody, emailStructure.getBody());
        Assert.assertEquals("Extracted signature should match", expectedSignature, emailStructure.getSignature());
    }

    private void setUpJep() throws Exception {
        jep = PowerMockito.mock(Jep.class);
        PowerMockito.whenNew(Jep.class).withAnyArguments().thenReturn(jep);
        threadLocal = PowerMockito.mock(ThreadLocal.class);
        PowerMockito.when(threadLocal.get()).thenReturn(jep);
        contentSegregation = new ContentSegregation();
        Whitebox.setInternalState(contentSegregation, "threadLocal", threadLocal);
        PowerMockito.when(jep.eval(Mockito.anyString())).thenReturn(true);
    }

    public static String emailWithSig = "Wow. Awesome!\n" +
            "--\n" +
            "Bob Smith";

    private static String emailChain = "James,\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "No, this doesn’t work for us either.\n" +
            "\n" +
            "\n" +
            "\n" +
            "I’ll try to explain what we need, with some additional description:\n" +
            "\n" +
            "\n" +
            "\n" +
            "1)      Example project\n" +
            "\n" +
            "\n" +
            "\n" +
            "What is an example project:\n" +
            "\n" +
            "\n" +
            "\n" +
            "A project with a reference to plugin in the pom file that is not testing\n" +
            "\n" +
            "any product. Something that we can run here without any additional\n" +
            "\n" +
            "external dependencies.\n" +
            "\n" +
            "\n" +
            "\n" +
            "Project tests are no good because we do not have project here, same for any\n" +
            "\n" +
            "other tests you have there. We need something that we can *easily* build\n" +
            "\n" +
            "and run here.\n" +
            "\n" +
            "\n" +
            "\n" +
            "Just a project with a few example tests – something that, let’s say,\n" +
            "\n" +
            "adds numbers and verifies the sum is correct. It should use plugin to do\n" +
            "\n" +
            "that. No external dependencies except plugin should be present in this\n" +
            "\n" +
            "project.\n" +
            "\n" +
            "\n" +
            "\n" +
            "In the example project there should be no copy/pasted code from plugin\n" +
            "\n" +
            "base code. Just reference(s) to plugin libraries.\n" +
            "\n" +
            "\n" +
            "\n" +
            "2)      Written instructions how to configure plugin stack and run the\n" +
            "\n" +
            "tests in example project. This should be just a list of steps we need to\n" +
            "\n" +
            "make to get the sample running.\n" +
            "\n" +
            "\n" +
            "\n" +
            "3)      Packaged plugin – we will need this in maven repository before we\n" +
            "\n" +
            "integrate. It’s ok to supply example project with a JAR but before we\n" +
            "\n" +
            "start integrating it here, this needs to be in maven repository so we\n" +
            "\n" +
            "can consume it.\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "Please let me know if you need some more details.\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "-Bob\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "From: James, Steven Paul \n" +
            "\n" +
            "Sent: 14 March 2016 10:56\n" +
            "\n" +
            "To: Smith, Bob <bob.smith@email.com>\n" +
            "\n" +
            "Cc: Kennedy, Jewel <jewel.kennedy@email.com>; Kelcey, Kasey\n" +
            "\n" +
            "<Kelcey.Kasey@email.com>; Shelby, Harper\n" +
            "\n" +
            "<Harper.Shelby@email.com>\n" +
            "\n" +
            "Subject: RE: plugin example code\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "Hi Bob,\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "I have already added you to share point page some time ago where we\n" +
            "\n" +
            "have documentation related to plugin\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "plugin code base:\n" +
            "\n" +
            "\n" +
            "\n" +
            "http://12.285.124.32/Auto/Gen\n" +
            "\n" +
            "<http://12.185.124.32/Auto/Gen> \n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "for API example:\n" +
            "\n" +
            "\n" +
            "\n" +
            "http://12.185.124.32/Auto/Gen/Castle/qa_vegas\n" +
            "\n" +
            "<http://12.185.124.32/Auto/Gen/Castle/qa_vegas> \n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "In the above qa_vegas project example we can make use of common classes\n" +
            "\n" +
            "what we have rather vegas specific.\n" +
            "\n" +
            "\n" +
            "\n" +
            "This is what we have, I think this is good enough to go ahead.\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\n" +
            "\n" +
            "Thanks,\n" +
            "\n" +
            "\n" +
            "\n" +
            "Steven James\n";

}
