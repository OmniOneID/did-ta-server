/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.tas.v1.common.service;

import org.omnione.did.data.model.did.DidDocument;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache for storing DID Documents with their corresponding timestamps.
 * This class uses a ConcurrentHashMap to store the cache entries, providing
 * thread-safe access and modifications.
 *
 * The cache entries consist of a DID Document and a timestamp, which are stored
 * in the nested static class CacheEntry.
 *
 */
public class DidDocCache {
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    /**
     * A cache entry for storing a DID Document and its timestamp.
     *
     */
    public static class CacheEntry {
        private DidDocument didDocument;
        private long timestamp;

        public CacheEntry(DidDocument didDocument, long timestamp) {
            this.didDocument = didDocument;
            this.timestamp = timestamp;
        }

        public DidDocument getDidDoc() {
            return didDocument;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Retrieve the DID Document associated with the given DID.
     * @param did The DID to retrieve the DID Document for
     * @return The DID Document associated with the given DID, or null if not found
     */
    public DidDocument getDidDoc(String did) {
        CacheEntry entry = cache.get(did);
        return (entry != null) ? entry.getDidDoc() : null;
    }

    /**
     * Store the given DID Document in the cache with the associated DID.
     * The current timestamp is used as the time of storage.
     * @param did The DID to store the DID Document for
     * @param didDoc The DID Document to store
     */
    public void putDidDoc(String did, DidDocument didDoc) {
        cache.put(did, new CacheEntry(didDoc, System.currentTimeMillis()));
    }

    /**
     * Checks if a DID Document is stored in the cache for the given DID.
     * @param did The DID to check for a stored DID Document
     * @return true if a DID Document is stored for the given DID, false otherwise
     */
    public boolean containsDidDoc(String did) {
        return cache.containsKey(did);
    }

    /**
     * Retrieve the timestamp when the DID Document was stored for the given DID.
     * @param did The DID to retrieve the timestamp for
     * @return The timestamp when the DID Document was stored, or 0 if not found
     */
    public long getTimestamp(String did) {
        CacheEntry entry = cache.get(did);
        return (entry != null) ? entry.getTimestamp() : 0;
    }

    /**
     * Return a set of all DID Documents currently stored in the cache.
     * @return A set of all DIDs currently stored in the cache
     *
     */
    public Set<String> getAllDids() {
        return cache.keySet();
    }
}
