package com.github.sisyphsu.dateparser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Complete
 *
 * @author sulin
 * @since 2019-09-15 11:19:31
 */
public final class DateParserUtils {

    private static DateParser dateParser = new DateParser();

    /**
     * Parse the specified String into Date instance, it will convert different TimeZone into system's default zone.
     *
     * @param str Datetime string like '2019-10-01 00:10:20 +0800'
     * @return Parsed datetime as Date
     */
    public static synchronized Date parseDate(String str) {
        return dateParser.parseDate(str);
    }

    /**
     * Parse the specified String into Calendar instance, it will convert different TimeZone into system's default zone.
     *
     * @param str Datetime string like '2019-10-01 00:10:20 +0800'
     * @return Parsed datetime as Calendar
     */
    public static synchronized Calendar parseCalendar(String str) {
        return dateParser.parseCalendar(str);
    }

    /**
     * Parse the specified String into LocalDateTime, it will convert different TimeZone into system's default zone.
     *
     * @param str Datetime string like '2019-10-01 +08:00'
     * @return Parsed datetime as LocalDateTime
     */
    public static synchronized LocalDateTime parseDateTime(String str) {
        return dateParser.parseDateTime(str);
    }

    /**
     * Parse the specified String into OffsetDateTime, use +00:00 as default ZoneOffset.
     *
     * @param str Datetime string like '2019-10-01'
     * @return Parsed datetime as OffsetDateTime
     */
    public static synchronized OffsetDateTime parseOffsetDateTime(String str) {
        return dateParser.parseOffsetDateTime(str);
    }

    /**
     * Register new standard parse rules, all captured group should have the specified names.
     *
     * @param re The regex of rule
     */
    public static synchronized void registerStandardRule(String re) {
        Rules.register(re);
    }

    /**
     * Register new customized parse rules.
     *
     * @param re      The regex of rule, like '\d{8}'
     * @param handler The handler for this rule
     */
    public static synchronized void registerCustomizedRule(String re, RuleHandler handler) {
        Rules.register(re, handler);
    }

}