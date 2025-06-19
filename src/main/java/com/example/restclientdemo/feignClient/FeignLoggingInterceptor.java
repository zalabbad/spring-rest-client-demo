package com.example.restclientdemo.feignClient;

import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor that logs Feign client requests. This provides visibility into
 * the outgoing requests made by the Feign client.
 *
 * <p>
 * This bean is to custom logging the requests/responses made by the Feign
 * client, and can be replaced with a property configuration to have the default
 * logging behavior.
 */
@Slf4j
public class FeignLoggingInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		String method = template.method();
		String url = template.url();
		String txId = MDC.get("tx_id");
		String userId = MDC.get("userId");

		log.info(
			"=========================== FeignClient Request Begin" + " ===========================");
		log.info("FeignClient - Sending {} request to {}, tx_id: {}, userId: {}", method, url, txId,
			userId);

		if (log.isDebugEnabled()) {
			log.debug("FeignClient - Request headers: {}", template.headers());
			if (template.body() != null) {
				log.debug("FeignClient - Request body: {}", new String(template.body(),
					StandardCharsets.UTF_8));
			}
		}
		log.info("=========================== FeignClient Request End ===========================");
	}
}
