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

    private MemoryFS tempfs = null;
    private FileObject tempfsroot = null;

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
        tempfs = new MemoryFS();
        tempfsroot = tempfs.getRoot();
    }

    @After
    public void tearDown() {
        tempfs = null;
        tempfsroot = null;
    }

    /**
     * Test of initial setup in class MemoryFS.
     */
    @Test
    public void testSetup() {
        System.out.println("Constructor Checks");
        assertEquals(0L, (long) tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNull(tempfs.getInputStreamReader("not_a_file"));
    }

    /**
     * Test of writing content in class MemoryFS.
     */
    @Test
    public void testWriter() {
        System.out.println("Writer");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            FileObject fo = writeToNewFile("written", WRITTENCONTENT);
            assertEquals(fo.asText(), WRITTENCONTENT);
            assertEquals(1, tempfsroot.getChildren().length);
            assertNull(tempfs.getFileObject("not_a_file"));
            assertNull(tempfs.getInputStreamReader("not_a_file"));
            assertEquals("written", fo.getNameExt());
            Reader reader;
            assertNotNull(reader = tempfs.getInputStreamReader("written"));
            reader.close();
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of appending content to new file in class MemoryFS.
     */
    @Test
    public void testAppendNewWriter() {
        System.out.println("writer append to new");
        try {
            String APPENDNEWCONTENT = "123..890";
            FileObject fo = appendToNewFile("append.new", APPENDNEWCONTENT);
            assertEquals(fo.asText(), APPENDNEWCONTENT);
            assertEquals(1, tempfsroot.getChildren().length);
            assertNull(tempfs.getFileObject("not_a_file"));
            assertNull(tempfs.getInputStreamReader("not_a_file"));
            assertEquals("append.new", fo.getNameExt());
            Reader reader;
            assertNotNull(reader = tempfs.getInputStreamReader("append.new"));
            reader.close();
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of appending content to existing file in class MemoryFS.
     */
    @Test
    public void testAppendWriter() {
        System.out.println("writer append to existing");
        String WRITTENCONTENT = "ABC..XYZ";
        String APPENDCONTENT = "234..678";
        try {
            FileObject fo = writeToNewFile("written", WRITTENCONTENT);
            assertEquals(fo.asText(), WRITTENCONTENT);
            appendToExistingFile("written", APPENDCONTENT, fo);
            assertEquals(1, tempfsroot.getChildren().length);
            assertNull(tempfs.getFileObject("not_a_file"));
            assertNotNull(tempfs.getFileObject("written"));
            assertNull(tempfs.getFileObject("not_a_file"));
            assertEquals("written", fo.getNameExt());
            assertEquals(WRITTENCONTENT + APPENDCONTENT, fo.asText());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of overwriting with less content to existing file in class MemoryFS.
     */
    @Test
    public void testOverwriteLessWriter() {
        System.out.println("writer overwriting with less");
        overwriteFile("ABC..XYZ", "P..W");

    }

    /**
     * Test of overwriting with more content to existing file in class MemoryFS.
     */
    @Test
    public void testOverwriteMOREWriter() {
        System.out.println("writer overwriting with more");
        overwriteFile("ABC..XYZ", "abcdefghijklmnopqrstuvwxyz");
    }

    /**
     * Test of file reader (reading a new file) in class MemoryFS.
     */
    @Test
    public void testReaderFromNew() {
        System.out.println("Reader from new");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            FileObject fo = writeToNewFile("test1", WRITTENCONTENT);
            try (Reader rdr = tempfs.getInputStreamReader("test1")) {
                int c;
                String res = "";
                while ((c = rdr.read()) != -1) {
                    res = res + ((char) c);
                }
                assertEquals(res, WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Test of file reader (reading an appended file) in class MemoryFS.
     */
    @Test
    public void testReaderFromAppended() {
        System.out.println("Reader from appended");
        String WRITTENCONTENT = "ABC..XYZ";
        String APPENDCONTENT = "234..678";
        try {
            FileObject fo = writeToNewFile("test1", WRITTENCONTENT);
            appendToExistingFile("test1", APPENDCONTENT, fo);
            assertEquals(WRITTENCONTENT + APPENDCONTENT, fo.asText());
            try (Reader rdr = tempfs.getInputStreamReader("test1")) {
                int c;
                String res = "";
                while ((c = rdr.read()) != -1) {
                    res = res + ((char) c);
                }
                assertEquals(res, WRITTENCONTENT + APPENDCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    private void overwriteFile(String initialcontent, String finalcontent) {
        try {
            FileObject fo = writeToNewFile("written", initialcontent);
            assertEquals(fo.asText(), initialcontent);
            writeToExistingFile("written", finalcontent, fo);
            assertEquals(fo.asText(), finalcontent);
            assertEquals(1, tempfsroot.getChildren().length);
            assertNull(tempfs.getFileObject("not_a_file"));
            assertNotNull(tempfs.getFileObject("written"));
            assertNull(tempfs.getFileObject("not_a_file"));
            assertEquals("written", fo.getNameExt());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    private FileObject writeToNewFile(String filename, String content) {
        FileObject fo = tempfs.getFileObject(filename);
        assertNull(fo);
        try {
            try (Writer writer = tempfs.getOutputStreamWriter(filename)) {
                assertNotNull(writer);
                fo = tempfs.getFileObject(filename);
                assertNotNull(fo);
                assertTrue(fo.isLocked());
                writer.write(content);
            }
            assertFalse(fo.isLocked());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        return fo;
    }

    private FileObject writeToExistingFile(String filename, String content, FileObject fo) {
        assertNotNull(fo);
        try {
            try (Writer writer = tempfs.getOutputStreamWriter(filename)) {
                assertNotNull(writer);
                assertTrue(fo.isLocked());
                writer.write(content);
            }
            assertFalse(fo.isLocked());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        return fo;
    }

    private void appendToExistingFile(String filename, String content, FileObject fo) {
        assertNotNull(fo);
        try {
            try (Writer writer = tempfs.getOutputStreamWriter(filename, true)) {
                assertNotNull(writer);
                assertTrue(fo.isLocked());
                writer.write(content);
            }
            assertFalse(fo.isLocked());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
    }

    private FileObject appendToNewFile(String filename, String content) {
        FileObject fo = tempfs.getFileObject(filename);
        assertNull(fo);
        try {
            try (Writer writer = tempfs.getOutputStreamWriter(filename, true)) {
                assertNotNull(writer);
                fo = tempfs.getFileObject(filename);
                assertNotNull(fo);
                assertTrue(fo.isLocked());
                writer.write(content);
            }
            assertFalse(fo.isLocked());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        return fo;
    }
}
