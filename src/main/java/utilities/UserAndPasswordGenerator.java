package utilities;

import org.apache.commons.lang.RandomStringUtils;

public class UserAndPasswordGenerator {

	public static void main(String[] args) {
		String generatedText = "";
		for (int i = 0; i < 2000; i++) {
			String userPass = RandomStringUtils.randomAlphanumeric(10);
			generatedText = generatedText + userPass + "\n";
		}
		System.out.println(generatedText);
	}

}
