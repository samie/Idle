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

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

/**
 *
 * @author Max Schuster
 */
public class IdleDemoPage {
    
    private final WebDriver driver;
    
    @FindBy(id = "status")
    private WebElement status;
    
    @FindBy(tagName = "body")
    private WebElement body;

    public IdleDemoPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public String getStatusText() {
        return status.getText();
    }
    
    public String getBodyClass() {
        return body.getAttribute("class");
    }
    
    public IdleDemoPage clickBody() {
        body.click();
        return this;
    }
    
}
