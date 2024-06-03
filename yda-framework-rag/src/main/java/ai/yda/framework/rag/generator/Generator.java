package ai.yda.framework.rag.generator;

public interface Generator<REQUEST, CONTEXT, RESPONSE> {

    RESPONSE generate(REQUEST request, CONTEXT context);
}
