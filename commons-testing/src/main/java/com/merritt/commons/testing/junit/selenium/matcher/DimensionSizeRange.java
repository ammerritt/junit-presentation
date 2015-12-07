package com.merritt.commons.testing.junit.selenium.matcher;

import org.apache.commons.lang3.Range;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.openqa.selenium.Dimension;

public class DimensionSizeRange extends TypeSafeMatcher<Dimension> {
    private final Dimension upperLeft;
    private final Dimension lowerRight;

    public DimensionSizeRange(final Dimension upperLeft, final Dimension lowerRight) {
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    @Override
    public boolean matchesSafely(final Dimension item) {
        final Range<Integer> xRange = Range.between(upperLeft.getWidth(), lowerRight.getWidth());
        final Range<Integer> yRange = Range.between(upperLeft.getHeight(), lowerRight.getHeight());
        return xRange.contains(item.getWidth()) && yRange.contains(item.getHeight());
    }

    public void describeTo(final Description description) {
        description.appendText("item's size was not between ").appendValue(upperLeft).appendText(" and ").appendValue(lowerRight);
    }

    @Factory
    public static Matcher<Dimension> withinRange(final Dimension upperLeft, final Dimension lowerRight) {
        return new DimensionSizeRange(upperLeft, lowerRight);
    }
}