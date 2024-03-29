/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.40
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.bwapi.bridge.swig;
import org.bwapi.bridge.model.BwapiPointable;
public class SWIG_Region implements BwapiPointable {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public SWIG_Region(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(SWIG_Region obj) {
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
        bridgeJNI.delete_SWIG_Region(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SWIG_Polygon getPolygon() {
    return new SWIG_Polygon(bridgeJNI.SWIG_Region_getPolygon(swigCPtr, this), false);
  }

  public SWIG_Position getCenter() {
    return new SWIG_Position(bridgeJNI.SWIG_Region_getCenter(swigCPtr, this), false);
  }

  public SWIGTYPE_p_std__setT_BWTA__Chokepoint_p_t getChokepoints() {
    return new SWIGTYPE_p_std__setT_BWTA__Chokepoint_p_t(bridgeJNI.SWIG_Region_getChokepoints(swigCPtr, this), false);
  }

  public SWIGTYPE_p_std__setT_BWTA__BaseLocation_p_t getBaseLocations() {
    return new SWIGTYPE_p_std__setT_BWTA__BaseLocation_p_t(bridgeJNI.SWIG_Region_getBaseLocations(swigCPtr, this), false);
  }

}
