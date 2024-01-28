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

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/** Page pattern for the IdleDemo.
 *
 * @author Max Schuster
 */
public class IdleDemoPage {

    private Page page;

    public IdleDemoPage(Page page) {
        this.page = page;
    }

    public IdleDemoPage(Playwright playwright, String url) {
        Browser browser = playwright.chromium().launch();
        Page p = browser.newPage();
        p.navigate(url);
        page = p;
    }

    public String getStatusText() {
        return page.getByTitle("status").innerText();
    }
    
    public String getBodyClass() {
        return page.locator("body").getAttribute("class");
    }
    
    public IdleDemoPage clickBody() {
        page.locator("body").click();
        return this;
    }

    public void inactiveFor(int ms) {
        page.waitForTimeout(ms);
    }
}
