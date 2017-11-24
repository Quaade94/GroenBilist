package com.quaade94.groenbilist;
/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.regex.Pattern;

public class Connection {
    private static Connection instance2;


    public static Connection getInstance() {
        if (instance2 == null) {
            instance2 = new Connection();
            System.out.println("Connection.instance var NULL - frisk start! Opretter en instance");
        }
        return instance2;
    }

        protected ArrayList<Integer> buffer = null;
        protected String cmd = null;
        protected boolean useImperialUnits = false;
        protected String rawData = null;
        protected Long responseDelayInMs = null;
        private long start;
        private long end;

        Logic I = Logic.getInstance();

        public void data(){

            this.cmd = "ATZ";
            this.buffer = new ArrayList<>();
            I.setACLevel(Integer.parseInt(rawData));
/*
            this.cmd = "";
            this.buffer = new ArrayList<>();
            I.setSOC(Integer.parseInt(rawData));

            this.cmd = "";
            this.buffer = new ArrayList<>();
            //I.setHeadlights(rawData);

            this.cmd = "";
            this.buffer = new ArrayList<>();
            I.setVelocity(Integer.parseInt(rawData));
*/
        }

        public void run(InputStream in, OutputStream out) throws IOException,
                InterruptedException {
            synchronized (Connection.class) {//Only one command can write and read a data in one time.
                start = System.currentTimeMillis();
                sendCommand(out);
                readResult(in);
                end = System.currentTimeMillis();
            }
        }

        protected void sendCommand(OutputStream out) throws IOException,
                InterruptedException {
            // write to OutputStream (i.e.: a BluetoothSocket) with an added
            // Carriage return
            out.write((cmd + "\r").getBytes());
            out.flush();
            if (responseDelayInMs != null && responseDelayInMs > 0) {
                Thread.sleep(responseDelayInMs);
            }
        }

        protected void resendCommand(OutputStream out) throws IOException,
                InterruptedException {
            out.write("\r".getBytes());
            out.flush();
            if (responseDelayInMs != null && responseDelayInMs > 0) {
                Thread.sleep(responseDelayInMs);
            }
        }

        protected void readResult(InputStream in) throws IOException {
            readRawData(in);
            fillBuffer();
        }

        private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
        private static Pattern BUSINIT_PATTERN = Pattern.compile("(BUS INIT)|(BUSINIT)|(\\.)");
        private static Pattern SEARCHING_PATTERN = Pattern.compile("SEARCHING");
        private static Pattern DIGITS_LETTERS_PATTERN = Pattern.compile("([0-9A-F])+");

        protected String replaceAll(Pattern pattern, String input, String replacement) {
            return pattern.matcher(input).replaceAll(replacement);
        }

        protected String removeAll(Pattern pattern, String input) {
            return pattern.matcher(input).replaceAll("");
        }

        protected void fillBuffer() {
            rawData = removeAll(WHITESPACE_PATTERN, rawData); //removes all [ \t\n\x0B\f\r]
            rawData = removeAll(BUSINIT_PATTERN, rawData);

            if (!DIGITS_LETTERS_PATTERN.matcher(rawData).matches()) {
                //throw new NonNumericResponseException(rawData);
            }

            // read string each two chars
            buffer.clear();
            int begin = 0;
            int end = 2;
            while (end <= rawData.length()) {
                buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
                begin = end;
                end += 2;
            }
        }

        protected void readRawData(InputStream in) throws IOException {
            byte b = 0;
            StringBuilder res = new StringBuilder();

            // read until '>' arrives OR end of stream reached
            char c;
            // -1 if the end of the stream is reached
            while (((b = (byte) in.read()) > -1)) {
                c = (char) b;
                if (c == '>') // read until '>' arrives
                {
                    break;
                }
                res.append(c);
            }

            rawData = removeAll(SEARCHING_PATTERN, res.toString());

            //kills multiline.. rawData = rawData.substring(rawData.lastIndexOf(13) + 1);
            rawData = removeAll(WHITESPACE_PATTERN, rawData);//removes all [ \t\n\x0B\f\r]
        }

        void checkForErrors() {
            /*
            for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
                //ResponseException messageError;

                try {
                    messageError = errorClass.newInstance();
                    messageError.setCommand(this.cmd);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (messageError.isError(rawData)) {
                    throw messageError;
                }
            }
            */
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Connection that = (Connection) o;

            return cmd != null ? cmd.equals(that.cmd) : that.cmd == null;
        }

        @Override
        public int hashCode() {
            return cmd != null ? cmd.hashCode() : 0;
        }
}