package com.ecc.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.After;

import java.util.Optional;
import java.io.ByteArrayInputStream;

public class InputUtilityTest {

	@Test
	public void givenAValidStringWhenRequestStringThenReturnString() {
		ByteArrayInputStream in = new ByteArrayInputStream("My Input String".getBytes());
		System.setIn(in);

		Optional<String> str = InputUtility.nextString("message");
	
		assertThat(str.get()).isEqualTo("My Input String");
	}

	@Test
	@Ignore
	public void givenAnInvalidSubstringWhenRequestStringThenReturnNothingAndDisplayError() {
	}

	@After
	public void resetSystemIn() {
		System.setIn(System.in);
	}
}