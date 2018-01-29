package jahan.khan.jdynmapgriefprevention;











class VersionTokenizer
{
  private final String _versionString;
  









  private final int _length;
  









  private int _position;
  









  private int _number;
  









  private String _suffix;
  








  private boolean _hasValue;
  









  public int getNumber()
  {
    return this._number;
  }
  
  public String getSuffix() { return this._suffix; }
  

  public boolean hasValue() { return this._hasValue; }
  
  public VersionTokenizer(String versionString) {
    if (versionString == null)
      throw new IllegalArgumentException("versionString is null");
    this._versionString = versionString;
    this._length = versionString.length();
  }
  
  public boolean MoveNext() { this._number = 0;
    this._suffix = "";
    this._hasValue = false;
    
    if (this._position >= this._length)
      return false;
    this._hasValue = true;
    while (this._position < this._length) {
      char c = this._versionString.charAt(this._position);
      if ((c < '0') || (c > '9')) break;
      this._number = (this._number * 10 + (c - '0'));
      this._position += 1;
    }
    int suffixStart = this._position;
    while (this._position < this._length) {
      char c = this._versionString.charAt(this._position);
      if (c == '.') break;
      this._position += 1;
    }
    this._suffix = this._versionString.substring(suffixStart, this._position);
    if (this._position < this._length) this._position += 1;
    return true;
  }
}
