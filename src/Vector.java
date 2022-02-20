public class Vector {
    private double mag;
    private double xMag;
    private double yMag;
    private double angle; // The angle of the vector in radians (relative to x+ axis)

    /**
     * Constructs a new {@code Vector} with the magnitude and angle set to 0
     */
    public Vector() {
        this(0, 0);
    }

    /**
     * Constructs a new {@code Vector} with the given magnitude and degrees angle
     * for radians use {@link #Vector(double, double, boolean) Vector(mag, angle,
     * true)}
     * 
     * @param mag   the magnitude of the vector
     * @param angle the angle between the vector and the +x axis (in
     *              degrees)
     */
    public Vector(double mag, double angle) {
        this(mag, angle, false);
    }

    /**
     * Constructs a new {@code Vector} with the given magnitude angle
     * 
     * @param mag       the magnitude of the vector
     * @param angle     The angle of the vector relative to x+ axis
     * @param isRadians if the value is false, uses the given angle in degrees (true
     *                  -> radians)
     */
    public Vector(double mag, double angle, boolean isRadians) {
        if (mag == 0) {
            this.mag = this.angle = xMag = yMag = 0;
            return;
        }
        if (isRadians) {
            setAngleInRadians(angle);
        } else {
            setAngleInDegrees(angle);
        }
        setMag(mag);
    }

    /**
     * Constructs a new {@code Vector} with the properties of a the given vector
     * 
     * @param v vector to copy
     */
    public Vector(Vector v) {
        mag = v.mag;
        xMag = v.xMag;
        yMag = v.yMag;
        angle = v.angle;
    }

    // #region setters
    public void setAngleInDegrees(double angle) {
        setAngleInRadians(Math.toRadians(angle));
    }

    public void setAngleInRadians(double angle) {
        this.angle = angle;
        double[] arr = CalculateXYMag(mag, angle);
        xMag = arr[0];
        yMag = arr[1];
    }

    public void setMag(double newMag) {
        mag = newMag;
        if (mag < 0) {
            setAngleInRadians(getAngleInRadians() + Math.PI);
            mag *= -1;
        }
        double[] arr = CalculateXYMag(mag, angle);
        xMag = arr[0];
        yMag = arr[1];
        if (newMag == 0)
            angle = 0;
    }

    public void setXMag(double newXMag) {
        xMag = newXMag;
        angle = CalculateAgnle(xMag, yMag);
        setMag(CalculateMag(xMag, yMag));
    }

    public void setYMag(double newYMag) {
        yMag = newYMag;
        angle = CalculateAgnle(xMag, yMag);
        setMag(CalculateMag(xMag, yMag));
    }

    // #endregion
    // #region getters
    public double getAngleInDegrees() {
        return Math.toDegrees(angle);
    }

    public double getAngleInRadians() {
        return angle;
    }

    public double getMag() {
        return mag;
    }

    public double getXMag() {
        return xMag;
    }

    public double getYMag() {
        return yMag;
    }
    // #endregion

    /**
     * Multiplys the vector by a scalar value and sets the vector magnitude to the
     * result
     * 
     * @param scalar
     * @return a refrence to this vector
     */
    public Vector multiply(double scalar) {
        setMag(mag * scalar);
        return this;
    }

    /**
     * Divides the vector by a divisor value and sets the vector magnitude to the
     * result
     * 
     * @param divisor
     * @return a refrence to this vector
     */
    public Vector divide(double divisor) {
        setMag(mag / divisor);
        return this;
    }

    /**
     * Adds a vector to this vector and sets this vector values to the result
     * 
     * @param v vector to add
     * @return a refrence to this vector
     */
    public Vector add(Vector v) {
        xMag += v.xMag;
        yMag += v.yMag;
        mag = CalculateMag(xMag, yMag);
        angle = CalculateAgnle(xMag, yMag);
        return this;
    }

    /**
     * Subtracts a vector from this vector and sets this vector values to the result
     * 
     * @param v vector to add
     * @return a refrence to this vector
     */
    public Vector subtract(Vector v) {
        this.add(new Vector(v).multiply(-1));
        return this;
    }

    private static double CalculateAgnle(double xMag, double yMag) {
        double newAngle;
        if (xMag == 0) {
            if (yMag > 0) {
                newAngle = Math.PI / 2;
            } else {
                newAngle = Math.PI * (3 / 2);
            }
            return newAngle;
        }
        newAngle = Math.atan(Math.abs(yMag / xMag));
        if (xMag < 0) {
            if (yMag < 0) {
                newAngle += Math.PI;
            } else {
                newAngle = Math.PI - newAngle;
            }
        } else if (yMag < 0) {
            newAngle = (2 * Math.PI) - newAngle;
        }
        return newAngle;
    }

    private static Double CalculateMag(double xMag, double yMag) {
        return Math.sqrt(Math.pow(xMag, 2) + Math.pow(yMag, 2));
    }

    private static double[] CalculateXYMag(double mag, double angle) {
        return new double[] { mag * Math.cos(angle), mag * Math.sin(angle) };
    }

    /**
     * Adds tow vectors
     * 
     * @param v1 the first vector
     * @param v2 the second vector
     * @return new Vector, equal to (v1 + v2)
     */
    public static Vector Add(Vector v1, Vector v2) {
        return new Vector(v1).add(v2);
    }

    /**
     * Subtracts the second vector(v2) from the first vector(v1)
     * 
     * @param v1 the first vector
     * @param v2 the second vector
     * @return new Vector, equal to (v1 - v2)
     */
    public static Vector Subtract(Vector v1, Vector v2) {
        return new Vector(v1).subtract(v2);
    }

    public static double DotProduct(Vector v1, Vector v2) {
        return Math.abs(v1.mag * v2.mag) * Math.cos(Math.abs(v1.angle - v2.angle));
    }

    /**
     * Create a copy of a vector and Multiplys it by a scalar value
     * 
     * @param v
     * @param scalar
     * @return new Vector, equal to the result
     */
    public static Vector Multiply(Vector v, double scalar) {
        return new Vector(v).multiply(scalar);
    }

    /**
     * Create a copy of a vector and Divides it by a divisor value
     * 
     * @param v
     * @param divisor
     * @return new Vector, equal to the result
     */
    public static Vector Divide(Vector v, double divisor) {
        return new Vector(v).divide(divisor);
    }

    public static Vector CreateVectorFromXY(double x, double y) {
        Vector v = new Vector();
        v.setAngleInRadians(CalculateAgnle(x, y));
        v.setMag(CalculateMag(x, y));
        return v;
    }
}