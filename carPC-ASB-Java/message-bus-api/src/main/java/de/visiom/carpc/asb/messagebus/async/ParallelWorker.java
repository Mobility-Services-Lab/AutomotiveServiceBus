/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.asb.messagebus.async;

import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for executing specific code in an infinite loop.
 * 
 * @author david
 * 
 */
public abstract class ParallelWorker {

    private static final Logger LOG = LoggerFactory
            .getLogger(ParallelWorker.class);

    private static final String INIT_CAUGHT_EXCEPTION_LOG_MESSAGE = "An exception was caught during the initialization of {}: {}";

    private int maximumNumberOfExecutions = -1;

    private int currentNumberOfExecutions = 0;

    /**
     * Starts the parallel execution of this worker class
     */
    public void start() {
        WorkerThread workerThread = new WorkerThread(getExecutionInterval());
        try {
            initialize();
            workerThread.start();
        } catch (Exception e) {
            // Don't start the thread if initialization was interrupted
            LOG.error(INIT_CAUGHT_EXCEPTION_LOG_MESSAGE, this.getClass()
                    .getName(), e);
        }
    }

    /**
     * The interval (in ms) between the executions of the {@link #execute()
     * execute} method.
     * 
     * @return
     */
    protected abstract long getExecutionInterval();

    /**
     * This method will be executed asynchronously in a new thread.
     */
    protected abstract void execute();

    /**
     * Performs initialization before starting to execute in parallel
     */
    protected void initialize() throws Exception {
        // initialization
    }

    public void setMaximumNumberOfExecutions(int maximumNumberOfExecutions) {
        this.maximumNumberOfExecutions = maximumNumberOfExecutions;
    }

    class WorkerThread extends Thread {

        private static final String EXECUTE_CAUGHT_EXCEPTION_LOG_MESSAGE = "An exception was caught during the execution of {}: {} -- {}";

        private static final String STOPPING_LOG_MESSAGE = "Stopping the execution of {}...";

        private final long executionInterval;

        private boolean stopAfterCurrentIteration = false;

        private final String className = ParallelWorker.this.getClass()
                .getName();

        public WorkerThread(long executionInterval) {
            this.executionInterval = executionInterval;
        }

        @Override
        public void run() {
            while (shouldExecutionContinue()) {
                try {
                    execute();
                } catch (ServiceUnavailableException sue) {
                    // Blueprint service has been stopped
                    LOG.info(STOPPING_LOG_MESSAGE, className);
                    stopAfterCurrentIteration = true;
                } catch (Exception e) {
                    LOG.warn(EXECUTE_CAUGHT_EXCEPTION_LOG_MESSAGE, className,
                            e, e.getStackTrace()[0]);
                }
                if (executionInterval > 0) {
                    pause();
                }
                if (maximumNumberOfExecutions > -1) {
                    currentNumberOfExecutions++;
                }
            }
        }

        private void pause() {
            synchronized (this) {
                try {
                    wait(executionInterval);
                } catch (InterruptedException e) {
                    LOG.error("Worker Thread was interruped: {}", e);
                }
            }
        }

        private boolean shouldExecutionContinue() {
            if (maximumNumberOfExecutions > -1) {
                return currentNumberOfExecutions <= maximumNumberOfExecutions;
            }
            return !stopAfterCurrentIteration;
        }
    }

}
