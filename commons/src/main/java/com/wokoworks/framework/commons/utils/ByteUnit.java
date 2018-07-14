package com.wokoworks.framework.commons.utils;

/**
 * 字节单位转换
 *
 * @author 0x0001
 * @date 2018-05-08
 */

public enum ByteUnit {
    /**
     * Byte
     */
    BYTES {
        @Override
        public double toBytes(double d) {
            return d;
        }

        @Override
        public double toKilobytes(double d) {
            return d / Math.pow(1024, 1);
        }

        @Override
        public double toMegabytes(double d) {
            return d / Math.pow(1024, 2);
        }

        @Override
        public double toGigabytes(double d) {
            return d / Math.pow(1024, 3);
        }

        @Override
        public double toTerabytes(double d) {
            return d / Math.pow(1024, 4);
        }

        @Override
        public double toPetabytes(double d) {
            return d / Math.pow(1024, 5);
        }
    },
    /**
     * KB
     */
    KILOBYTES {
        @Override
        public double toBytes(double d) {
            return d * Math.pow(1024, 1);
        }

        @Override
        public double toKilobytes(double d) {
            return d;
        }

        @Override
        public double toMegabytes(double d) {
            return d / Math.pow(1024, 1);
        }

        @Override
        public double toGigabytes(double d) {
            return d / Math.pow(1024, 2);
        }

        @Override
        public double toTerabytes(double d) {
            return d / Math.pow(1024, 3);
        }

        @Override
        public double toPetabytes(double d) {
            return d / Math.pow(1024, 4);
        }
    },
    /**
     * MB
     */
    MEGABYTES {
        @Override
        public double toBytes(double d) {
            return d * Math.pow(1024, 2);
        }

        @Override
        public double toKilobytes(double d) {
            return d * Math.pow(1024, 1);
        }

        @Override
        public double toMegabytes(double d) {
            return d;
        }

        @Override
        public double toGigabytes(double d) {
            return d / Math.pow(1024, 1);
        }

        @Override
        public double toTerabytes(double d) {
            return d / Math.pow(1024, 2);
        }

        @Override
        public double toPetabytes(double d) {
            return d / Math.pow(1024, 3);
        }
    },
    /**
     * GB
     */
    GIGABYTES {
        @Override
        public double toBytes(double d) {
            return d * Math.pow(1024, 3);
        }

        @Override
        public double toKilobytes(double d) {
            return d * Math.pow(1024, 2);
        }

        @Override
        public double toMegabytes(double d) {
            return d * Math.pow(1024, 1);
        }

        @Override
        public double toGigabytes(double d) {
            return d;
        }

        @Override
        public double toTerabytes(double d) {
            return d / Math.pow(1024, 1);
        }

        @Override
        public double toPetabytes(double d) {
            return d / Math.pow(1024, 2);
        }
    },
    /**
     * TB
     */
    TERABYTES {
        @Override
        public double toBytes(double d) {
            return d * Math.pow(1024, 4);
        }

        @Override
        public double toKilobytes(double d) {
            return d * Math.pow(1024, 3);
        }

        @Override
        public double toMegabytes(double d) {
            return d * Math.pow(1024, 2);
        }

        @Override
        public double toGigabytes(double d) {
            return d * Math.pow(1024, 1);
        }

        @Override
        public double toTerabytes(double d) {
            return d;
        }

        @Override
        public double toPetabytes(double d) {
            return d / Math.pow(1024, 1);
        }
    },
    /**
     * PB
     */
    PETABYTES {
        @Override
        public double toBytes(double d) {
            return d * Math.pow(1024, 5);
        }

        @Override
        public double toKilobytes(double d) {
            return d * Math.pow(1024, 4);
        }

        @Override
        public double toMegabytes(double d) {
            return d * Math.pow(1024, 3);
        }

        @Override
        public double toGigabytes(double d) {
            return d * Math.pow(1024, 2);
        }

        @Override
        public double toTerabytes(double d) {
            return d * Math.pow(1024, 1);
        }

        @Override
        public double toPetabytes(double d) {
            return d;
        }
    };


    public abstract double toBytes(double d);
    public abstract double toKilobytes(double d);
    public abstract double toMegabytes(double d);
    public abstract double toGigabytes(double d);
    public abstract double toTerabytes(double d);
    public abstract double toPetabytes(double d);
}
