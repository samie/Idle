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

import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.github.webdriverextensions.junitrunner.WebDriverRunner;
import com.github.webdriverextensions.junitrunner.annotations.PhantomJS;
import com.github.webdriverextensions.vaadin.VaadinBot;
import java.net.MalformedURLException;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.PageFactory;
import org.vaadin.addonhelpers.automated.AbstractWebDriverCase;
import org.vaadin.addons.idle.manual.demo.IdleDemo;

/**
 *
 * @author Max Schuster
 */
@RunWith(WebDriverRunner.class)
@PhantomJS
public class TestIdleDemo extends AbstractWebDriverCase {
    
    IdleDemoPage page;
    
    @Before
    public void setup() throws MalformedURLException {
        startBrowser(WebDriverExtensionsContext.getDriver());
        driver.navigate().to(
                new URL("http://localhost:5678/") + IdleDemo.class.getName());
        VaadinBot.waitForVaadin();
        page = PageFactory.initElements(driver, IdleDemoPage.class);
        clickNotification();
    }
    
    private void becomeInactive() {
        sleep(3500);
    }
    
    @Test
    public void testEventListenersBodyClass() {
        // Check initial state
        Assert.assertThat(page.getStatusText(), CoreMatchers.is("User active"));
        Assert.assertThat(page.getBodyClass(), CoreMatchers.containsString("useractive"));
        
        becomeInactive();
        Assert.assertThat(page.getStatusText(), CoreMatchers.is("User inactive"));
        Assert.assertThat(page.getBodyClass(), CoreMatchers.containsString("userinactive"));
        
        // becoming active again
        page.clickBody();
        Assert.assertThat(page.getStatusText(), CoreMatchers.is("User active"));
        Assert.assertThat(page.getBodyClass(), CoreMatchers.containsString("useractive"));
    }
    
}
