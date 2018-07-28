package com.lausy.contentretriever;

import android.support.test.espresso.core.internal.deps.guava.collect.Ordering;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.number.OrderingComparison;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <h1>Instrumented Unit Tests</h1>
 *
 * Copyright 2018:  Rick Lau
 *
 * @author Rick Lau
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListAdapterActivityTest {
    @Rule
    public ActivityTestRule<ContentRetrievalActivity> mActivityRule = new ActivityTestRule<>(
            ContentRetrievalActivity.class);

    /**
     * Loads test data to the list view.
     * Sorts in ascending order.
     * Tests for ascending sort order.
     * @throws Exception
     */
    @Test
    public void clickSortAscending() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("load test data")).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("sort ascending")).perform(click());

        onView(withId(R.id.content_listview)).check(matches(isSorted(true)));
    }

    /**
     * Loads test data to the list view.
     * Sorts in descending order.
     * Tests for descending sort order.
     * @throws Exception
     */
    @Test
    public void clickSortDescending() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("load test data")).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("sort descending")).perform(click());
        onView(withId(R.id.content_listview)).check(matches(isSorted(false)));
    }

    /**
     * Negative test case.  Should fail.
     * Loads test data to the list view.
     * Sorts in descending order.
     * Tests for ascending sort order.
     *
     * @throws Exception
     *
     */
    @Test
    public void clickSortDescendingShouldFail() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("load test data")).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("sort descending")).perform(click());
        onView(withId(R.id.content_listview)).check(matches(isSorted(true)));
    }


    @Test
    public void clickFabButton() throws Exception {
        onView(withId(R.id.refresh)).perform(click());
    }

    private static Matcher<View> isSorted(Boolean ascending) {
        return new TypeSafeMatcher<View>() {

            private final List<Integer> my_titles = new ArrayList<>();

            @Override
            protected boolean matchesSafely(View item) {
                ListView my_list = (ListView) item;
                ContentListDataAdapter my_adapter = (ContentListDataAdapter) my_list.getAdapter();

                my_titles.clear();
                my_titles.addAll(extractTeamNames(my_adapter.getAllData()));

                if (ascending)
                    return testAscending();
                else
                    return testDescending();
                //return Ordering.natural().isOrdered(my_titles);
            }

            private Boolean testAscending() {
                Boolean ordered = true;
                for (int i=1 ; i<my_titles.size()-2 ; i++)
                {
                    if (my_titles.get(i-1) > my_titles.get(i)) {
                        ordered = false;
                        break;
                    }
                }
                return ordered;
            }

            private Boolean testDescending() {
                Boolean ordered = true;
                for (int i=1 ; i<my_titles.size()-2 ; i++)
                {
                    if (my_titles.get(i-1) < my_titles.get(i)) {
                        ordered = false;
                        break;
                    }
                }
                return ordered;
            }

            private List<Integer> extractTeamNames(List<ContentListDataAdapter.ViewData> data_list) {
                List<Integer> titles = new ArrayList<>();
                for (ContentListDataAdapter.ViewData data : data_list) {
                    titles.add(data.getId());
                }
                return titles;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("is items sorted ascending: " + my_titles);
            }
        };
    }
}
