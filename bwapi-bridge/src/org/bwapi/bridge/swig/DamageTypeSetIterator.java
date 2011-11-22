/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.bwapi.bridge.swig;
import org.bwapi.bridge.model.BwapiPointable;
public class DamageTypeSetIterator implements BwapiPointable {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public DamageTypeSetIterator(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(DamageTypeSetIterator obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public long getCPtr() {
    return swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        bridgeJNI.delete_DamageTypeSetIterator(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public DamageTypeSetIterator(SWIGTYPE_p_std__setT_BWAPI__DamageType_t original) {
    this(bridgeJNI.new_DamageTypeSetIterator(SWIGTYPE_p_std__setT_BWAPI__DamageType_t.getCPtr(original)), true);
  }

  public boolean hasNext() {
    return bridgeJNI.DamageTypeSetIterator_hasNext(swigCPtr, this);
  }

  public SWIG_DamageType next() {
    return new SWIG_DamageType(bridgeJNI.DamageTypeSetIterator_next(swigCPtr, this), true);
  }

}
