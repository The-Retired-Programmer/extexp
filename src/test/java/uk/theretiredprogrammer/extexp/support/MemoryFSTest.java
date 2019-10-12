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
package uk.theretiredprogrammer.extexp.support;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.filesystems.FileObject;

/**
 * Test for in-memory filesystem for temporary file storage.
 *
 * @author richard linsdale
 */
public class MemoryFSTest {

    public MemoryFSTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of all methods in class MemoryFS.
     */
    @Test
    public void testALL() {
        MemoryFS tempfs = new MemoryFS();
        FileObject tempfsroot = tempfs.getRoot();
        //
        //  run a set of tests to check results on empty file store
        //
        assertEquals(0, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNull(tempfs.getInputStreamReader("not_a_file"));
        //
        //  write first file then run set of tests to check results
        //
        System.out.println("write");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("written")) {
                writer.write(WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        FileObject fo;
        assertEquals(1, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNull(tempfs.getInputStreamReader("not_a_file"));
        assertNotNull(fo = tempfs.getFileObject("written"));
        assertEquals("written", fo.getNameExt());
        Reader reader;
        assertNotNull(reader = tempfs.getInputStreamReader("written"));
        try {
            reader.close();
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        try {
            assertEquals(WRITTENCONTENT, fo.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  append to new file then run set of tests to check results
        //
        System.out.println("append new");
        String APPENDNEWCONTENT = "123..890";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("append.new", true)) {
                writer.write(APPENDNEWCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        assertEquals(2, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNotNull(tempfs.getFileObject("append.new"));
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo2;
        assertNotNull(fo2 = tempfs.getFileObject("append.new"));
        assertEquals("append.new", fo2.getNameExt());
        try {
            assertEquals(APPENDNEWCONTENT, fo2.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  append to written then run set of tests to check results
        //
        System.out.println("append existing");
        String APPENDCONTENT = "234..678";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("written", true)) {
                writer.write(APPENDCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        assertEquals(2, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNotNull(tempfs.getFileObject("written"));
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo3;
        assertNotNull(fo3 = tempfs.getFileObject("written"));
        assertEquals("written", fo3.getNameExt());
        try {
            assertEquals(WRITTENCONTENT + APPENDCONTENT, fo3.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  overwrite existing file with less content then run set of tests to check results
        //
        System.out.println("Overwrite existing with less");
        String NEWCONTENT = "P..W";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("append.new")) {
                writer.write(NEWCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        assertEquals(2, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNotNull(tempfs.getFileObject("append.new"));
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo4;
        assertNotNull(fo4 = tempfs.getFileObject("append.new"));
        assertEquals("append.new", fo4.getNameExt());
        try {
            assertEquals(NEWCONTENT, fo4.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  overwrite existing file with more content then run set of tests to check results
        //
        System.out.println("Overwrite existing - with more");
        String NEWCONTENT2 = "abcdefghijklmnopqrstuvwxyz";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("append.new")) {
                writer.write(NEWCONTENT2);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        assertEquals(2, tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNotNull(tempfs.getFileObject("append.new"));
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo5;
        assertNotNull(fo5 = tempfs.getFileObject("append.new"));
        assertEquals("append.new", fo5.getNameExt());
        try {
            assertEquals(NEWCONTENT2, fo5.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of streams and reader methods in class MemoryFS.
     */
    @Test
    public void testStreamsAndReaders() {
        MemoryFS tempfs = new MemoryFS();
        System.out.println("Streams and Readers - simple reader");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            try (Writer writer = tempfs.getOutputStreamWriter("test1")) {
                writer.write(WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        try {
            try (Reader rdr = tempfs.getInputStreamReader("test1")) {
                int c;
                String res = "";
                while ((c = rdr.read()) != -1) {
                    res = res + ((char) c);
                }
                assertEquals(res, WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception while reading- " + ex.getLocalizedMessage());
        }
        String WRITTENCONTENT2 = "PQR...WXY";
        try {
            try (Writer wtr = tempfs.getOutputStreamWriter("test2")) {
                wtr.write(WRITTENCONTENT2);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        try {
            try (Reader rdr2 = tempfs.getInputStreamReader("test2")) {
                int c;
                String res = "";
                while ((c = rdr2.read()) != -1) {
                    res = res + ((char) c);
                }
                assertEquals(res, WRITTENCONTENT2);
            }
        } catch (IOException ex) {
            fail("Unexpected exception while reading- " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of locks and close in class MemoryFS.
     *
     * @throws java.io.IOException if problems
     */
    @Test
    public void testLocksAndClose() throws IOException {
        MemoryFS tempfs = new MemoryFS();
        System.out.println("Locks and Close");
        FileObject fo = tempfs.getFileObject("test2");
        assertNull(fo);
        try (BufferedWriter writer = new BufferedWriter(tempfs.getOutputStreamWriter("test2"))) {
            assertNotNull(writer);
            fo = tempfs.getFileObject("test2");
            assertNotNull(fo);
            assertTrue(fo.isLocked());
        }
        assertFalse(fo.isLocked());
    }

    /**
     * Test of append locks and close in class MemoryFS.
     *
     * @throws java.io.IOException if problems
     */
    @Test
    public void testAppendLocksAndClose() throws IOException {
        MemoryFS tempfs = new MemoryFS();
        System.out.println("Append Locks and Close");
        String WRITTENCONTENT = "ABC..XYZ";
        String APPENDEDCONTENT = "*** Appended Text ***";
        try {
            try (Writer wtr = tempfs.getOutputStreamWriter("test1")) {
                wtr.write(WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        FileObject fo = tempfs.getFileObject("test1");
        assertNotNull(fo);
        try (Writer writer = tempfs.getOutputStreamWriter("test1", true)) {
            assertNotNull(writer);
            assertTrue(fo.isLocked());
            writer.write(APPENDEDCONTENT);
        }
        assertFalse(fo.isLocked());
        assertEquals(fo.asText(), WRITTENCONTENT + APPENDEDCONTENT);
    }
}
