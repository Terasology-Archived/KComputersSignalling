/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.kcomputerssignalling.peripherals;

import org.terasology.blockNetwork.BlockNetworkUtil;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.kallisti.base.component.ComponentMethod;
import org.terasology.kallisti.base.component.Peripheral;
import org.terasology.kcomputers.KComputersUtil;
import org.terasology.signalling.components.SignalConsumerAdvancedStatusComponent;
import org.terasology.signalling.components.SignalConsumerStatusComponent;
import org.terasology.signalling.components.SignalProducerComponent;
import org.terasology.signalling.components.SignalProducerModifiedComponent;
import org.terasology.world.block.BlockComponent;

public class PeripheralSignalTransceiver implements Peripheral {
    private final EntityRef self;

    public PeripheralSignalTransceiver(EntityRef self) {
        this.self = self;
    }

    @ComponentMethod
    public int getInput(Number side) {
        if (self.hasComponent(SignalConsumerAdvancedStatusComponent.class) && self.hasComponent(BlockComponent.class)) {
            SignalConsumerAdvancedStatusComponent component = self.getComponent(SignalConsumerAdvancedStatusComponent.class);

            String name = BlockNetworkUtil.getResultSide(self.getComponent(BlockComponent.class).getBlock(), KComputersUtil.getOCSide(side.intValue())).name();
            return component.signalStrengths.getOrDefault(name, 0);
        } else if (self.hasComponent(SignalConsumerStatusComponent.class)) {
            return self.getComponent(SignalConsumerStatusComponent.class).hasSignal ? 1 : 0;
        } else {
            return 0;
        }
    }

    @ComponentMethod
    public int getOutput(Number side) {
        if (self.hasComponent(SignalProducerComponent.class)) {
            return self.getComponent(SignalProducerComponent.class).signalStrength;
        } else {
            return 0;
        }
    }

    @ComponentMethod
    public int setOutput(Number side, Number value) {
        if (self.hasComponent(SignalProducerComponent.class)) {
            self.getComponent(SignalProducerComponent.class).signalStrength = value.intValue();
            self.saveComponent(self.getComponent(SignalProducerComponent.class));
            self.addComponent(new SignalProducerModifiedComponent());
            return value.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public String type() {
        return "signal_transceiver";
    }
}
