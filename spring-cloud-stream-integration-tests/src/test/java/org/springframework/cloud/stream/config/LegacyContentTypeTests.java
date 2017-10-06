/*
 * Copyright 2015-2017 the original author or authors.
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

package org.springframework.cloud.stream.config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Soby Chacko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { LegacyContentTypeTests.LegacyTestSink.class})
public class LegacyContentTypeTests {

	@Autowired
	private Sink testSink;

	@Test
	public void testOriginalContentTypeIsRetrievedForLegacyContentHeaderType() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		MessageHandler messageHandler = new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				assertThat(message.getPayload()).isInstanceOf(byte[].class);
				assertThat(message.getPayload()).isEqualTo("{\"message\":\"Hi\"}".getBytes());
				assertThat(message.getHeaders().get(MessageHeaders.CONTENT_TYPE)).isEqualTo("application/json");
				latch.countDown();
			}
		};
		testSink.input().subscribe(messageHandler);
		testSink.input().send(MessageBuilder.withPayload("{\"message\":\"Hi\"}".getBytes()).setHeader(BinderHeaders.BINDER_ORIGINAL_CONTENT_TYPE, "application/json").build());
		assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
		testSink.input().unsubscribe(messageHandler);
	}

	@EnableBinding(Sink.class)
	@EnableAutoConfiguration
	@PropertySource("classpath:/org/springframework/cloud/stream/config/channel/legacy-sink-channel-configurers.properties")
	public static class LegacyTestSink {

	}
}
