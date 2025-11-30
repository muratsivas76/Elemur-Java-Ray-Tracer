package net.elena.murat.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an interval where a ray is inside a shape.
 * Contains both entry (in) and exit (out) intersection information.
 * Used for CSG (Constructive Solid Geometry) operations.
 */
public class IntersectionInterval {
  public final double tIn, tOut;
  public final Intersection in, out;
  
  /**
   * Constructs an interval with entry and exit data.
   * @param tIn The parameter t where the ray enters the shape.
   * @param tOut The parameter t where the ray exits the shape.
   * @param in The Intersection at the entry point.
   * @param out The Intersection at the exit point.
   */
  public IntersectionInterval(double tIn, double tOut, Intersection in, Intersection out) {
    this.tIn = tIn;
    this.tOut = tOut;
    this.in = in;
    this.out = out;
  }
  
  /**
   * Alternative constructor using Intersection objects directly.
   * @param in The entry intersection.
   * @param out The exit intersection.
   */
  public IntersectionInterval(Intersection in, Intersection out) {
    this(in.getT(), out.getT(), in, out);
  }
  
  /**
   * Creates a degenerate interval for non-solid shapes (e.g., planes).
   * @param t The intersection parameter.
   * @param hit The intersection data (used for both in and out).
   * @return A new IntersectionInterval with tIn = tOut.
   */
  public static IntersectionInterval point(double t, Intersection hit) {
    return new IntersectionInterval(t, t, hit, hit);
  }
  
  /**
   * Returns the entry intersection.
   * @return The Intersection at the entry point.
   */
  public Intersection getEntry() {
    return in;
  }
  
  /**
   * Returns the exit intersection.
   * @return The Intersection at the exit point.
   */
  public Intersection getExit() {
    return out;
  }
  
  /**
   * Returns the t values (tIn, tOut) in sorted order (ascending).
   * Useful for CSG boundary analysis.
   * @return Unmodifiable list of doubles in ascending order.
   */
  public List<Double> getTSorted() {
    if (tIn <= tOut) {
      return Arrays.asList(tIn, tOut);
      } else {
      return Arrays.asList(tOut, tIn);
    }
  }
  
  /**
   * Checks if this interval is degenerate (single point).
   * @return true if tIn equals tOut.
   */
  public boolean isPoint() {
    return Math.abs(tIn - tOut) < 1e-10;
  }
  
  /**
   * Checks if this interval is valid (tIn lesser igual than tOut).
   * @return true if the interval is valid.
   */
  public boolean isValid() {
    return tIn <= tOut;
  }
  
  @Override
  public String toString() {
    return String.format("IntersectionInterval(tIn=%.4f, tOut=%.4f, shape=%s)",
      tIn, tOut, in != null && in.getShape() != null ?
    in.getShape().getClass().getSimpleName() : "null");
  }
  
}
