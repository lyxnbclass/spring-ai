/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.model.google.genai.autoconfigure.embedding;

import org.junit.jupiter.api.Test;

import org.springframework.ai.google.genai.embedding.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleGenAiEmbeddingConnectionAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(GoogleGenAiEmbeddingConnectionAutoConfiguration.class,
				GoogleGenAiTextEmbeddingAutoConfiguration.class));

	@Test
	void sharedApiKeyCreatesEmbeddingModel() {
		this.contextRunner.withPropertyValues("spring.ai.google.genai.api-key=shared-key").run(context -> {
			assertThat(context).hasNotFailed();
			GoogleGenAiEmbeddingConnectionDetails details = context
				.getBean(GoogleGenAiEmbeddingConnectionDetails.class);
			assertThat(details.getApiKey()).isEqualTo("shared-key");
			assertThat(context).hasSingleBean(GoogleGenAiTextEmbeddingModel.class);
		});
	}

	@Test
	void sharedApiKeySupportsRelaxedPropertyBinding() {
		this.contextRunner.withPropertyValues("spring.ai.google.genai.apiKey=shared-key").run(context -> {
			assertThat(context).hasNotFailed();
			GoogleGenAiEmbeddingConnectionDetails details = context
				.getBean(GoogleGenAiEmbeddingConnectionDetails.class);
			assertThat(details.getApiKey()).isEqualTo("shared-key");
		});
	}

	@Test
	void embeddingApiKeyOverridesSharedApiKey() {
		this.contextRunner
			.withPropertyValues("spring.ai.google.genai.api-key=shared-key",
					"spring.ai.google.genai.embedding.api-key=embedding-key")
			.run(context -> {
				GoogleGenAiEmbeddingConnectionDetails details = context
					.getBean(GoogleGenAiEmbeddingConnectionDetails.class);
				assertThat(details.getApiKey()).isEqualTo("embedding-key");
			});
	}

	@Test
	void explicitEmbeddingVertexConfigurationDoesNotUseSharedApiKey() {
		this.contextRunner
			.withPropertyValues("spring.ai.google.genai.api-key=shared-key",
					"spring.ai.google.genai.embedding.project-id=test-project")
			.run(context -> {
				assertThat(context).hasFailed();
				assertThat(context.getStartupFailure()).rootCause()
					.hasMessageContaining("Google GenAI location must be set!");
			});
	}

	@Test
	void explicitVertexModeDoesNotUseSharedApiKey() {
		this.contextRunner
			.withPropertyValues("spring.ai.google.genai.api-key=shared-key",
					"spring.ai.google.genai.embedding.vertex-ai=true")
			.run(context -> {
				assertThat(context).hasFailed();
				assertThat(context.getStartupFailure()).rootCause()
					.hasMessageContaining("Google GenAI project-id must be set!");
			});
	}

	@Test
	void sharedVertexModeDoesNotUseSharedApiKey() {
		this.contextRunner
			.withPropertyValues("spring.ai.google.genai.api-key=shared-key", "spring.ai.google.genai.vertex-ai=true")
			.run(context -> {
				assertThat(context).hasFailed();
				assertThat(context.getStartupFailure()).rootCause()
					.hasMessageContaining("Google GenAI project-id must be set!");
			});
	}

}
