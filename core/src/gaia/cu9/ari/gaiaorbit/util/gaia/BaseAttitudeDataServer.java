/*
 * GaiaTools
 * Copyright (C) 2006 Gaia Data Processing and Analysis Consortium
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package gaia.cu9.ari.gaiaorbit.util.gaia;

import gaia.cu9.ari.gaiaorbit.util.coord.AstroUtils;
import gaia.cu9.ari.gaiaorbit.util.gaia.time.TimeContext;

import java.util.Date;
import java.util.Stack;

/**
 * Common base class for all attitude data servers. This holds all common fields
 * e.g. the time origin for relative time scales (in ns) and implements
 * {@link #getAttitude(long)} in terms of {@link #getAttitude(long)} which
 * is the same for all servers.
 *
 * The time context and its possible switch is implemented in a thread-safe manner.
 * Derived classes should hence be likewise thread-safe.
 *
 * @author Uwe Lammers
 * @version $Id: BaseAttitudeDataServer.java 254926 2012-10-01 15:10:38Z
 *          ulammers $
 * @param <A>
 *            type of Attitude that the server is serving
 */
public abstract class BaseAttitudeDataServer<A extends Attitude> {

    /**
     * Some scanning laws have constants or tables for interpolation that need
     * to be computed before the first use and recomputed after changing certain
     * reference values. This flag indicates that the constants or tables
     * (whatever applicable) are up to date.
     */
    protected boolean initialized = false;

    private long refEpoch = -1;

    /**
     * native and initially requested time context of the server - has to be set by the implementing class
     */
    protected TimeContext nativeTimeContext = null;
    protected TimeContext initialRequestedTimeContext = null;

    protected ThreadLocal<TimeContext> requestedTimeContext = new ThreadLocal<TimeContext>() {
        @Override
        protected TimeContext initialValue() {
            return initialRequestedTimeContext;
        }
    };

    /**
     * switch to decide if attitude uncertainties and correlations should be calculated
     */
    protected boolean withUncertaintiesCorrelations = true;

    /**
     * @return Returns the initialised.
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }


    public A getAttitude(Date date) {
        long tNs = (long) ((AstroUtils.getJulianDateCache(date) - AstroUtils.JD_J2010) * AstroUtils.DAY_TO_NS);
        return getAttitudeNative(tNs);
    }

    /**
     */
    public synchronized A getAttitude(long time) {
        return getAttitudeNative(time);
    }

    /**
     * Evaluate the attitude in the native time system of the server
     */
    abstract protected A getAttitudeNative(long time);

    public void setRefTime(long t) {
        this.refEpoch = t;
    }

    public long getRefTime() {
        return refEpoch;
    }

}
