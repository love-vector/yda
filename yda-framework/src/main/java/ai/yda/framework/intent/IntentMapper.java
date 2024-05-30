package ai.yda.framework.intent;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IntentMapper {

    IntentMapper INSTANCE = Mappers.getMapper(IntentMapper.class);

    IntentApproximation toApproximation(final Intent intent, Float distance);
}
