/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.xml.internal.html;


import java.io.StringReader;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;
import org.xwiki.xml.html.HTMLCleaner;
import org.xwiki.xml.html.HTMLCleanerConfiguration;
import org.xwiki.xml.html.HTMLUtils;
import org.xwiki.xml.internal.html.filter.AttributeFilter;
import org.xwiki.xml.internal.html.filter.BodyFilter;
import org.xwiki.xml.internal.html.filter.FontFilter;
import org.xwiki.xml.internal.html.filter.LinkFilter;
import org.xwiki.xml.internal.html.filter.ListFilter;
import org.xwiki.xml.internal.html.filter.ListItemFilter;
import org.xwiki.xml.internal.html.filter.UniqueIdFilter;


/**
 * Unit tests for {@link org.xwiki.xml.internal.html.DefaultHTMLCleaner}.
 *
 * @version $Id$
 * @since 1.6M1
 */
@ComponentList({ ListFilter.class, ListItemFilter.class, FontFilter.class, BodyFilter.class, AttributeFilter.class, UniqueIdFilter.class, DefaultHTMLCleaner.class, LinkFilter.class })
public class AmplDefaultHTMLCleanerTest {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");

    private static final String HEADER_FULL = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>";

    private static final String FOOTER = "</body></html>\n";

    @Rule
    public final MockitoComponentMockingRule<HTMLCleaner> mocker = new MockitoComponentMockingRule<HTMLCleaner>(DefaultHTMLCleaner.class);

    /**
     * Test that cleaning works when there's a TITLE element in the body (but with a namespace). The issue was that
     * HTMLCleaner would consider it a duplicate of the TITLE element in the HEAD even though it's namespaced. (see also
     * <a href="https://jira.xwiki.org/browse/XWIKI-9753">XWIKI-9753</a>).
     */
    @Test
    @Ignore("See https://jira.xwiki.org/browse/XWIKI-9753")
    public void cleanTitleWithNamespace() throws Exception {
        // Test with TITLE in HEAD
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n" + (((((((((((("  <head>\n" + "    <title>Title test</title>\n") + "  </head>\n") + "  <body>\n") + "    <p>before</p>\n") + "    <svg xmlns=\"http://www.w3.org/2000/svg\" height=\"300\" width=\"500\">\n") + "      <g>\n") + "        <title>SVG Title Demo example</title>\n") + "        <rect height=\"50\" style=\"fill:none; stroke:blue; stroke-width:1px\" width=\"200\" x=\"10\" ") + "y=\"10\"></rect>\n") + "      </g>\n") + "    </svg>\n") + "    <p>after</p>\n");
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input))));
    }

    /**
     * Verify that a xmlns namespace set on the HTML element is not removed by default and it's removed if
     * {@link HTMLCleanerConfiguration#NAMESPACES_AWARE} is set to false.
     */
    @Test
    @Ignore("See https://sourceforge.net/p/htmlcleaner/bugs/168/")
    public void cleanHTMLTagWithNamespace() throws Exception {
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>";
        // Default
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input))));
        // Configured for namespace awareness being false
        HTMLCleanerConfiguration config = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        config.setParameters(Collections.singletonMap(HTMLCleanerConfiguration.NAMESPACES_AWARE, "false"));
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input), config)));
    }

    private void assertHTML(String expected, String actual) throws ComponentLookupException {
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual))));
    }

    private void assertHTMLWithHeadContent(String expected, String actual) throws ComponentLookupException {
        Assert.assertEquals((((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head>") + expected) + "</head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual))));
    }
}

