/*
 * Copyright 2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A process executor which provides a wrapper class around the ProcessBuilder
 * to provides an improved class for providing external process execution.
 *
 * @author richard linsdale
 */
public class ProcessExecutor {

    private final ProcessBuilder pb;
    private Consumer<String> errorlinehandler = null;
    private String name = "Command";
    private Consumer<String> readlinehandler = null;
    private Supplier<String> writelinehandler = null;

    /**
     * Constructor
     *
     * @param commandline a series of command line components
     */
    public ProcessExecutor(String... commandline) {
        pb = new ProcessBuilder(commandline);
    }

    /**
     * Set the display name of this process executor (used in error messages)
     *
     * @param name the name
     */
    public void setDisplayName(String name) {
        this.name = name;
    }

    /**
     * Set the function which will be used to process error messages (both
     * STDERR and class error messages).
     *
     * @param errorlinehandler the error handling function (Consumer of String)
     */
    public void setErrorLineFunction(Consumer<String> errorlinehandler) {
        this.errorlinehandler = errorlinehandler;
    }

    /**
     * Set the function which will be used to process output (STDOUT).
     *
     * @param readlinehandler the output handling function (Consumer of String)
     */
    public void setOutputLineFunction(Consumer<String> readlinehandler) {
        this.readlinehandler = readlinehandler;
    }

    /**
     * Set the function which will be used to generate input (STDIN).
     *
     * @param writelinehandler the input handling function (Supplier of String)
     */
    public void setInputLineFunction(Supplier<String> writelinehandler) {
        this.writelinehandler = writelinehandler;
    }

    /**
     * Execute the external process.
     */
    protected void execute() {
        try {
            Process process = pb.start();
            if (writelinehandler != null) {
                STDIN stdin = new STDIN(process.getOutputStream(), writelinehandler);
                stdin.start();
            }
            if (readlinehandler != null && errorlinehandler != null) {
                STDOUT stdout = new STDOUT(process.getInputStream(), readlinehandler, errorlinehandler);
                stdout.start();
            }
            if (errorlinehandler != null) {
                STDERR stderr = new STDERR(process.getErrorStream(), errorlinehandler);
                stderr.start();
            }
            process.waitFor();
        } catch (InterruptedException | IOException ex) {
            errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
        }
    }

    private class STDOUT extends Thread {

        private final InputStream inputstream;
        private final Consumer<String> readlinehandler;
        private final Consumer<String> errorlinehandler;

        STDOUT(InputStream inputstream, Consumer<String> readlinehandler, Consumer<String> errorlinehandler) {
            this.inputstream = inputstream;
            this.readlinehandler = readlinehandler;
            this.errorlinehandler = errorlinehandler;
        }

        @Override
        public void run() {
            try {
                try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(inputstream))) {
                    String line;
                    while ((line = fromReader.readLine()) != null) {
                        readlinehandler.accept(line);
                    }
                }
            } catch (IOException ex) {
                errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
            }
        }
    }

    private class STDERR extends Thread {

        private final InputStream inputstream;
        private final Consumer<String> errorlinehandler;

        STDERR(InputStream inputstream, Consumer<String> errorlinehandler) {
            this.inputstream = inputstream;
            this.errorlinehandler = errorlinehandler;
        }

        @Override
        public void run() {
            try {
                try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(inputstream))) {
                    String line;
                    while ((line = fromReader.readLine()) != null) {
                        errorlinehandler.accept(line);
                    }
                }
            } catch (IOException ex) {
                errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
            }
        }
    }

    private class STDIN extends Thread {

        private final OutputStream outputstream;
        private final Supplier<String> writelinehandler;

        STDIN(OutputStream outputstream, Supplier<String> writelinehandler) {
            this.outputstream = outputstream;
            this.writelinehandler = writelinehandler;
        }

        @Override
        public void run() {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputstream))) {
                String line;
                while ((line = writelinehandler.get()) != null) {
                    writer.println(line);
                }
            }
        }
    }
}
