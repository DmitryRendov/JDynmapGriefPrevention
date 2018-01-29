package jahan.khan.jdynmapgriefprevention;


public class StringSearch
{
  private final int BASE;
  
  private int[] occurrence;
  private String pattern;
  
  public StringSearch(String pattern)
  {
    this.BASE = 256;
    this.pattern = pattern;
    
    this.occurrence = new int[this.BASE];
    for (int c = 0; c < this.BASE; c++)
      this.occurrence[c] = -1;
    for (int j = 0; j < pattern.length(); j++)
      this.occurrence[pattern.charAt(j)] = j;
  }
  
  public int search(String text) {
    int n = text.length();
    int m = this.pattern.length();
    int skip;
    for (int i = 0; i <= n - m; i += skip) {
      skip = 0;
      for (int j = m - 1; j >= 0; j--) {
        if (this.pattern.charAt(j) != text.charAt(i + j)) {
          skip = Math.max(1, j - this.occurrence[text.charAt(i + j)]);
          break;
        }
      }
      if (skip == 0) return i;
    }
    return -1;
  }
}
