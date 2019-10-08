package vn.vmgmedia.youtobe.common;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import vn.vmgmedia.youtobe.service.PlaylistService;

/** AsyncConfiguration
 * Config Async
 * @author Huy.Tho
 * 
 * */
@Configuration
@EnableAsync
public class AsyncConfiguration {
	private static final Logger logger = Logger.getLogger(PlaylistService.class);
	
	@Bean (name = "taskExecutor")
    public Executor taskExecutor() {
		logger.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Thread-");
        executor.initialize();
        return executor;
    }
}
