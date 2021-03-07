package Model;

public class Validator {
    public static boolean checkPassword(String password) {

        boolean isValid = true;

        if (!(password.length() >= 8)) {
            System.out.println("Password must have at least 8 characters");
            isValid = false;
        }

        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            System.out.println("Password must have at least one uppercase character");
            isValid = false;
        }

        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            System.out.println("Password must have at least one lowercase character");
            isValid = false;
        }

        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            System.out.println("Password must have at least one number");
            isValid = false;
        }

        String specialChars = "(.*[@,!,$,^,#].*$)";
        if (!password.matches(specialChars)) {
            System.out.println("Password must have atleast one special symbol (@,$,!,^)");
            isValid = false;
        }

        return isValid;
    }

}
