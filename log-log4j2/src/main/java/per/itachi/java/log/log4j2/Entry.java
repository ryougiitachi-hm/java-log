package per.itachi.java.log.log4j2;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Entry {
	
	private static final Logger logger = LoggerFactory.getLogger(Entry.class);
	
	private static final String MDC_TRACE_ID = "trace-id"; 

	public static void main(String[] args) {
		logger.info("log4j2");
		testMDC();
	}
	
	private static void testMDC() {
		MDC.put(MDC_TRACE_ID, UUID.randomUUID().toString());
		logger.info("show trace-id");
		MDC.remove(MDC_TRACE_ID);
		logger.info("after clearing context");
	}

}
