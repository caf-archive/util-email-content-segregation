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
import jep.JepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Uses Jep to call to the Mailgun/Talon library to separate email content.
 */
public class ContentSegregation {

    ContentSegregation(){
    }

    /**
     * A thread local instance of the Jep library. This is required to be thread local
     * as <a href="https://github.com/mrj0/jep/wiki/Performance-Considerations">
     * Jep will only execute calls on the thread it was instantiated on</a>
     * and <a href=" https://github.com/mrj0/jep/issues/28"> closing the Jep instance breaks the Numpy Python Library.</a>
     * Because of these two issues all Worker threads will call to a separate Jep thread.
     */
    private static final ThreadLocal<Jep> threadLocal = new ThreadLocal<Jep>() {
        @Override
        protected Jep initialValue() {
            try {
                return new Jep(false, null, null, new ClassEnquirerImpl());
            } catch (JepException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            Jep jep = this.get();
            if (jep != null) {
                try {
                    jep.close();
                } catch (JepException ex) {
                    throw new RuntimeException(ex);
                }
            }
            super.remove();
        }
    };

    /**
     * Calls a python script to mark the lines of the email.
     * e = empty line
     * m = line that starts with quotation marker '{@literal >}'
     * s = start of next message
     * t = lines with content.
     *
     * @param message the message to separate.
     * @return An ordered list of line indexes that specify the start of each message in the chain.
     * @throws JepException - Unable to split the message provided
     */
    public static List<Integer> splitEmail(String message) throws JepException {
        List<Integer> emailStartLineNumbers = new ArrayList<>();
        Jep jep = threadLocal.get();
        if (jep == null) {
            return emailStartLineNumbers;
        }
        jep.eval("import split_email");
        jep.set("arg", message);
        jep.eval("x = split_email.splitEmail(arg)");
        Object lineMarkers = jep.getValue("x");
        jep.eval("del x");
        jep.eval("del arg");
        if (lineMarkers instanceof String) {
            char[] markers = ((String) lineMarkers).toCharArray();
            int size = markers.length;
            for (int i = 0; i < size; i++) {
                if (markers[i] == 's') {
                    emailStartLineNumbers.add(i);
                }
            }
        } else {
            throw new RuntimeException("Unexpected return type from Python when separating email messages.");
        }

        return emailStartLineNumbers;

    }

    /**
     * Attempts to extract the signature from the specified message.
     *
     * @param message The message to extract the signature from.
     * @return EmailStructure An object containing the signature and the body of the email
     * with the signature removed.
     * @throws JepException - Unable to extract the signature from the message
     */
    public static EmailStructure extractSignature(String message) throws JepException {
        Jep jep = threadLocal.get();
        jep.eval("import split_email");
        jep.set("email", message);
        jep.eval("signature = split_email.extractSignature(email)");
        Object signatureAndBody = jep.getValue("signature");
        jep.eval("del signature");
        jep.eval("del email");
        if (signatureAndBody instanceof Collection) {
            //Python script will only return two values, the email body and signature.
            List<String> sigAndBodyTuple = new ArrayList<>((Collection<? extends String>) signatureAndBody);
            return new EmailStructure(sigAndBodyTuple.get(0), sigAndBodyTuple.get(1));
        }
        throw new RuntimeException("Unexpected return type from Python");
    }

    /**
     * Attempts to extract the signature from the specified message using the slower machine learning based method.
     *
     * @param message The message to extract the signature from.
     * @param sender  The sender of the message.
     * @return EmailStructure An object containing the signature and the body of the email
     * with the signature removed.
     * @throws JepException - Unable to extract the signature from the message
     */
    public static EmailStructure extractSignature_MachineLearning(String message, String sender) throws JepException {
        Jep jep = threadLocal.get();
        jep.eval("import split_email");
        jep.set("email", message);
        jep.set("sender", sender);
        jep.eval("sig = split_email.extractSignature_MachineLearning(email, sender)");
        Object signatureAndBody = jep.getValue("sig");
        jep.eval("del sig");
        jep.eval("del email");
        jep.eval("del sender");
        if (signatureAndBody instanceof Collection) {
            List<String> sigAndBodyTuple = new ArrayList<>((Collection<? extends String>) signatureAndBody);
            return new EmailStructure(sigAndBodyTuple.get(0), sigAndBodyTuple.get(1));
        }
        throw new RuntimeException("Unexpected return type from Python when extracting signature.");
    }

}
