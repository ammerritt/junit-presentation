package com.merritt.commons.testing.junit.selenium.matcher;

import org.apache.commons.lang3.Range;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class IsWithinRange<T> extends BaseMatcher<T> {
    private final Range<Integer> xRange;
    private final Range<Integer> yRange;
    private boolean unsupportedType = false;
    private final String unsupportedClassName = "";

    public IsWithinRange(final Range<Integer> xRange, final Range<Integer> yRange) {
        this.xRange = xRange;
        this.yRange = yRange;
    }

    @Override
    public boolean matches(final Object item) {
        int x = -99999;
        int y = -99999;

        if (item instanceof Point) {
            x = ((Point)item).getX();
            y = ((Point)item).getY();
        } else if (item instanceof Dimension) {
            x = ((Dimension)item).getWidth();
            y = ((Dimension)item).getHeight();
        } else {
            unsupportedType = true;
        }

        return !unsupportedType && xRange.contains(x) && yRange.contains(y);
    }

    //    public boolean matchesSafely( final T item )
    //    {
    //        final IntRange xRange = new IntRange( upperLeft.getX(), lowerRight.getX() );
    //        final IntRange yRange = new IntRange( upperLeft.getY(), lowerRight.getY() );
    //        return xRange.containsInteger( item.getX() ) && yRange.containsInteger( item.getY() );
    //    }

    public void describeTo(final Description description) {
        if (unsupportedType) {
            description.appendText("Class: '" + unsupportedClassName + "' is not supported by RectangularRange.").appendText("Currently only org.openqa.selenium.Point and org.openqa.selenium.Dimension are supported.");
        } else {
            description.appendText("item was not between ").appendValue(xRange).appendText(" and ").appendValue(yRange);
        }
    }

    /**
     * @param xRange - the range containing the minimum and maximum values for the x/width 
     * @param yRange - the range containing the minimum and maximum values for the y/height 
     * @return
     */
    @Factory
    public static <T> Matcher<T> withinRange(final Range<Integer> xRange, final Range<Integer> yRange) {
        return new IsWithinRange<T>(xRange, yRange);
    }
}