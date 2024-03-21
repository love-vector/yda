package org.vector.assistant.util.converter;

import java.util.ArrayList;
import java.util.List;

public final class EmbeddingConverter {

    public static List<Float> normalizeVector(final List<Double> vector) {
        var norm = Double.valueOf(0.0);
        for (var component : vector) {
            norm += component * component;
        }
        norm = Math.sqrt(norm);

        List<Float> normalizedVector = new ArrayList<>(vector.size());
        for (double component : vector) {
            normalizedVector.add((float) (component / norm));
        }
        return normalizedVector;
    }

    private EmbeddingConverter() {}
}
