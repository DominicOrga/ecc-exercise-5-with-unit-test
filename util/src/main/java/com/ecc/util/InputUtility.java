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
					return Optional.empty();
				};
			}
		}

		return Optional.of(input);
	}

	public static Optional<Integer> nextInt(String message) {
		return nextInt(message, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static Optional<Integer> nextInt(String message, int min, int max) {
		Scanner scanner = new Scanner(System.in);
		boolean isInputValidated = false;
		int intInput = 0;

		System.out.print(message + " ");
		String input = scanner.nextLine();
		
		if (input.matches("-?\\d+")) {
			intInput = Integer.parseInt(input);

			if (intInput >= min && intInput <= max) {
				return Optional.of(intInput);
			}
		}	

		return Optional.empty();
	}	
}