/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.bwapi.bridge.swig;
import org.bwapi.bridge.model.BwapiPointable;
public class SWIG_Force implements BwapiPointable {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public SWIG_Force(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(SWIG_Force obj) {
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
        bridgeJNI.delete_SWIG_Force(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public String getName() {
    return bridgeJNI.SWIG_Force_getName(swigCPtr, this);
  }

  public SWIGTYPE_p_std__setT_BWAPI__Player_p_t getPlayers() {
    return new SWIGTYPE_p_std__setT_BWAPI__Player_p_t(bridgeJNI.SWIG_Force_getPlayers(swigCPtr, this), true);
  }

}
