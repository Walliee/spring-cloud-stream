/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.cloud.stream.binding;

import org.springframework.integration.handler.BridgeHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * A {@link StreamListenerResultAdapter} used for bridging an {@link org.springframework.cloud.stream.annotation.Output}
 * {@link MessageChannel} to a bound {@link MessageChannel}.
 * @author Marius Bogoevici
 */
public class MessageChannelStreamListenerResultAdapter
		implements StreamListenerResultAdapter<MessageChannel, MessageChannel> {

	@Override
	public boolean supports(Class<?> resultType, Class<?> boundElement) {
		return MessageChannel.class.isAssignableFrom(resultType)
				&& MessageChannel.class.isAssignableFrom(boundElement);
	}

	@Override
	public void adapt(MessageChannel streamListenerResult, MessageChannel boundElement) {
		BridgeHandler handler = new BridgeHandler();
		handler.setOutputChannel(boundElement);
		handler.afterPropertiesSet();
		((SubscribableChannel) streamListenerResult).subscribe(handler);
	}
}