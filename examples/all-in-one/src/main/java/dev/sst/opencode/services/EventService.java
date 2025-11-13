package dev.sst.opencode.services;

import dev.sst.opencode.models.OpenCodeEvent;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * Service interface for event streaming operations
 */
public interface EventService {

	/**
	 * Subscribe to server events using reactive streams
	 */
	Flux<OpenCodeEvent> subscribeToEvents();

	/**
	 * Subscribe to events with a callback
	 */
	void subscribeWithCallback(Consumer<OpenCodeEvent> onEvent, Consumer<Throwable> onError, Runnable onComplete);

	/**
	 * Subscribe to events with filtering
	 */
	Flux<OpenCodeEvent> subscribeToEvents(String eventTypeFilter);

	/**
	 * Close event stream connection
	 */
	void closeEventStream();

}