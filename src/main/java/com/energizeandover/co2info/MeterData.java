/*
 * Copyright 2020 Energize Andover
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.energizeandover.co2info;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents one CO2 meter's readings.
 */
public class MeterData extends ArrayList<Reading> implements Serializable {
    private String meterName;
    private double readingSum;
    private double readingAverage;

    public MeterData() {
        super();
        readingAverage = 0;
    }

    public MeterData(String meterName) {
        super();
        this.meterName = meterName;
        readingAverage = 0;
    }

    private void updateData(double value) {
        readingSum += value;
        readingAverage = readingSum / size();
    }

    /**
     * Appends the specified reading to the end of this list.
     *
     * @param reading reading to be appended to this list
     * @return <tt>true</tt>
     */
    @Override
    public boolean add(Reading reading) {
        boolean result = super.add(reading);
        updateData(reading.getPpm());

        return result;
    }

    /**
     * Inserts the specified reading at the specified position in this
     * list. Shifts the reading currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified reading is to be inserted
     * @param reading reading to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, Reading reading) {
        super.add(index, reading);

        updateData(reading.getPpm());
    }

    /**
     * Replaces the reading at the specified position in this list with
     * the specified reading.
     *
     * @param index   index of the reading to replace
     * @param reading reading to be stored at the specified position
     * @return the reading previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public Reading set(int index, Reading reading) {
        updateData(-1 * get(index).getPpm());
        Reading result = super.set(index, reading);

        updateData(reading.getPpm());
        return result;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public Reading remove(int index) {
        Reading result = super.remove(index);

        updateData(-1 * get(index).getPpm());
        return result;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        updateData(-1 * get(indexOf(o)).getPpm());

        return result;
    }

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public double findAveragePpm() {
        double sum = 0;
        int size = 0;
        for (Reading reading : this) {
            if (reading.getPpm() != 0) {
                sum += reading.getPpm();
                size++;
            }
        }
        return sum / size;
    }

    public int findReadingCount(double min, double max) {
        int count = 0;
        for (Reading reading : this) {
            if (reading.getPpm() >= min && reading.getPpm() < max) {
                count++;
            }
        }
        return count;
    }

    public int findReadingCount(int equals) {
        int count = 0;
        for (Reading reading : this) {
            if (reading.getPpm() == equals) {
                count++;
            }
        }
        return count;
    }

    public String toStringUnhealthyReadings() {
        int poorCount = findReadingCount(1000, 2000);
        int veryPoorCount = findReadingCount(2000, 5000);
        int criticalCount = findReadingCount(5000, Double.MAX_VALUE);

        return meterName + ":\n"
                + ((poorCount > 0) ? "    " + poorCount + " poor [1000ppm, 2000ppm)\n" : "")
                + ((veryPoorCount > 0) ? "    " + veryPoorCount + " very poor [2000ppm, 5000ppm)\n" : "")
                + ((criticalCount > 0) ? "    " + criticalCount + " critical > 5000ppm\n" : "");
    }

    public String toStringBrokenReadings() {
        int brokenCount = findReadingCount(0);
        return meterName + ": "
                + ((brokenCount > 0) ? brokenCount + " broken readings\n" : "\n");
    }

    public String toStringAverage() {
        return meterName + ": " + Math.round(findAveragePpm() * 100) / 100.0 + " average ppm";
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(meterName).append("\n");
        for (Reading reading : this) {
            stringBuilder.append(reading).append("\n");
        }
        return stringBuilder.toString();
    }
}
