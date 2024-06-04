package ai.yda.vector.store.milvus;

import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.param.index.DropIndexParam;

import org.springframework.ai.embedding.EmbeddingModel;

import ai.yda.framework.rag.shared.store.vector.CollectionDoesNotExistException;
import ai.yda.framework.rag.shared.store.vector.VectorStore;

public class MilvusVectorStore extends org.springframework.ai.vectorstore.MilvusVectorStore implements VectorStore {

    private final Integer embeddingDimension;

    private final MilvusServiceClient milvusClient;

    public MilvusVectorStore(
            MilvusServiceClient milvusClient, EmbeddingModel embeddingModel, Integer embeddingDimension) {
        super(milvusClient, embeddingModel, Boolean.FALSE);
        this.milvusClient = milvusClient;
        this.embeddingDimension = embeddingDimension;
    }

    public MilvusVectorStore(
            MilvusServiceClient milvusClient,
            EmbeddingModel embeddingModel,
            MilvusVectorStoreConfig config,
            Integer embeddingDimension) {
        super(milvusClient, embeddingModel, config, Boolean.FALSE);
        this.milvusClient = milvusClient;
        this.embeddingDimension = embeddingDimension;
    }

    @Override
    public void createCollection(final String collectionName) throws CollectionDoesNotExistException {
        if (!isCollectionExists(collectionName)) {
            FieldType docIdFieldType = FieldType.newBuilder()
                    .withName("doc_id")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(36)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();
            FieldType contentFieldType = FieldType.newBuilder()
                    .withName("content")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(65535)
                    .build();
            FieldType metadataFieldType = FieldType.newBuilder()
                    .withName("metadata")
                    .withDataType(DataType.JSON)
                    .build();
            FieldType embeddingFieldType = FieldType.newBuilder()
                    .withName("embedding")
                    .withDataType(DataType.FloatVector)
                    .withDimension(embeddingDimension)
                    .build();
            CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("Spring AI Vector Store")
                    .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                    .withShardsNum(2)
                    .addFieldType(docIdFieldType)
                    .addFieldType(contentFieldType)
                    .addFieldType(metadataFieldType)
                    .addFieldType(embeddingFieldType)
                    .build();
            R<RpcStatus> collectionStatus = this.milvusClient.createCollection(createCollectionReq);
            if (collectionStatus.getException() != null) {
                throw new RuntimeException("Failed to create collection", collectionStatus.getException());
            }
        }

        R<DescribeIndexResponse> indexDescriptionResponse =
                this.milvusClient.describeIndex(DescribeIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        R loadCollectionStatus;
        if (indexDescriptionResponse.getData() == null) {
            loadCollectionStatus = this.milvusClient.createIndex(CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName("embedding")
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.COSINE)
                    .withExtraParam("{\"nlist\":1024}")
                    .withSyncMode(Boolean.FALSE)
                    .build());
            if (loadCollectionStatus.getException() != null) {
                throw new RuntimeException("Failed to create Index", loadCollectionStatus.getException());
            }
        }

        loadCollectionStatus = this.milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        if (loadCollectionStatus.getException() != null) {
            throw new RuntimeException("Collection loading failed!", loadCollectionStatus.getException());
        }
    }

    @Override
    public void deleteCollection(final String collectionName) throws CollectionDoesNotExistException {
        R<RpcStatus> status = this.milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        if (status.getException() != null) {
            throw new RuntimeException("Release collection failed!", status.getException());
        } else {
            status = this.milvusClient.dropIndex(DropIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build());
            if (status.getException() != null) {
                throw new RuntimeException("Drop Index failed!", status.getException());
            } else {
                status = this.milvusClient.dropCollection(DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
                if (status.getException() != null) {
                    throw new RuntimeException("Drop Collection failed!", status.getException());
                }
            }
        }
    }

    public Boolean isCollectionExists(final String collectionName) {
        return this.milvusClient
                .hasCollection(HasCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build())
                .getData();
    }
}
