/*
 * Copyright 2011-2025 Lime Mojito Pty Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.limemojito.aws.lambda;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static lombok.AccessLevel.PROTECTED;

/**
 * A concrete implementation of this class we be picked up in a spring application and behave as an AWS SnapStart
 * Optimizer by participating in the preCheckpoint and postCheckpoint lifecycle.
 *
 * @see <a href="https://github.com/CRaC/org.crac">CRaC Lifecycle</a>
 */
@Slf4j
public abstract class SnapStartOptimizer implements Resource, ApplicationContextAware {
    @Getter(value = PROTECTED)
    private ApplicationContext applicationContext;

    /**
     * Links the application context to CRaC lifecycle.
     *
     * @param applicationContext Spring application context.
     * @throws BeansException if unable to register
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Core.getGlobalContext().register(this);
        log.info("SnapStart Optimizer {} registered", this.getClass().getSimpleName());
    }

    /**
     * CRaC callback for pre checkpoint (AWS SnapStart snapshot)
     *
     * @param context CRaC context
     * @throws Exception on a callback failure.
     */
    @Override
    public final void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        log.info("Before Checkpoint");
        performBeforeCheckpoint();
    }

    /**
     * CRaC callback for pre checkpoint (AWS SnapStart snapshot)
     *
     * @param context CRaC context
     * @throws Exception on a callback failure.
     */
    @Override
    public final void afterRestore(Context<? extends Resource> context) throws Exception {
        log.info("After Restore");
        performAfterRestore();
    }

    /**
     * Steps to execute to exercise system.  Run some code to exercise the system by loading classes, attempting network
     * connections, etc.
     *
     * @throws Exception on a failure
     * @see #swallowError(Runnable)
     */
    protected abstract void performBeforeCheckpoint() throws Exception;


    /**
     * Validate that we can retrieve the bean used if there is a SPRING_CLOUD_FUNCTION_DEFINITION environment variable
     * set.  This is used often in Spring Cloud Function lambda setups.
     */
    protected void checkSpringCloudFunctionDefinitionBean() {
        final String envName = "SPRING_CLOUD_FUNCTION_DEFINITION";
        final String springCloudFunctionDefinition = System.getenv().get(envName);
        if (springCloudFunctionDefinition != null) {
            log.debug("Checking function bean retrieval for {}", springCloudFunctionDefinition);
            final Object bean = getApplicationContext().getBean(springCloudFunctionDefinition);
            log.info("Retrieved bean instance {}", bean.getClass().getName());
        } else {
            log.warn("No {} environment variable set", envName);
        }
    }

    /**
     * Perform a function and swallow any exceptions.
     * ie {@code swallowError( () -> dynamoDb.scan(...) )}
     *
     * @param runnable Steps to perform.
     */
    protected void swallowError(Runnable runnable) {
        log.debug("Performing swallowError");
        try {
            runnable.run();
        } catch (Exception e) {
            log.info("swallowError threw an exception: {}", e.getClass().getSimpleName());
        }
    }

    /**
     * Override if after checkpoint processing required.    Run some code to exercise the system by loading classes,
     * attempting network connections, etc.
     *
     * @throws Exception on a failure
     * @see #swallowError(Runnable)
     */
    protected void performAfterRestore() throws Exception {

    }
}
