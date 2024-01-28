/*
 * Copyright 2017 Sami Ekblad.
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
package org.vaadin.addons.idle.automated;

import com.microsoft.playwright.*;

import java.net.MalformedURLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 *
 * @author Sami Ekblad
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestIdleDemo  {

    @LocalServerPort
    private int port; // Use the random HTTP port assigned to test

    static Playwright playwright = Playwright.create();
    IdleDemoPage page;

    @BeforeEach
    public void setup() throws MalformedURLException {
        page = new IdleDemoPage(playwright,"http://localhost:" + port + "/test");
    }
    
    private void becomeInactive() {
        page.inactiveFor(2500);
    }
    
    @Test
    public void testEventListenersBodyClass() {

        // Check initial state
        Assertions.assertEquals("Server-side state: User active", page.getStatusText());
        Assertions.assertEquals("useractive",page.getBodyClass());

        becomeInactive();
        Assertions.assertEquals("Server-side state: User inactive", page.getStatusText());
        Assertions.assertEquals("userinactive",page.getBodyClass());

        // becoming active again
        page.clickBody();
        Assertions.assertEquals("Server-side state: User active", page.getStatusText());
        Assertions.assertEquals("useractive",page.getBodyClass());
    }
}
