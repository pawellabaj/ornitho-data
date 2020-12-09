package pl.com.labaj.ornitho.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DateRange {
    LocalDate from;
    LocalDate to;
}
