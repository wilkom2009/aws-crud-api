package com.wilkom.awscrudapi.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.wilkom.awscrudapi.model.Item;
import com.wilkom.awscrudapi.model.response.GetItemsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;

public class CrudDAO {

    private final DynamoDbClient ddbClt;
    private final DynamoDbEnhancedClient enhanceddbClient;
    private final String tableName;
    private final DynamoDbTable<Item> table;
    Logger logger = LoggerFactory.getLogger(CrudDAO.class);

    public CrudDAO() {
        SdkHttpClient httpClient = ApacheHttpClient.builder()
                .proxyConfiguration(ProxyConfiguration.builder()
                        .useSystemPropertyValues(true)
                        .build())
                .build();
        this.ddbClt = DynamoDbClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .build();
        this.enhanceddbClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddbClt)
                .build();
        this.tableName = Optional.ofNullable(System.getenv("TABLE_NAME")).orElse("crud-table");
        this.table = enhanceddbClient.table(tableName, TableSchema.fromBean(Item.class));
    }

    public String saveItem(Item item) {
        item.setId(UUID.randomUUID().toString());
        table.putItem(item);
        return item.getId();
    }


    public String updateItem(String id, Item item) {
        item.setId(id);
        table.putItem(item);
        return item.getId();
    }

    /**
     * Get item list
     * 
     * @return item list
     */
    public List<Item> getItemList() {
        List<Item> items = new ArrayList<>();
        try {
            Iterator<Item> results = table.scan().items().iterator();
            results.forEachRemaining(items::add);
            return items;
        } catch (DynamoDbException e) {
            return null;
        }
    }

    public Item getItemById(String id) {
        try {
            // Create a KEY object
            Key key = Key.builder()
                    .partitionValue(id)
                    .build();

            // Get the item by using the key
            Item result = table.getItem(r -> r.key(key));
            return result;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null;
    }

    public void deleteItem(String id) {
        try {
            // Create a KEY object
            Key key = Key.builder()
                    .partitionValue(id)
                    .build();

            // Get the item by using the key
            table.deleteItem(r -> r.key(key));
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
