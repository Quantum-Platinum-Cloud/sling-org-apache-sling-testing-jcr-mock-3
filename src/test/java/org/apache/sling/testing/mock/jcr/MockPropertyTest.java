/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.testing.mock.jcr;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.value.BinaryValue;
import org.junit.Test;

public class MockPropertyTest extends AbstractItemTest {

    @Test
    public void testRemove() throws RepositoryException {
        this.node1.setProperty("prop1", "value1");
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals("value1", prop1.getString());

        prop1.remove();
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testString() throws RepositoryException {
        this.node1.setProperty("prop1", "value1");
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals("value1", prop1.getString());
        assertEquals("value1", prop1.getValue().getString());

        prop1.setValue("value2");
        assertEquals("value2", prop1.getString());
        assertEquals("value2", prop1.getValue().getString());

        assertFalse(prop1.isMultiple());
        assertFalse(prop1.getDefinition().isMultiple());
        assertEquals(6, prop1.getLength());

        assertFalse(prop1.getDefinition().isProtected());
        assertFalse(prop1.getDefinition().isAutoCreated());
        assertFalse(prop1.getDefinition().isMandatory());
        assertFalse(prop1.getDefinition().isFullTextSearchable());
        assertFalse(prop1.getDefinition().isQueryOrderable());
    }

    @Test
    public void testStringSetNullViaNode() throws RepositoryException {
        this.node1.setProperty("prop1", "value1");

        this.node1.setProperty("prop1", (String)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testStringSetNullViaProp() throws RepositoryException {
        this.node1.setProperty("prop1", "value1");

        this.node1.getProperty("prop1").setValue((String)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testStringArray() throws RepositoryException {
        String[] value1 = new String[] { "aaa", "bbb" };
        this.node1.setProperty("prop1", value1);
        Property prop1 = this.node1.getProperty("prop1");

        Value[] values = prop1.getValues();
        for (int i = 0; i < values.length; i++) {
            assertEquals("value #" + i, value1[i], values[i].getString());
        }

        String[] value2 = new String[] { "cc" };
        prop1.setValue(value2);
        values = prop1.getValues();
        for (int i = 0; i < values.length; i++) {
            assertEquals("value #" + i, value2[i], values[i].getString());
        }

        assertTrue(prop1.isMultiple());
        assertTrue(prop1.getDefinition().isMultiple());
        assertArrayEquals(new long[] { 2 }, prop1.getLengths());
    }

    @Test
    public void testStringArraySetNullViaNode() throws RepositoryException {
        String[] value1 = new String[] { "aaa", "bbb" };
        this.node1.setProperty("prop1", value1);

        this.node1.setProperty("prop1", (String[])null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testStringArraySetNullViaProp() throws RepositoryException {
        String[] value1 = new String[] { "aaa", "bbb" };
        this.node1.setProperty("prop1", value1);

        this.node1.getProperty("prop1").setValue((String[])null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testBoolean() throws RepositoryException {
        this.node1.setProperty("prop1", true);
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals(true, prop1.getBoolean());
        assertEquals(true, prop1.getValue().getBoolean());

        prop1.setValue(false);
        assertEquals(false, prop1.getBoolean());
        assertEquals(false, prop1.getValue().getBoolean());
    }

    @Test
    public void testDouble() throws RepositoryException {
        this.node1.setProperty("prop1", 1.5d);
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals(1.5d, prop1.getDouble(), 0.001d);
        assertEquals(1.5d, prop1.getValue().getDouble(), 0.001d);

        prop1.setValue(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, prop1.getDouble(), 0.001d);
        assertEquals(Double.MAX_VALUE, prop1.getValue().getDouble(), 0.001d);
    }

    @Test
    public void testLong() throws RepositoryException {
        this.node1.setProperty("prop1", 5L);
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals(5L, prop1.getLong());
        assertEquals(5L, prop1.getValue().getLong());

        prop1.setValue(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, prop1.getLong());
        assertEquals(Long.MAX_VALUE, prop1.getValue().getLong());
    }

    @Test
    public void testBigDecimal() throws RepositoryException {
        this.node1.setProperty("prop1", new BigDecimal("1.5"));
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals(new BigDecimal("1.5"), prop1.getDecimal());
        assertEquals(new BigDecimal("1.5"), prop1.getValue().getDecimal());

        prop1.setValue(new BigDecimal("99999999.99999"));
        assertEquals(new BigDecimal("99999999.99999"), prop1.getDecimal());
        assertEquals(new BigDecimal("99999999.99999"), prop1.getValue().getDecimal());
    }

    @Test
    public void testBigDecimalSetNullViaNode() throws RepositoryException {
        this.node1.setProperty("prop1", new BigDecimal("1.5"));

        this.node1.setProperty("prop1", (BigDecimal)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testBigDecimalSetNullViaProp() throws RepositoryException {
        this.node1.setProperty("prop1", new BigDecimal("1.5"));

        this.node1.getProperty("prop1").setValue((BigDecimal)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testCalendar() throws RepositoryException {
        Calendar value1 = Calendar.getInstance();

        this.node1.setProperty("prop1", value1);
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals(value1, prop1.getDate());
        assertEquals(value1, prop1.getValue().getDate());

        Calendar value2 = Calendar.getInstance();
        value2.add(Calendar.MONTH, -1);

        prop1.setValue(value2);
        assertEquals(value2, prop1.getDate());
        assertEquals(value2, prop1.getValue().getDate());
    }

    @Test
    public void testCalendarSetNullViaNode() throws RepositoryException {
        Calendar value1 = Calendar.getInstance();
        this.node1.setProperty("prop1", value1);

        this.node1.setProperty("prop1", (Calendar)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testCalendarSetNullViaProp() throws RepositoryException {
        Calendar value1 = Calendar.getInstance();
        this.node1.setProperty("prop1", value1);

        this.node1.getProperty("prop1").setValue((Calendar)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testBinary() throws RepositoryException, IOException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };

        this.node1.setProperty("prop1", new BinaryValue(value1).getBinary());
        Property prop1 = this.node1.getProperty("prop1");
        assertArrayEquals(value1, IOUtils.toByteArray(prop1.getBinary().getStream()));
        assertArrayEquals(value1, IOUtils.toByteArray(prop1.getValue().getBinary().getStream()));

        byte[] value2 = new byte[] { 0x04, 0x05, 0x06 };

        prop1.setValue(new BinaryValue(value2).getBinary());
        assertArrayEquals(value2, IOUtils.toByteArray(prop1.getBinary().getStream()));
        assertArrayEquals(value2, IOUtils.toByteArray(prop1.getValue().getBinary().getStream()));
    }

    @Test
    public void testBinarySetNullViaNode() throws RepositoryException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };
        this.node1.setProperty("prop1", new BinaryValue(value1).getBinary());

        this.node1.setProperty("prop1", (BinaryValue)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testBinarySetNullViaProp() throws RepositoryException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };
        this.node1.setProperty("prop1", new BinaryValue(value1).getBinary());

        this.node1.getProperty("prop1").setValue((BinaryValue)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInputStream() throws RepositoryException, IOException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };

        this.node1.setProperty("prop1", new ByteArrayInputStream(value1));
        Property prop1 = this.node1.getProperty("prop1");
        assertArrayEquals(value1, IOUtils.toByteArray(prop1.getStream()));

        byte[] value2 = new byte[] { 0x04, 0x05, 0x06 };

        prop1.setValue(new ByteArrayInputStream(value2));
        assertArrayEquals(value2, IOUtils.toByteArray(prop1.getValue().getStream()));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInputStreamSetNullViaNode() throws RepositoryException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };
        this.node1.setProperty("prop1", new ByteArrayInputStream(value1));

        this.node1.setProperty("prop1", (InputStream)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInputStreamSetNullViaProp() throws RepositoryException {
        byte[] value1 = new byte[] { 0x01, 0x01, 0x03 };
        this.node1.setProperty("prop1", new ByteArrayInputStream(value1));

        this.node1.getProperty("prop1").setValue((InputStream)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testValue() throws RepositoryException {
        this.node1.setProperty("prop1", this.session.getValueFactory().createValue("value1"));
        Property prop1 = this.node1.getProperty("prop1");
        assertEquals("value1", prop1.getString());
        assertEquals("value1", prop1.getValue().getString());

        prop1.setValue(this.session.getValueFactory().createValue("value2"));
        assertEquals("value2", prop1.getString());
        assertEquals("value2", prop1.getValue().getString());

        assertFalse(prop1.isMultiple());
        assertFalse(prop1.getDefinition().isMultiple());
        assertEquals(6, prop1.getLength());
    }

    @Test
    public void testValueSetNullViaNode() throws RepositoryException {
        this.node1.setProperty("prop1", this.session.getValueFactory().createValue("value1"));

        this.node1.setProperty("prop1", (Value)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testValueSetNullViaProp() throws RepositoryException {
        this.node1.setProperty("prop1", this.session.getValueFactory().createValue("value1"));

        this.node1.getProperty("prop1").setValue((Value)null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testValueArray() throws RepositoryException {
        Value[] value1 = new Value[] { this.session.getValueFactory().createValue("aaa"),
                this.session.getValueFactory().createValue("bbb") };
        this.node1.setProperty("prop1", value1);
        Property prop1 = this.node1.getProperty("prop1");

        Value[] values = prop1.getValues();
        for (int i = 0; i < values.length; i++) {
            assertEquals("value #" + i, value1[i].getString(), values[i].getString());
        }

        Value[] value2 = new Value[] { this.session.getValueFactory().createValue("cc") };
        prop1.setValue(value2);
        values = prop1.getValues();
        for (int i = 0; i < values.length; i++) {
            assertEquals("value #" + i, value2[i].getString(), values[i].getString());
        }

        assertTrue(prop1.isMultiple());
        assertTrue(prop1.getDefinition().isMultiple());
        assertArrayEquals(new long[] { 2 }, prop1.getLengths());
    }

    @Test
    public void testValueArraySetNullViaNode() throws RepositoryException {
        Value[] value1 = new Value[] { this.session.getValueFactory().createValue("aaa"),
                this.session.getValueFactory().createValue("bbb") };
        this.node1.setProperty("prop1", value1);

        this.node1.setProperty("prop1", (Value[])null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testValueArraySetNullViaProp() throws RepositoryException {
        Value[] value1 = new Value[] { this.session.getValueFactory().createValue("aaa"),
                this.session.getValueFactory().createValue("bbb") };
        this.node1.setProperty("prop1", value1);

        this.node1.getProperty("prop1").setValue((Value[])null);
        assertFalse(this.node1.hasProperty("prop1"));
    }

    @Test
    public void testEmptyArrayGetType() throws RepositoryException {
        this.node1.setProperty("prop1", new Value[] {});
        Property prop1 = this.node1.getProperty("prop1");
        assertTrue(prop1.isMultiple());
        assertEquals(PropertyType.UNDEFINED, prop1.getType());
    }

    @Test(expected=ValueFormatException.class)
    public void testSingleValueAsValueArray() throws RepositoryException {
        this.node1.setProperty("prop1", this.session.getValueFactory().createValue("value1"));
        Property prop1 = this.node1.getProperty("prop1");
        assertFalse(prop1.isMultiple());
        assertEquals("value1", prop1.getValues()[0].getString());
    }

    @Test
    public void testIsSameForPropComparedToItself() throws RepositoryException {
        assertTrue(this.prop1.isSame(this.prop1));
    }

    @Test
    public void testIsSameForPropComparedToSameProp() throws RepositoryException {
        // a different object referencing the same property
        Property prop1Ref = this.node1.getProperty("prop1");
        assertTrue(this.prop1.isSame(prop1Ref));
    }

    @Test
    public void testIsSameForPropComparedToDifferentPropFromSameParent() throws RepositoryException {
        Property prop2 = this.node1.setProperty("prop2", "value2");
        assertFalse(this.prop1.isSame(prop2));
    }

    @Test
    public void testIsSameForPropComparedToPropFromDifferentParent() throws RepositoryException {
        Property prop11 = this.node11.setProperty("prop1", "value1");
        assertFalse(this.prop1.isSame(prop11));
    }

    @Test
    public void testIsSameForPropFromDifferentRepository() throws RepositoryException {
        Repository otherRepository = MockJcr.newRepository();
        Session otherSession = otherRepository.login();
        Node otherNode1 = otherSession.getRootNode().addNode("node1");
        Property otherProp1 = otherNode1.setProperty("prop1", "value1");

        assertFalse(this.prop1.isSame(otherProp1));
    }

    @Test
    public void testIsSameForPropFromDifferentWorkspace() throws RepositoryException {
        Session otherSession = session.getRepository().login("otherWorkspace");
        Node otherRootNode = otherSession.getRootNode();
        Node otherNode1 = otherRootNode.addNode("node1");
        Property otherProp1 = otherNode1.setProperty("prop1", "value1");

        assertFalse(this.prop1.isSame(otherProp1));
    }

}
