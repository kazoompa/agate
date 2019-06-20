/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.agate.config;

import java.util.concurrent.Executor;

import org.obiba.agate.async.ExceptionHandlingAsyncTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;


@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer, EnvironmentAware {

  private static final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

  private static final int DEFAULT_MAX_POOL_SIZE = 50;

  private static final int DEFAULT_QUEUE_CAPACITY = 10000;

  private static final int DEFAULT_POOL_SIZE = 2;

  private RelaxedPropertyResolver propertyResolver;

  @Override
  public void setEnvironment(Environment environment) {
    propertyResolver = new RelaxedPropertyResolver(environment, "async.");
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  @Bean
  @Override
  public Executor getAsyncExecutor() {
    log.debug("Creating Async Task Executor");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(propertyResolver.getProperty("corePoolSize", Integer.class, DEFAULT_POOL_SIZE));
    executor.setMaxPoolSize(propertyResolver.getProperty("maxPoolSize", Integer.class, DEFAULT_MAX_POOL_SIZE));
    executor.setQueueCapacity(propertyResolver.getProperty("queueCapacity", Integer.class, DEFAULT_QUEUE_CAPACITY));
    executor.setThreadNamePrefix("agate-executor-");
    return new ExceptionHandlingAsyncTaskExecutor(executor);
  }
}
