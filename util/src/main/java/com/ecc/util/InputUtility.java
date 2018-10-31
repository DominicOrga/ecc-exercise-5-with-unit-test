package com.ecc.util;

import java.util.Optional;
import java.util.Scanner;

public class InputUtility {

	public static Optional<String> nextString(String message, String... invalidSubstrings) {
		Scanner scanner = new Scanner(System.in);
		boolean isInputValidated = false;

		System.out.print(message + " ");
		String input = scanner.nextLine();

		if (invalidSubstrings != null) {
			for (int i = 0, s = invalidSubstrings.length; i < s; i++) {
				if (input.contains(invalidSubstrings[i])) {
					System.out.printf("Input contains invalid substring: %s\n", invalidSubstrings[i]);
					return  Optional.empty();
				};
			}
		}

		return Optional.of(input);
	}
}