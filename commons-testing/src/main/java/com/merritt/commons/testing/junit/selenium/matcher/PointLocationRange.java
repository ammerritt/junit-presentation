package com.merritt.commons.testing.junit.selenium.matcher;

import org.apache.commons.lang3.Range;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.openqa.selenium.Point;

public class PointLocationRange extends TypeSafeMatcher<Point> {
    private final Point upperLeft;
    private final Point lowerRight;

    public PointLocationRange(final Point upperLeft, final Point lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    @Override
    public boolean matchesSafely(final Point item) {
        final Range<Integer> xRange = Range.between(upperLeft.getX(), lowerRight.getX());
        final Range<Integer> yRange = Range.between(upperLeft.getY(), lowerRight.getY());
        return xRange.contains(item.getX()) && yRange.contains(item.getY());
    }

    public void describeTo(final Description description) {
        description.appendText("item's location was not between ").appendValue(upperLeft).appendText(" and ").appendValue(lowerRight);
    }

    @Factory
    public static Matcher<Point> withinRange(final Point upperLeft, final Point lowerRight) {
        return new PointLocationRange(upperLeft, lowerRight);
    }
}