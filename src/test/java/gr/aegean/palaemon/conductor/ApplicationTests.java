package gr.aegean.palaemon.conductor;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void testSha256(){
		String macAddress ="58:37:8B:DE:42:F7";
		String expected="b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759";
		String result =DigestUtils.sha256Hex(macAddress);
		assertEquals(result, expected);

	}
}
