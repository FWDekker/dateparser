package com.github.sisyphsu.dateparser;

import lombok.Data;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This DateTime used for caching the properties of parser.
 *
 * @author sulin
 * @since 2019-09-12 14:58:15
 */
@Data
public final class DateBuilder {

    private static final ZoneOffset SYSTEM_ZONE_OFFSET = OffsetDateTime.now().getOffset();

    int week;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int ns;
    long unixsecond;

    boolean zoneOffsetSetted;
    int zoneOffset;
    TimeZone zone;

    boolean am;
    boolean pm;

    /**
     * Reset this instance, clear all fields to be default value.
     */
    void reset() {
        this.week = 1;
        this.year = 0;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.ns = 0;
        this.unixsecond = 0;
        this.am = false;
        this.pm = false;
        this.zoneOffsetSetted = false;
        this.zoneOffset = 0;
        this.zone = null;
    }

    /**
     * Convert this instance into Date
     */
    Date toDate() {
        if (!zoneOffsetSetted) {
            return toCalendar().getTime();
        }
        long second = toLocalDateTime().toEpochSecond(SYSTEM_ZONE_OFFSET);
        long ns = toLocalDateTime().getNano();
        return new Date(second * 1000 + ns / 1000000);
    }

    /**
     * Convert this instance into Calendar
     */
    Calendar toCalendar() {
        this.prepare();
        Calendar calendar = Calendar.getInstance();
        if (unixsecond != 0) {
            calendar.setTimeInMillis(unixsecond * 1000 + ns / 1000000);
            return calendar;
        }
        if (zone != null) {
            calendar.setTimeZone(zone);
        }
        if (zoneOffsetSetted) {
            String[] ids = TimeZone.getAvailableIDs(zoneOffset * 60000);
            if (ids.length == 0) {
                throw new DateTimeException("Can't build Calendar, because the zoneOffset[" + zoneOffset
                        + "] can't be converted to an valid TimeZone.");
            }
            calendar.setTimeZone(TimeZone.getTimeZone(ids[0]));
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, ns / 1000000);
        return calendar;
    }

    /**
     * Convert this instance into LocalDateTime
     */
    LocalDateTime toLocalDateTime() {
        LocalDateTime dateTime = this.buildDateTime();
        // with ZoneOffset
        if (zoneOffsetSetted) {
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(zoneOffset / 60, zoneOffset % 60);
            return dateTime.atOffset(offset).toLocalDateTime();
        }
        // with TimeZone
        if (zone != null) {
            return dateTime.atZone(zone.toZoneId()).toLocalDateTime();
        }
        return dateTime;
    }

    /**
     * Convert this instance into OffsetDateTime
     */
    OffsetDateTime toOffsetDateTime() {
        LocalDateTime dateTime = this.buildDateTime();
        // with ZoneOffset
        if (zoneOffsetSetted) {
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(zoneOffset / 60, zoneOffset % 60);
            return dateTime.atOffset(offset);
        }
        // with TimeZone
        if (zone != null) {
            return dateTime.atZone(zone.toZoneId()).toOffsetDateTime();
        }
        // with default
        return dateTime.atZone(ZoneOffset.ofHoursMinutes(0, 0)).toOffsetDateTime();
    }

    /**
     * Build a LocaDateTime instance, didn't handle TimeZone.
     */
    private LocalDateTime buildDateTime() {
        this.prepare();
        if (unixsecond > 0) {
            return LocalDateTime.ofEpochSecond(unixsecond, ns, ZoneOffset.UTC);
        }
        return LocalDateTime.of(year, month, day, hour, minute, second, ns);
    }

    /**
     * Prepare this builder
     */
    private void prepare() {
        if (am && hour == 12) {
            this.hour = 0;
        }
        if (pm && hour != 12) {
            this.hour += 12;
        }
    }

}