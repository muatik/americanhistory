package com.muatik.americanhistory;

import com.squareup.otto.Bus;

/**
 * Created by muatik on 30.03.2015.
 */
public class BusProvider {

    private static Bus bus;

    public static Bus get() {
        if (bus == null)
            bus = new Bus();
        return bus;
    }
}
