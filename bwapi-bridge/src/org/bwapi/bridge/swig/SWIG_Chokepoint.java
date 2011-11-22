/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.bwapi.bridge.swig;
import org.bwapi.bridge.model.BwapiPointable;
public class SWIG_Chokepoint implements BwapiPointable {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public SWIG_Chokepoint(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(SWIG_Chokepoint obj) {
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
        bridgeJNI.delete_SWIG_Chokepoint(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ChokepointGetRegionsPair getRegions() {
    return new ChokepointGetRegionsPair(bridgeJNI.SWIG_Chokepoint_getRegions(swigCPtr, this), false);
  }

  public ChokepointGetSidesPair getSides() {
    return new ChokepointGetSidesPair(bridgeJNI.SWIG_Chokepoint_getSides(swigCPtr, this), false);
  }

  public SWIG_Position getCenter() {
    return new SWIG_Position(bridgeJNI.SWIG_Chokepoint_getCenter(swigCPtr, this), true);
  }

  public double getWidth() {
    return bridgeJNI.SWIG_Chokepoint_getWidth(swigCPtr, this);
  }

}
