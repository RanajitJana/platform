/*
*  Copyright (c)  WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.identity.entitlement.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.entitlement.PDPConstants;

/**
 * 
 */
public class EntitlementPolicyClearingCache extends EntitlementBaseCache<IdentityCacheKey, IdentityCacheEntry>{

    private static EntitlementPolicyClearingCache entitlementPolicyCache = null;
    private static final Object lock = new Object(); 

    private EntitlementPolicyClearingCache() {
    	super(PDPConstants.ENTITLEMENT_POLICY_CACHE);
    }

    /**
     * the logger we'll use for all messages
     */
	private static Log log = LogFactory.getLog(EntitlementPolicyClearingCache.class);

	/**
	 * Gets a new instance of EntitlementPolicyClearingCache.
	 *
	 * @return A new instance of EntitlementPolicyClearingCache.
	 */
	public static EntitlementPolicyClearingCache getInstance() {
        if(entitlementPolicyCache == null){
            synchronized (lock){
                if(entitlementPolicyCache == null){
                    entitlementPolicyCache = new EntitlementPolicyClearingCache();
                }
            }
        }
        return entitlementPolicyCache;
	}

    public void addToCache(int hashCode){

        int tenantId = CarbonContext.getCurrentContext().getTenantId();
        IdentityCacheKey cacheKey = new IdentityCacheKey(tenantId, "");
        IdentityCacheEntry cacheEntry = new IdentityCacheEntry(hashCode);
        entitlementPolicyCache.addToCache(cacheKey, cacheEntry);
        if (log.isDebugEnabled()) {
            log.debug("Cache entry is added");
        }
    }

    public int getFromCache(){

        int hashCode = 0;
        int tenantId = CarbonContext.getCurrentContext().getTenantId();
        IdentityCacheKey cacheKey = new IdentityCacheKey(tenantId, "");
        Object entry = entitlementPolicyCache.getValueFromCache(cacheKey);
        if(entry != null){
            IdentityCacheEntry cacheEntry = (IdentityCacheEntry) entry;
            hashCode =  cacheEntry.getHashEntry();
            if (log.isDebugEnabled()) {
                log.debug("Cache entry is found");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cache entry is not found");
            }
        }

        return hashCode;
    }

    public void invalidateCache(){

        int tenantId = CarbonContext.getCurrentContext().getTenantId();
        IdentityCacheKey cacheKey = new IdentityCacheKey(tenantId, "");

        entitlementPolicyCache.clearCacheEntry(cacheKey);
            //sending cluster message
//            CacheInvalidator invalidator = EntitlementServiceComponent.getCacheInvalidator();
//            try {
//                if (invalidator != null) {
//                    invalidator.invalidateCache(PDPConstants.ENTITLEMENT_POLICY_CACHE, cacheKey);
//                    if (log.isDebugEnabled()) {
//                        log.debug("Calling invalidation cache");
//                    }
//                } else {
//                    if (log.isDebugEnabled()) {
//                        log.debug("Not calling invalidation cache");
//                    }
//                }
//            } catch (CacheException e) {
//                log.error("Error while invalidating cache", e);
//            }
    }
}
