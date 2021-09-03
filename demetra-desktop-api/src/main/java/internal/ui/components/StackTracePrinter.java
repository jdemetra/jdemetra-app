/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package internal.ui.components;

import demetra.io.Files2;

import java.io.File;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 *
 * @author Philippe Charles
 * @param <PRINTER>
 */
public abstract class StackTracePrinter<PRINTER> {

    public static final String NAME_CSS = "name";
    public static final String MESSAGE_CSS = "message";
    public static final String KEYWORD_CSS = "keyword";
    public static final String ELEMENT_NAME_CSS = "elementName";
    public static final String ELEMENT_SOURCE_CSS = "elementSource";
    //
    private static final String CAUSE_CAPTION = "Caused by: ";
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    public static StackTracePrinter<StringBuilder> htmlBuilder() {
        return HtmlBuilder.INSTANCE;
    }

    public void printStackTrace(PRINTER p, Throwable th) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
        dejaVu.add(th);

        // Print our stack trace
        printNameAndMessage(p, 0, "", th, "");
        StackTraceElement[] trace = th.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            printTraceElement(p, 0, traceElement);
        }

        // Print suppressed exceptions, if any
        for (Throwable se : th.getSuppressed()) {
            printEnclosedStackTrace(p, se, trace, SUPPRESSED_CAPTION, 1, dejaVu);
        }

        // Print cause, if any
        Throwable ourCause = th.getCause();
        if (ourCause != null) {
            printEnclosedStackTrace(p, ourCause, trace, CAUSE_CAPTION, 0, dejaVu);
        }
    }

    abstract protected void printName(PRINTER p, Class<?> clazz);

    abstract protected void printMessage(PRINTER p, String message);

    abstract protected void printKeyword(PRINTER p, String text);

    abstract protected void println(PRINTER p);

    abstract protected void printElementName(PRINTER p, StackTraceElement traceElement);

    abstract protected void printElementSource(PRINTER p, StackTraceElement traceElement);

    abstract protected void printTab(PRINTER p, int count);

    abstract public String toString(Throwable th);

    private void printNameAndMessage(PRINTER p, int level, String prefix, Throwable th, String suffix) {
        printTab(p, level);
        printKeyword(p, prefix);
        printName(p, th.getClass());
        String message = th.getLocalizedMessage();
        if (message != null) {
            printKeyword(p, ": ");
            printMessage(p, message);
        }
        printKeyword(p, suffix);
        println(p);
    }

    private void printTraceElement(PRINTER p, int prefix, StackTraceElement traceElement) {
        printTab(p, prefix + 1);
        printKeyword(p, "at ");
        printElementName(p, traceElement);
        printElementSource(p, traceElement);
        println(p);
    }

    /**
     * Print our stack trace as an enclosed exception for the specified stack
     * trace.
     */
    private void printEnclosedStackTrace(PRINTER p, Throwable th, StackTraceElement[] enclosingTrace, String caption, int level, Set<Throwable> dejaVu) {
        if (dejaVu.contains(th)) {
            printNameAndMessage(p, 1, "[CIRCULAR REFERENCE:", th, "]");
        } else {
            dejaVu.add(th);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = th.getStackTrace();
            int framesInCommon = getFramesInCommon(trace, enclosingTrace);

            // Print our stack trace
            printNameAndMessage(p, level, caption, th, "");
            for (int i = 0; i < trace.length - framesInCommon; i++) {
                printTraceElement(p, level, trace[i]);
            }
            if (framesInCommon != 0) {
                printTab(p, level + 1);
                printKeyword(p, "... " + framesInCommon + " more");
                println(p);
            }

            // Print suppressed exceptions, if any
            for (Throwable se : th.getSuppressed()) {
                printEnclosedStackTrace(p, se, trace, SUPPRESSED_CAPTION, level + 1, dejaVu);
            }

            // Print cause, if any
            Throwable ourCause = th.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(p, ourCause, trace, CAUSE_CAPTION, level, dejaVu);
            }
        }
    }

    private static int getFramesInCommon(StackTraceElement[] trace, StackTraceElement[] enclosingTrace) {
        int m = trace.length - 1;
        int n = enclosingTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
            m--;
            n--;
        }
        return trace.length - 1 - m;
    }

    private static final class HtmlBuilder extends StackTracePrinter<StringBuilder> {

        public static final HtmlBuilder INSTANCE = new HtmlBuilder();

        @Override
        protected void printName(StringBuilder sb, Class<?> clazz) {
            sb.append("<span class='name'>").append(clazz.getName()).append("</span>");
        }

        @Override
        protected void printMessage(StringBuilder sb, String message) {
            File file = Files2.extractFile(message);
            if (file != null) {
                sb.append("<a class='message' href='").append(file.toURI()).append("'>").append(message).append("</a>");
            } else {
                sb.append("<span class='message'>").append(message).append("</span>");
            }
        }

        @Override
        protected void printKeyword(StringBuilder sb, String text) {
            sb.append("<span class='keyword'>").append(text).append("</span>");
        }

        @Override
        protected void println(StringBuilder sb) {
            sb.append("<br>");
        }

        @Override
        protected void printElementName(StringBuilder sb, StackTraceElement e) {
            sb.append("<span class='elementName'>").append(e.getClassName()).append(".").append(e.getMethodName()).append("</span>");
        }

        @Override
        protected void printElementSource(StringBuilder sb, StackTraceElement e) {
            sb.append("<span class='elementSource'>(");
            if (e.isNativeMethod()) {
                sb.append("Native Method");
            } else {
                if (e.getFileName() != null && e.getLineNumber() >= 0) {
                    sb.append(e.getFileName()).append(":").append(e.getLineNumber());
                } else {
                    if (e.getFileName() != null) {
                        sb.append(e.getFileName());
                    } else {
                        sb.append("Unknown Source");
                    }
                }
            }
            sb.append(")</span>");
        }

        @Override
        protected void printTab(StringBuilder sb, int count) {
            for (int i = 0; i < count; i++) {
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
        }

        @Override
        public String toString(Throwable th) {
            StringBuilder sb = new StringBuilder();
            printStackTrace(sb, th);
            return sb.toString();
        }
    }
}
