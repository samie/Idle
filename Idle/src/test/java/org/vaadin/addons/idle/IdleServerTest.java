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

package org.vaadin.addons.idle;

import com.vaadin.flow.component.UI;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IdleServerTest {
    
    private UI testUI;
    
    @BeforeEach
    public void before() {
        testUI = new UI();
    }

    @Test
    public void testTrack() {
        Assert.assertNotNull(Idle.track(testUI));
    }
    
    @Test
    public void testGet() {
        Idle idle = Idle.track(testUI);
        Assert.assertEquals(idle, Idle.get(testUI));
    }
    
    @Test()
    public void testDoubleTracking() {
        Idle.track(testUI);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Idle.track(testUI);
        });
    }
    
}
