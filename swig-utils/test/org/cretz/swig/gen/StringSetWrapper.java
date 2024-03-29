/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.cretz.swig.gen;

public class StringSetWrapper {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected StringSetWrapper(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(StringSetWrapper obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        swigutilsJNI.delete_StringSetWrapper(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public StringSetWrapper(SWIGTYPE_p_std__setT_std__string_t original) {
    this(swigutilsJNI.new_StringSetWrapper(SWIGTYPE_p_std__setT_std__string_t.getCPtr(original)), true);
  }

  public int size() {
    return swigutilsJNI.StringSetWrapper_size(swigCPtr, this);
  }

  public boolean contains(String item) {
    return swigutilsJNI.StringSetWrapper_contains(swigCPtr, this, item);
  }

  public boolean add(String item) {
    return swigutilsJNI.StringSetWrapper_add(swigCPtr, this, item);
  }

  public void clear() {
    swigutilsJNI.StringSetWrapper_clear(swigCPtr, this);
  }

  public boolean remove(String item) {
    return swigutilsJNI.StringSetWrapper_remove(swigCPtr, this, item);
  }

}
