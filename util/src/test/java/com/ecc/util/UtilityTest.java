package com.ecc.util;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class UtilityTest {
	
	@Test
	public void whenResourceRequestedExistsThenReturnResourcePath() {
		Optional<String> path = Utility.getResourcePath("dummy.txt");
		assertThat(path.isPresent()).isTrue();
	}

	@Test
	public void whenResourceRequestedDoesNotExistThenReturnNothing() {
		Optional<String> path = Utility.getResourcePath("non-existing-file.txt");
		assertThat(path.isPresent()).isFalse();
	}
}