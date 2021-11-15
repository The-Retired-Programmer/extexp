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
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.openide.filesystems.FileObject;

/**
 * Tests for in-memory filesystem
 *
 * @author richard linsdale
 */
@TestMethodOrder(OrderAnnotation.class)
class MemoryFSTest {

    private MemoryFS tempfs = null;
    private FileObject tempfsroot = null;

    @BeforeEach
    void init() {
        tempfs = new MemoryFS();
        tempfsroot = tempfs.getRoot();
    }

    @AfterEach
    void tearDown() {
        tempfs = null;
        tempfsroot = null;
    }

    @Test
    @Order(1)
    @DisplayName("Constructor Checks")
    void constructor(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        assertEquals(0L, (long) tempfsroot.getChildren().length);
        assertNull(tempfs.getFileObject("not_a_file"));
        assertNull(tempfs.getInputStreamReader("not_a_file"));
    }

    @Test
    @Order(2)
    @DisplayName("Writer")
    void writer(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
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

    @Test
    @Order(3)
    @DisplayName("Writer append to new")
    void appendNewWriter(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
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

    @Test
    @Order(4)
    @DisplayName("Writer append to existing")
    void appendWriter(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
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

    @Test
    @Order(5)
    @DisplayName("Writer overwriting with less")
    void overwriteLessWriter(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        overwriteFile("ABC..XYZ", "P..W");
    }
    
    @Test
    @Order(6)
    @DisplayName("Writer overwriting with more")
    void overwriteMOREWriter(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        overwriteFile("ABC..XYZ", "abcdefghijklmnopqrstuvwxyz");
    }

    @Test
    @Order(7)
    @DisplayName("Reader from new")
    void readerFromNew(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
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

    @Test
    @Order(8)
    @DisplayName("Reader from appended")
    void readerFromAppended(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
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
