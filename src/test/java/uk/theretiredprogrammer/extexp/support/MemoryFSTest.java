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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

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
        //
        //  run a set of tests to check results on empty file store
        //
        System.out.println("getallnames");
        assertEquals(0, tempfs.allnames().size());
        System.out.println("exists");
        assertEquals(false, tempfs.exists("not_a_file"));
        System.out.println("fo");
        assertNull(tempfs.getFileObject("not_a_file"));
        System.out.println("content");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  write first file then run set of tests to check results
        //
        System.out.println("write");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            tempfs.write("written", WRITTENCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        System.out.println("getallnames - after write");
        assertEquals(1, tempfs.allnames().size());
        System.out.println("exists - after write");
        assertEquals(false, tempfs.exists("not_a_file"));
        assertEquals(true, tempfs.exists("written"));
        System.out.println("fo - after write");
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo = tempfs.getFileObject("written");
        assertNotNull(fo);
        assertEquals("written", fo.getNameExt());
        System.out.println("content - after write");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
            Optional<String> content = tempfs.read("written");
            assertTrue(content.isPresent());
            assertEquals(WRITTENCONTENT, content.get());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  append to new file then run set of tests to check results
        //
        System.out.println("append new");
        String APPENDNEWCONTENT = "123..890";
        try {
            tempfs.append("append.new", APPENDNEWCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        System.out.println("getallnames - after append new");
        assertEquals(2, tempfs.allnames().size());
        System.out.println("exists - after append new");
        assertEquals(false, tempfs.exists("not_a_file"));
        assertEquals(true, tempfs.exists("append.new"));
        System.out.println("fo - after append new");
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo2 = tempfs.getFileObject("append.new");
        assertNotNull(fo2);
        assertEquals("append.new", fo2.getNameExt());
        System.out.println("content - after append new");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
            Optional<String> content = tempfs.read("append.new");
            assertTrue(content.isPresent());
            assertEquals(APPENDNEWCONTENT, content.get());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  append to written then run set of tests to check results
        //
        System.out.println("append existing");
        String APPENDCONTENT = "234..678";
        try {
            tempfs.append("written", APPENDCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        System.out.println("getallnames - after append");
        assertEquals(2, tempfs.allnames().size());
        System.out.println("exists - after append");
        assertEquals(false, tempfs.exists("not_a_file"));
        assertEquals(true, tempfs.exists("written"));
        System.out.println("fo - after append");
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo3 = tempfs.getFileObject("written");
        assertNotNull(fo3);
        assertEquals("written", fo3.getNameExt());
        System.out.println("content - after append");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
            Optional<String> content = tempfs.read("written");
            assertTrue(content.isPresent());
            assertEquals(WRITTENCONTENT + APPENDCONTENT, content.get());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  overwrite existing file with less content then run set of tests to check results
        //
        System.out.println("Overwrite existing with less");
        String NEWCONTENT = "P..W";
        try {
            tempfs.write("append.new", NEWCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        System.out.println("getallnames - after overwrite");
        assertEquals(2, tempfs.allnames().size());
        System.out.println("exists - after overwrite");
        assertEquals(false, tempfs.exists("not_a_file"));
        assertEquals(true, tempfs.exists("append.new"));
        System.out.println("fo - after overwrite");
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo4 = tempfs.getFileObject("append.new");
        assertNotNull(fo4);
        assertEquals("append.new", fo4.getNameExt());
        System.out.println("content - after overwrite");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
            Optional<String> content = tempfs.read("append.new");
            assertTrue(content.isPresent());
            assertEquals(NEWCONTENT, content.get());
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        //
        //  overwrite existing file with more content then run set of tests to check results
        //
        System.out.println("Overwrite existing - with more");
        String NEWCONTENT2 = "abcdefghijklmnopqrstuvwxyz";
        try {
            tempfs.write("append.new", NEWCONTENT2);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        System.out.println("getallnames - after overwrite");
        assertEquals(2, tempfs.allnames().size());
        System.out.println("exists - after overwrite");
        assertEquals(false, tempfs.exists("not_a_file"));
        assertEquals(true, tempfs.exists("append.new"));
        System.out.println("fo - after overwrite");
        assertNull(tempfs.getFileObject("not_a_file"));
        FileObject fo5 = tempfs.getFileObject("append.new");
        assertNotNull(fo5);
        assertEquals("append.new", fo5.getNameExt());
        System.out.println("content - after overwrite");
        try {
            assertFalse(tempfs.read("not_a_file").isPresent());
            Optional<String> content = tempfs.read("append.new");
            assertTrue(content.isPresent());
            assertEquals(NEWCONTENT2, content.get());
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
            tempfs.write("test1", WRITTENCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        Reader rdr = tempfs.getInputStreamReader("test1");
        try {
            int c;
            String res = "";
            while ((c = rdr.read()) != -1) {
                res = res+((char)c);
            }
            assertEquals(res, WRITTENCONTENT);
            rdr.close();
        } catch (IOException ex) {
            fail("Unexpected exception while reading- " + ex.getLocalizedMessage());
        }
        System.out.println("Streams and Readers - simple writer then reader");
        String WRITTENCONTENT2 = "PQR...WXY";
        try {
            try (Writer wtr = tempfs.getOutputStreamWriter("test2")) {
                wtr.write(WRITTENCONTENT2);
            }
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        try {
            try (Reader rdr2 = tempfs.getInputStreamReader("test2")){
            int c;
            String res = "";
            while ((c = rdr2.read()) != -1) {
                res = res+((char)c);
            }
            assertEquals(res, WRITTENCONTENT2);
            }
        } catch (IOException ex) {
            fail("Unexpected exception while reading- " + ex.getLocalizedMessage());
        }
    }
    
    /**
     * Test of streamsource methods in class MemoryFS.
     */
    @Test
    public void testStreamSource() {
        MemoryFS tempfs = new MemoryFS();
        System.out.println("StreamSource");
        String WRITTENCONTENT = "ABC..XYZ";
        try {
            tempfs.write("test1", WRITTENCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        StreamSource ss = new StreamSource();
        assertTrue(ss.isEmpty());
        try {
            try( Reader rdr = tempfs.getInputStreamReader("test1")){
            StreamSource in = new StreamSource(rdr);
            assertFalse(in.isEmpty());
            int c;
            String res = "";
            while ((c = rdr.read()) != -1) {
                res = res+((char)c);
            }
            assertEquals(res, WRITTENCONTENT);
            }
        } catch (IOException ex) {
            fail("Unexpected exception while reading- " + ex.getLocalizedMessage());
        }
    }
    
    /**
     * Test of locks and close in class MemoryFS.
     * @throws java.io.IOException if problems
     */
    @Test
    public void testLocksAndClose()  throws IOException{
        MemoryFS tempfs = new MemoryFS();
        System.out.println("Locks and Close");
        FileObject fo = tempfs.getFileObject("test2");
        assertNull(fo);
        BufferedWriter writer = new BufferedWriter(tempfs.getOutputStreamWriter("test2"));
        assertNotNull(writer);
        fo = tempfs.getFileObject("test2");
        assertNotNull(fo);
        assertTrue(fo.isLocked());
        writer.close();
        assertFalse(fo.isLocked());
    }
    
    /**
     * Test of locks and close in class MemoryFS.
     * @throws java.io.IOException if problems
     */
    @Test
    public void testAppendLocksAndClose()  throws IOException{
        MemoryFS tempfs = new MemoryFS();
        System.out.println("Append Locks and Close");
        String WRITTENCONTENT = "ABC..XYZ";
        String APPENDEDCONTENT = "*** Appended Text ***";
        try {
            tempfs.write("test1", WRITTENCONTENT);
        } catch (IOException ex) {
            fail("Unexpected exception - " + ex.getLocalizedMessage());
        }
        FileObject fo = tempfs.getFileObject("test1");
        assertNotNull(fo);
        BufferedWriter writer = new BufferedWriter(tempfs.getOutputStreamWriter("test1", true));
        assertNotNull(writer);
        assertTrue(fo.isLocked());
        writer.write(APPENDEDCONTENT);
        writer.close();
        assertFalse(fo.isLocked());
        assertEquals(tempfs.read("test1").get(),WRITTENCONTENT+APPENDEDCONTENT);
    }
}
