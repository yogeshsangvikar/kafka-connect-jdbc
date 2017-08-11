/*
 * Copyright 2016 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.confluent.connect.jdbc.sink.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import io.confluent.connect.jdbc.sink.DbMetadataQueries;

public class TableMetadataLoadingCache {
  private static final Logger log = LoggerFactory.getLogger(TableMetadataLoadingCache.class);

  private final Map<String, DbTable> cache = new HashMap<>();

  public DbTable get(final Connection connection, final String tableName) throws SQLException {
    DbTable dbTable = cache.get(tableName);
    if (dbTable == null) {
      if (DbMetadataQueries.doesTableExist(connection, tableName)) {
        dbTable = DbMetadataQueries.getTableMetadata(connection, tableName);
        cache.put(tableName, dbTable);
      } else {
        return null;
      }
    }
    return dbTable;
  }

  public DbTable refresh(final Connection connection, final String tableName) throws SQLException {
    DbTable dbTable = DbMetadataQueries.getTableMetadata(connection, tableName);
    log.info("Updating cached metadata -- {}", dbTable);
    cache.put(dbTable.name, dbTable);
    return dbTable;
  }
}
