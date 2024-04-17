package org.vector.yda.model.response;

import org.vector.yda.model.dto.IntentionDto;

public record DetermineIntentionResponse(IntentionDto intention, Float distance) {}
