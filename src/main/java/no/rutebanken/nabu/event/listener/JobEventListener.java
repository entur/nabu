/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.event.listener;

import org.entur.pubsub.base.AbstractEnturGooglePubSubConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("!pubsub-listener-disabled")
public class JobEventListener extends AbstractEnturGooglePubSubConsumer {

    public static final String JOB_EVENT_QUEUE = "JobEventQueue";

    @Autowired
    private JobEventProcessor jobEventProcessor;

    @Override
    protected String getDestinationName() {
        return JOB_EVENT_QUEUE;
    }

    @Override
    public void onMessage(byte[] content, Map<String, String> headers) {
        jobEventProcessor.processMessage(new String(content));
    }
}
