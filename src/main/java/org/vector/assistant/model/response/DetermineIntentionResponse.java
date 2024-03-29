package org.vector.assistant.model.response;

import org.vector.assistant.model.dto.IntentionDto;

public record DetermineIntentionResponse(IntentionDto intention, Float distance) {}
