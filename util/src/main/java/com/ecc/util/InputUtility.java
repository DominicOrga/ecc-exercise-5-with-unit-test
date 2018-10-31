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

		System.out.print(message + " ");
		String input = scanner.nextLine();
		
		if (input.matches("-?\\d+")) {
			int intInput = Integer.parseInt(input);

			if (intInput >= min && intInput <= max) {
				return Optional.of(intInput);
			}
		}	

		return Optional.empty();
	}	

	public static Optional<Boolean> nextBool(String message, String trueSymbol, String falseSymbol) {
		Scanner scanner = new Scanner(System.in);

		System.out.print(message + " ");
		String input = scanner.nextLine();

		if (input.equals(trueSymbol)) {
			return Optional.of(true);
		}		
		else if (input.equals(falseSymbol)) {
			return Optional.of(false);
		}
		else {
			return Optional.empty();
		}
	}
}