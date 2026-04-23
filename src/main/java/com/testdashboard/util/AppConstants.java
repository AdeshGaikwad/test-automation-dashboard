package com.testdashboard.util;

public class AppConstants {

    private AppConstants() {}

    // Default failure threshold — alert fires when failure rate crosses this
    public static final double DEFAULT_FAILURE_THRESHOLD = 20.0;

    // Cache names — must match names used in @Cacheable annotations
    public static final String CACHE_SUMMARY = "summary";
    public static final String CACHE_TRENDS  = "trends";

    // Default pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE     = 50;
}