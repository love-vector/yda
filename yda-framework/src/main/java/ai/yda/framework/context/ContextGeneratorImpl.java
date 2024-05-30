package ai.yda.framework.context;

public class ContextGeneratorImpl implements ContextGenerator {

    @Override
    public Context generateContext(final RawContext rawContext) {
        return new Context();
    }
}
