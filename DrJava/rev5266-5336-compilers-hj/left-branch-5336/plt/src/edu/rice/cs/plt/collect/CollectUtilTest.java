

package edu.rice.cs.plt.collect;

import junit.framework.TestCase;
import java.util.Arrays;
import java.util.List;

import static edu.rice.cs.plt.collect.CollectUtil.*;


public class CollectUtilTest extends TestCase {

  public void assertList(List<?> actual, Object... expected) {
    assertEquals(Arrays.asList(expected), actual);
  }
  
  private List<String> list(String... ss) {
    return Arrays.asList(ss);
  }
  
  public void testMaxList() {
    assertList(maxList(list(), STRING_PREFIX_ORDER));
    assertList(maxList(list("b"), STRING_PREFIX_ORDER), "b");
    assertList(maxList(list("a", "b", "ab"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(maxList(list("ab", "b", "a"), STRING_PREFIX_ORDER), "ab", "b");
    assertList(maxList(list("abc", "ab", "a"), STRING_PREFIX_ORDER), "abc");
    assertList(maxList(list("a", "ab", "abc"), STRING_PREFIX_ORDER), "abc");
    assertList(maxList(list("a", "ab", "a", "b", "ab"), STRING_PREFIX_ORDER), "ab", "b");
  }
  
  public void testComposeMaxLists() {
    assertList(composeMaxLists(list(), list(), STRING_PREFIX_ORDER));
    assertList(composeMaxLists(list(), list("a", "b", "c"), STRING_PREFIX_ORDER), "a", "b", "c");
    assertList(composeMaxLists(list("a", "b", "c"), list(), STRING_PREFIX_ORDER), "a", "b", "c");
    assertList(composeMaxLists(list("b", "ab"), list("b"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMaxLists(list("ab", "b"), list("b"), STRING_PREFIX_ORDER), "ab", "b");
    assertList(composeMaxLists(list("b"), list("b", "ab"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMaxLists(list("b"), list("ab", "b"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMaxLists(list("ab", "cd"), list("ef", "c"), STRING_PREFIX_ORDER), "ab", "cd", "ef");
    assertList(composeMaxLists(list("ef", "c"), list("ab", "cd"), STRING_PREFIX_ORDER), "ef", "ab", "cd");
  }
  
  public void testMinList() {
    assertList(minList(list(), STRING_PREFIX_ORDER));
    assertList(minList(list("b"), STRING_PREFIX_ORDER), "b");
    assertList(minList(list("a", "b", "ab"), STRING_PREFIX_ORDER), "a", "b");
    assertList(minList(list("ab", "b", "a"), STRING_PREFIX_ORDER), "b", "a");
    assertList(minList(list("abc", "ab", "a"), STRING_PREFIX_ORDER), "a");
    assertList(minList(list("a", "ab", "abc"), STRING_PREFIX_ORDER), "a");
    assertList(minList(list("a", "ab", "a", "b", "ab"), STRING_PREFIX_ORDER), "a", "b");
  }
  
  public void testComposeMinLists() {
    assertList(composeMinLists(list(), list(), STRING_PREFIX_ORDER));
    assertList(composeMinLists(list(), list("a", "b", "c"), STRING_PREFIX_ORDER), "a", "b", "c");
    assertList(composeMinLists(list("a", "b", "c"), list(), STRING_PREFIX_ORDER), "a", "b", "c");
    assertList(composeMinLists(list("b", "ab"), list("b"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMinLists(list("ab", "b"), list("b"), STRING_PREFIX_ORDER), "ab", "b");
    assertList(composeMinLists(list("b"), list("b", "ab"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMinLists(list("b"), list("ab", "b"), STRING_PREFIX_ORDER), "b", "ab");
    assertList(composeMinLists(list("ab", "cd"), list("ef", "c"), STRING_PREFIX_ORDER), "ab", "ef", "c");
    assertList(composeMinLists(list("ef", "c"), list("ab", "cd"), STRING_PREFIX_ORDER), "ef", "c", "ab");
  }
  
  public void testPrefixStringOrder() {
    assertTrue(STRING_PREFIX_ORDER.contains("a", "abc"));
    assertTrue(STRING_PREFIX_ORDER.contains("ab", "abc"));
    assertTrue(STRING_PREFIX_ORDER.contains("abc", "abc"));
    assertTrue(STRING_PREFIX_ORDER.contains("", "abc"));
    assertFalse(STRING_PREFIX_ORDER.contains("abcd", "abc"));
    assertFalse(STRING_PREFIX_ORDER.contains("bc", "abc"));
    assertFalse(STRING_PREFIX_ORDER.contains("a", "b"));
  }
  
  public void testSubstringOrder() {
    assertTrue(SUBSTRING_ORDER.contains("a", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("b", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("c", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("ab", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("abc", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("bc", "abc"));
    assertTrue(SUBSTRING_ORDER.contains("", "abc"));
    assertFalse(SUBSTRING_ORDER.contains("abcd", "abc"));
    assertFalse(SUBSTRING_ORDER.contains("ac", "abc"));
  }
  
  public void testSubsetOrder() {
    assertTrue(SUBSET_ORDER.contains(Arrays.<Integer>asList(), Arrays.<Integer>asList()));
    assertTrue(SUBSET_ORDER.contains(Arrays.<Integer>asList(), Arrays.asList(1, 2)));
    assertTrue(SUBSET_ORDER.contains(Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 1)));
    assertTrue(SUBSET_ORDER.contains(Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 1)));
    assertFalse(SUBSET_ORDER.contains(Arrays.asList(1, 2, 3), Arrays.asList(2, 3)));
  }
  
}
