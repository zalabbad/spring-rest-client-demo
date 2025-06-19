package com.example.restclientdemo.feignClient;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor that propagates headers from the current request to the Feign
 * client request. This ensures that headers like transaction IDs and
 * authentication tokens are passed along.
 */
@Slf4j
public class FeignHeaderPropagationInterceptor implements RequestInterceptor {

	private static final String[] HEADERS_TO_PROPAGATE = {"Authorization", "X-Transaction-ID",
														"X-Custom-Header"};

	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
			.getRequestAttributes();
		if (requestAttributes == null) {
			log.warn("FeignClient - No request attributes available, cannot propagate headers");
			return;
		}

		HttpServletRequest request = requestAttributes.getRequest();
		log.debug("FeignClient - Propagating headers from current request to Feign client request");

		// Propagate specific headers
		for (String header : HEADERS_TO_PROPAGATE) {
			String value = request.getHeader(header);
			if (value != null) {
				template.header(header, value);
				log.debug("FeignClient - Propagated header {}: {}", header, value);
			}
		}

		// // Add MDC values as headers
		// String txId = MDC.get("tx_id");
		// if (txId != null) {
		// template.header("X-Transaction-ID", txId);
		// log.debug("FeignClient - Added transaction ID from MDC: {}", txId);
		// }
		//
		// String userId = MDC.get("userId");
		// if (userId != null) {
		// template.header("X-User-ID", userId);
		// log.debug("FeignClient - Added user ID from MDC: {}", userId);
		// }
	}
}
