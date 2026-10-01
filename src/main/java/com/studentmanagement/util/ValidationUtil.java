package com.studentmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.regex.Pattern;

/** Stateless input checks. Services use these before any data reaches MySQL. */
public final class ValidationUtil {

    public static final int MIN_YEAR = 1;
    public static final int MAX_YEAR = 4;
    public static final double MIN_MARKS = 0;
    public static final double MAX_MARKS = 100;
    public static final List<String> GENDERS = List.of("Male", "Female", "Other");

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern NAME = Pattern.compile("^\\p{L}[\\p{L} .'-]{1,99}$");
    private static final LocalDate EARLIEST_BIRTH_DATE = LocalDate.of(1900, 1, 1);

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isWithinLength(String value, int maxLength) {
        return value != null && value.length() <= maxLength;
    }

    public static boolean isValidName(String name) {
        return name != null && NAME.matcher(name).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 120 && EMAIL.matcher(email).matches();
    }

    /** A 10-digit Indian mobile number starting with 6, 7, 8 or 9. */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE.matcher(phone).matches();
    }

    public static boolean isValidGender(String gender) {
        return GENDERS.contains(gender);
    }

    public static boolean isValidYear(int year) {
        return year >= MIN_YEAR && year <= MAX_YEAR;
    }

    public static boolean isValidMarks(double marks) {
        return marks >= MIN_MARKS && marks <= MAX_MARKS;
    }

    public static boolean isValidDateOfBirth(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now()) && !date.isBefore(EARLIEST_BIRTH_DATE);
    }

    /** Parses text such as "1001". Empty if the text is not a positive whole number. */
    public static Optional<Integer> parsePositiveInt(String text) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            int value = Integer.parseInt(text.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** Parses a decimal number. Empty for non-numbers, NaN and infinity. */
    public static OptionalDouble parseNumber(String text) {
        if (text == null) {
            return OptionalDouble.empty();
        }
        try {
            double value = Double.parseDouble(text.trim());
            return Double.isFinite(value) ? OptionalDouble.of(value) : OptionalDouble.empty();
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    /** Parses a date in ISO format yyyy-MM-dd. */
    public static Optional<LocalDate> parseDate(String text) {
        if (text == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(text.trim()));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
